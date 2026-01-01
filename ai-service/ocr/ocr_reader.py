import cv2
import re
from paddleocr import PaddleOCR
from typing import Optional, Dict, List, Tuple

# Initialize PaddleOCR
paddle_ocr = PaddleOCR(
    use_angle_cls=True,
    lang='en',
    show_log=False,
    gpu=False
)

# Plate patterns (adjust for actual format)
PLATE_PATTERNS = [
    r'^[0-9]{1,4}TUN[0-9]{1,4}$',  # 123TUN456
    r'^TUN[0-9]{4,6}$',             # TUN123456
    r'^[A-Z]{2}[0-9]{3,4}$',        # AB1234
]

# Common OCR errors (character confusion)
OCR_CORRECTIONS = {
    'O': '0',  # Letter O → Number 0
    'I': '1',  # Letter I → Number 1
    'l': '1',  # Lowercase L → Number 1
    'S': '5',  # Letter S → Number 5
    'B': '8',  # Letter B → Number 8
    'Z': '2',  # Letter Z → Number 2
    'G': '6',  # Letter G → Number 6
}

# Reverse corrections for letter positions
REVERSE_CORRECTIONS = {
    '0': 'O',
    '1': 'I',
    '5': 'S',
    '8': 'B',
}


class OCRResult:
    """Enhanced OCR result with validation metadata"""
    
    def __init__(
        self,
        plate_number: Optional[str],
        raw_text: str,
        confidence: float,
        validated: bool,
        corrections: List[str] = None,
        errors: List[str] = None
    ):
        self.plate_number = plate_number
        self.raw_text = raw_text
        self.confidence = confidence
        self.validated = validated
        self.corrections = corrections or []
        self.errors = errors or []
    
    def to_dict(self) -> Dict:
        result = {
            "plate_number": self.plate_number,
            "raw_ocr_text": self.raw_text,
            "confidence": round(self.confidence, 3),
            "validated": self.validated
        }
        
        if self.corrections:
            result["corrections_applied"] = self.corrections
        
        if self.errors:
            result["validation_errors"] = self.errors
        
        return result


def read_plate_enhanced(p_crop, min_confidence: float = 0.5) -> OCRResult:
    """
    Enhanced plate reading with validation and error correction
    
    Args:
        p_crop: Cropped plate image
        min_confidence: Minimum confidence threshold
    
    Returns:
        OCRResult with validation metadata
    """
    
    if p_crop is None or p_crop.size == 0:
        return OCRResult(
            plate_number=None,
            raw_text="",
            confidence=0.0,
            validated=False,
            errors=["empty_image"]
        )
    
    # Convert to RGB for PaddleOCR
    plate_rgb = cv2.cvtColor(p_crop, cv2.COLOR_BGR2RGB)
    
    # Run OCR
    results = paddle_ocr.ocr(plate_rgb, cls=True)
    
    if not results or not results[0] or len(results[0]) == 0:
        return OCRResult(
            plate_number=None,
            raw_text="",
            confidence=0.0,
            validated=False,
            errors=["no_text_detected"]
        )
    
    # Extract text and confidence
    detection = results[0][0]
    raw_text = detection[1][0]
    confidence = float(detection[1][1])
    
    # Initial cleaning
    cleaned_text = basic_clean(raw_text)
    
    # Check minimum length
    if len(cleaned_text) < 4:
        return OCRResult(
            plate_number=None,
            raw_text=raw_text,
            confidence=confidence,
            validated=False,
            errors=["too_short"]
        )
    
    # Check confidence threshold
    if confidence < min_confidence:
        return OCRResult(
            plate_number=cleaned_text,
            raw_text=raw_text,
            confidence=confidence,
            validated=False,
            errors=["low_confidence"]
        )
    
    # Apply corrections and validation
    corrected_text, corrections = apply_corrections(cleaned_text)
    
    # Validate against patterns
    is_valid, pattern_matched = validate_plate_format(corrected_text)
    
    if is_valid:
        return OCRResult(
            plate_number=corrected_text,
            raw_text=raw_text,
            confidence=confidence,
            validated=True,
            corrections=corrections
        )
    else:
        # Still return the corrected text but mark as unvalidated
        return OCRResult(
            plate_number=corrected_text,
            raw_text=raw_text,
            confidence=confidence,
            validated=False,
            corrections=corrections,
            errors=["format_mismatch"]
        )


def basic_clean(text: str) -> str:
    """Remove special characters and normalize"""
    # Remove spaces, hyphens, dots
    text = text.replace(" ", "").replace("-", "").replace(".", "")
    # Keep only alphanumeric
    text = re.sub(r"[^A-Z0-9]", "", text.upper())
    return text


def apply_corrections(text: str) -> Tuple[str, List[str]]:
    """
    Apply intelligent OCR error corrections
    
    Strategy:
    - Numbers in expected numeric positions → keep/correct to numbers
    - Letters in expected letter positions → keep/correct to letters
    
    Returns:
        (corrected_text, list_of_corrections)
    """
    corrections = []
    corrected = list(text)
    
    # Detect if this looks like a Tunisian plate (contains TUN)
    if "TUN" in text:
        # Format: 123TUN456
        # Everything before TUN should be numbers
        # Everything after TUN should be numbers
        
        tun_pos = text.find("TUN")
        
        # Correct prefix (before TUN)
        for i in range(tun_pos):
            char = corrected[i]
            if char in OCR_CORRECTIONS:
                old_char = char
                new_char = OCR_CORRECTIONS[char]
                corrected[i] = new_char
                corrections.append(f"{old_char}→{new_char}")
        
        # Correct suffix (after TUN)
        for i in range(tun_pos + 3, len(corrected)):
            char = corrected[i]
            if char in OCR_CORRECTIONS:
                old_char = char
                new_char = OCR_CORRECTIONS[char]
                corrected[i] = new_char
                corrections.append(f"{old_char}→{new_char}")
    
    else:
        # Generic correction: assume numbers are more common
        for i, char in enumerate(corrected):
            if char in OCR_CORRECTIONS:
                old_char = char
                new_char = OCR_CORRECTIONS[char]
                corrected[i] = new_char
                corrections.append(f"{old_char}→{new_char}")
    
    return ''.join(corrected), corrections


def validate_plate_format(text: str) -> Tuple[bool, Optional[str]]:
    """
    Validate plate against known patterns
    
    Returns:
        (is_valid, matched_pattern)
    """
    for pattern in PLATE_PATTERNS:
        if re.match(pattern, text):
            return True, pattern
    
    return False, None


def multi_pass_ocr(p_crop, max_attempts: int = 3) -> OCRResult:
    """
    Perform multiple OCR passes with different preprocessing
    
    Returns best result based on confidence
    """
    if p_crop is None or p_crop.size == 0:
        return OCRResult(
            plate_number=None,
            raw_text="",
            confidence=0.0,
            validated=False,
            errors=["empty_image"]
        )
    
    results = []
    
    # Pass 1: Original image
    result1 = read_plate_enhanced(p_crop)
    results.append(result1)
    
    if result1.validated and result1.confidence > 0.85:
        return result1  # High confidence, no need for more passes
    
    # Pass 2: Contrast enhancement
    if max_attempts >= 2:
        enhanced = enhance_contrast(p_crop)
        result2 = read_plate_enhanced(enhanced)
        results.append(result2)
    
    # Pass 3: Sharpening
    if max_attempts >= 3:
        sharpened = sharpen_image(p_crop)
        result3 = read_plate_enhanced(sharpened)
        results.append(result3)
    
    # Return best result (prioritize validated, then confidence)
    validated_results = [r for r in results if r.validated]
    if validated_results:
        return max(validated_results, key=lambda r: r.confidence)
    
    # No validated results, return highest confidence
    return max(results, key=lambda r: r.confidence)


def enhance_contrast(image):
    """Apply CLAHE (Contrast Limited Adaptive Histogram Equalization)"""
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
    enhanced = clahe.apply(gray)
    return cv2.cvtColor(enhanced, cv2.COLOR_GRAY2BGR)


def sharpen_image(image):
    """Apply sharpening kernel"""
    kernel = np.array([[-1, -1, -1],
                       [-1,  9, -1],
                       [-1, -1, -1]])
    return cv2.filter2D(image, -1, kernel)


# Backward compatibility function
def plate_text(p_crop) -> Optional[str]:
    """
    Legacy function for backward compatibility
    Returns only the plate number or None
    """
    result = read_plate_enhanced(p_crop)
    return result.plate_number if result.validated else None


# For numpy array operations
import numpy as np