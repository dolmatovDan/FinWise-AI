import sys
import os
import json
import re
import torch
import numpy as np
from PIL import Image
from transformers import DonutProcessor, VisionEncoderDecoderModel

#os.environ["TRANSFORMERS_CACHE"] = "/tmp"
#os.environ["HF_HOME"] = "/tmp"

MODEL_ID = "naver-clova-ix/donut-base-finetuned-cord-v2"
device = "cuda" if torch.cuda.is_available() else "cpu"

processor = DonutProcessor.from_pretrained(MODEL_ID)
model = VisionEncoderDecoderModel.from_pretrained(MODEL_ID).to(device)

def pick_total_from_text(text: str):
    if not text:
        return None
    text = text.replace("\xa0", " ")
    def _to_float(s):
        s = s.replace(" ", "").replace(",", ".")
        try: return float(s)
        except: return None
    eq_matches = re.findall(r"=\s*(-?\d{1,3}(?:[ .,\u00A0]?\d{3})*(?:[.,]\d{2}))", text)
    for m in reversed(eq_matches):
        v = _to_float(m)
        if v and v > 0: return v
    matches = re.findall(r"(-?\d{1,3}(?:[ .,\u00A0]?\d{3})*(?:[.,]\d{2}))", text)
    best = None
    for m in matches:
        v = _to_float(m)
        if v and 0 < v < 1e6:
            best = v
    return best

def extract_total(image_path: str):
    image = Image.open(image_path).convert("RGB")
    task_prompt = "<s_cord-v2>"
    pixel_values = processor(image, return_tensors="pt").pixel_values.to(device)
    decoder_input_ids = processor.tokenizer(task_prompt, add_special_tokens=False, return_tensors="pt").input_ids.to(device)
    outputs = model.generate(
        pixel_values,
        decoder_input_ids=decoder_input_ids,
        max_length=model.config.decoder.max_position_embeddings,
        early_stopping=True,
        pad_token_id=processor.tokenizer.pad_token_id,
        eos_token_id=processor.tokenizer.eos_token_id,
        use_cache=True,
        num_beams=1,
    )
    seq = processor.batch_decode(outputs, skip_special_tokens=True)[0]
    seq = seq.replace(task_prompt, "").replace("<s>", "").replace("</s>", "").strip()
    try:
        data = json.loads(seq)
        for k in ["total", "total_price", "grand_total"]:
            if k in data:
                return float(str(data[k]).replace(",", "."))
    except Exception:
        pass
    return pick_total_from_text(seq)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: receipt_total_api.py path/to/receipt.jpg", file=sys.stderr)
        sys.exit(1)
    total = extract_total(sys.argv[1])
    print(total if total is not None else "null")
