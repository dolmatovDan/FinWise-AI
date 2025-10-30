import os
import re
import pandas as pd
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer

from common import read_json_stdin, write_json_stdout, current_month_snapshot, clean_ru

ALLOWED_MODEL_ID = "Qwen/Qwen2.5-0.5B-Instruct"

os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")
try:
    torch.set_num_threads(1)
except Exception:
    pass

_DEVICE = torch.device("cpu")
_tokenizer = None
_model = None
_loaded = False


def _load():
    global _tokenizer, _model, _loaded
    if _loaded and _tokenizer is not None and _model is not None:
        return _tokenizer, _model

    _tokenizer = AutoTokenizer.from_pretrained(
        ALLOWED_MODEL_ID,
        trust_remote_code=True,
    )
    _model = AutoModelForCausalLM.from_pretrained(
        ALLOWED_MODEL_ID,
        torch_dtype=torch.float32,
        low_cpu_mem_usage=True,
        trust_remote_code=True,
    ).to(_DEVICE).eval()

    if _tokenizer.pad_token_id is None:
        _tokenizer.pad_token_id = _tokenizer.eos_token_id

    _loaded = True
    return _tokenizer, _model


def _gen(messages, tok, mdl, max_new_tokens=200, det=True):
    txt = tok.apply_chat_template(
        messages, tokenize=False, add_generation_prompt=True
    )
    inputs = tok(
        txt,
        return_tensors="pt",
        padding=True,
        truncation=True,
        max_length=1400,
    ).to(_DEVICE)

    with torch.no_grad():
        common = dict(
            max_new_tokens=max_new_tokens,
            repetition_penalty=1.08 if det else 1.12,
            no_repeat_ngram_size=5 if det else 6,
            eos_token_id=tok.eos_token_id,
            pad_token_id=tok.pad_token_id,
        )
        if det:
            out = mdl.generate(
                **inputs,
                do_sample=False,
                num_beams=4,
                **common,
            )
        else:
            out = mdl.generate(
                **inputs,
                do_sample=True,
                temperature=0.8,
                top_p=0.9,
                top_k=50,
                **common,
            )
    return tok.decode(out[0], skip_special_tokens=True)


_BULLET_KILL = re.compile(
    r"(?i)(учитывай данные|данные пользователя|месяц:|доход:|расход:|нетто:|топ стат|вопрос:|assistant)"
)
_ONLY_PUNCT = re.compile(r"^[-•\s\.\,\;\:\!\?]+$")


def _to_bullets(text: str) -> str:
    if not text:
        return ""
    m = re.search(r"(\n\s*[-*]\s+|\n\s*\d+[\).\s]+|•)", "\n" + text)
    if m:
        text = text[m.start() :]

    text = re.sub(r"^\s*[*•]\s+", "- ", text, flags=re.M)
    text = re.sub(r"^\s*\d+[\).\s]+", "- ", text, flags=re.M)

    uniq, seen = [], set()
    for ln in text.split("\n"):
        s = ln.strip()
        if not s or not s.startswith("- "):
            continue
        if _BULLET_KILL.search(s) or _ONLY_PUNCT.match(s):
            continue
        s = re.sub(r"\s{2,}", " ", s)
        s = re.sub(r"\.\s*\.+$", ".", s)
        key = s.lower()
        if key in seen:
            continue
        seen.add(key)
        uniq.append(s)
        if len(uniq) >= 7:
            break

    return "\n".join(s.replace("- ", "• ", 1) for s in uniq)


def main():
    req = read_json_stdin()
    tx = req.get("transactions") or []
    question = (req.get("question") or "").strip()
    
    df = pd.DataFrame(tx) if tx else None
    snap = current_month_snapshot(df) if df is not None and not df.empty else {}

    if snap:
        ctx = [
            f"Месяц: {snap['month']}",
            f"Доход: {snap['income_total']:.0f}",
            f"Расход: {abs(snap['expense_total']):.0f}",
            f"Нетто: {snap['net']:.0f}",
        ]
        if snap.get("top_expense_categories"):
            ctx.append("Топ статей расходов:")
            for cat, val in snap["top_expense_categories"]:
                ctx.append(f"- {cat}: {abs(val):.0f}")
        context = "\n".join(ctx)
    else:
        context = "Данных за текущий месяц нет."

    system_msg = (
        "Ты финансовый помощник. Отвечай по-русски. "
        "Верни ТОЛЬКО список из 5–7 конкретных шагов экономии с цифрами (лимиты, проценты, частота). "
        "Каждая строка должна начинаться с символов \"- \". Никаких вступлений."
    )
    messages = [
        {"role": "system", "content": system_msg},
        {
            "role": "user",
            "content": (
                f"Мои данные за текущий месяц:\n{context}\n\nВопрос: {question}\n"
                "Начни ответ сразу со строки, которая начинается с \"- \". Верни только список."
            ),
        },
    ]

    tok, mdl = _load()
    raw = _gen(messages, tok, mdl, det=True)
    text = _to_bullets(clean_ru(raw))

    if text.count("\n") + 1 < 3:
        raw2 = _gen(messages, tok, mdl, det=False)
        text2 = _to_bullets(clean_ru(raw2))
        if text2:
            text = text2

    write_json_stdout({"advice": text})


if __name__ == "__main__":
    main()
