import os, re
import pandas as pd
from transformers import AutoModelForCausalLM, AutoTokenizer
import torch

from common import read_json_stdin, write_json_stdout, current_month_snapshot, clean_ru

_DEVICE = torch.device("cpu")
_tokenizer = None
_model = None
_loaded_id = None

def _load(model_id: str):
    global _tokenizer, _model, _loaded_id
    if _loaded_id == model_id and _tokenizer is not None:
        return _tokenizer, _model
    token = os.getenv("HF_TOKEN") or os.getenv("HUGGINGFACEHUB_API_TOKEN") or None
    needs = model_id.startswith("meta-llama/")
    if needs and not token:
        raise RuntimeError("HF_TOKEN is required for meta-llama models")
    _tokenizer = AutoTokenizer.from_pretrained(model_id, token=token or None)
    _model = AutoModelForCausalLM.from_pretrained(
        model_id, token=token or None, torch_dtype=torch.float32, low_cpu_mem_usage=True
    ).to(_DEVICE).eval()
    if _tokenizer.pad_token_id is None:
        _tokenizer.pad_token_id = _tokenizer.eos_token_id
    _loaded_id = model_id
    return _tokenizer, _model

def _gen(messages, tok, mdl, max_new_tokens=240, det=True):
    txt = tok.apply_chat_template(messages, tokenize=False, add_generation_prompt=True)
    inputs = tok(txt, return_tensors="pt", padding=True, truncation=True, max_length=1400).to(_DEVICE)
    with torch.no_grad():
        if det:
            out = mdl.generate(**inputs, max_new_tokens=max_new_tokens, do_sample=False,
                               num_beams=4, repetition_penalty=1.08, no_repeat_ngram_size=5,
                               eos_token_id=tok.eos_token_id, pad_token_id=tok.pad_token_id)
        else:
            out = mdl.generate(**inputs, max_new_tokens=max_new_tokens, do_sample=True,
                               temperature=0.8, top_p=0.9, top_k=50,
                               repetition_penalty=1.15, no_repeat_ngram_size=6,
                               eos_token_id=tok.eos_token_id, pad_token_id=tok.pad_token_id)
    return tok.decode(out[0], skip_special_tokens=True)

def _to_bullets(text: str) -> str:
    if not text:
        return ""
    m = re.search(r"(\n\s*[-*]\s+|\n\s*\d+[\).\s]+|•)", "\n"+text)
    if m: text = text[m.start():]
    text = re.sub(r"^\s*[*•]\s+", "- ", text, flags=re.M)
    text = re.sub(r"^\s*\d+[\).\s]+", "- ", text, flags=re.M)
    kill = re.compile(r"(?i)(учитывай данные|данные пользователя|месяц:|доход:|расход:|нетто:|топ стат|вопрос:|assistant)")
    only_punct = re.compile(r"^[-•\s\.\,\;\:\!\?]+$")
    lines, uniq, seen = [], [], set()
    for ln in text.split("\n"):
        s = ln.strip()
        if not s or not s.startswith("- "): continue
        if kill.search(s) or only_punct.match(s): continue
        s = re.sub(r"\s{2,}", " ", s)
        s = re.sub(r"\.\s*\.+$", ".", s)
        key = s.lower()
        if key in seen: continue
        seen.add(key); uniq.append(s)
    uniq = uniq[:7]
    return "\n".join(s.replace("- ", "• ", 1) for s in uniq)

def main():
    req = read_json_stdin()
    tx = req.get("transactions") or []
    question = (req.get("question") or "").strip()
    model_id = req.get("model") or "Qwen/Qwen2.5-0.5B-Instruct"

    df = pd.DataFrame(tx) if tx else None
    snap = current_month_snapshot(df) if df is not None and not df.empty else {}
    if snap:
        ctx = [f"Месяц: {snap['month']}",
               f"Доход: {snap['income_total']:.0f}",
               f"Расход: {abs(snap['expense_total']):.0f}",
               f"Нетто: {snap['net']:.0f}"]
        if snap.get("top_expense_categories"):
            ctx.append("Топ статей расходов:")
            for cat,val in snap["top_expense_categories"]:
                ctx.append(f"- {cat}: {abs(val):.0f}")
        context = "\n".join(ctx)
    else:
        context = "Данных за текущий месяц нет."

    system_msg = ("Ты финансовый помощник. Отвечай по-русски. "
                  "Верни ТОЛЬКО список из 5–7 конкретных шагов экономии с цифрами (лимиты, проценты, частота). "
                  "Каждая строка должна начинаться с символов \"- \". Никаких вступлений.")
    messages = [
        {"role":"system","content":system_msg},
        {"role":"user","content": f"Мои данные за текущий месяц:\n{context}\n\nВопрос: {question}\n"
                                  "Начни ответ сразу со строки, которая начинается с \"- \". Верни только список."}
    ]

    tok, mdl = _load(model_id)
    raw = _gen(messages, tok, mdl, det=True)
    text = _to_bullets(clean_ru(raw))
    if text.count("\n")+1 < 3: 
        raw2 = _gen(messages, tok, mdl, det=False)
        text2 = _to_bullets(clean_ru(raw2))
        if text2: text = text2
    write_json_stdout({"advice": text})

if __name__ == "__main__":
    main()
