import cv2
import re
from paddleocr import PaddleOCR

paddle_ocr = PaddleOCR(
    use_angle_cls=True, # need to rotate the image to the correct orientation
    lang='en', # English language model
    show_log=False, # disable logging
    gpu=False # cpu mode
)

def plate_text(p_crop):

    if p_crop is None or p_crop.size == 0:
        return None

    plate_rgb = cv2.cvtColor(p_crop, cv2.COLOR_BGR2RGB)

    results = paddle_ocr.ocr(plate_rgb, cls=True) # get OCR results

    result = results[0] if results else None

    if result and len(result) > 0:
        text = result[0][1][0]  # PaddleOCR extraction format
        cleaned = clean(text) # clean the extracted text
        if cleaned:
            return cleaned


def clean(text: str):
    text = re.sub(r"[^A-Z0-9]", "", text.upper())
    return text if len(text) >= 4 else None