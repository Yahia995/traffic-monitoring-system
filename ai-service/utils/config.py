import os
from typing import Tuple

# Model Paths
VEHICLE_MODEL_PATH = os.getenv("VEHICLE_MODEL_PATH", "models/vehicle_yolo.pt")
PLATE_MODEL_PATH = os.getenv("PLATE_MODEL_PATH", "models/plate_yolo.pt")

# Speed / Physics Calibration
PIXEL_TO_METER = float(os.getenv("PIXEL_TO_METER", "0.05"))
SPEED_LIMIT = float(os.getenv("SPEED_LIMIT", "50.0"))

# Processing Settings
FRAME_SKIP = int(os.getenv("FRAME_SKIP", "1"))
MIN_TRACKED_FRAMES = int(os.getenv("MIN_TRACKED_FRAMES", "8"))

# Detection Confidence Thresholds
VEHICLE_CONFIDENCE = float(os.getenv("VEHICLE_CONFIDENCE", "0.35"))
PLATE_CONFIDENCE = float(os.getenv("PLATE_CONFIDENCE", "0.25"))
OCR_CONFIDENCE = float(os.getenv("OCR_CONFIDENCE", "0.5"))

# Tracking Settings
MAX_DISAPPEARED = int(os.getenv("MAX_DISAPPEARED", "60"))
MAX_DISTANCE = float(os.getenv("MAX_DISTANCE", "70.0"))

# File Upload Settings
MAX_UPLOAD_MB = int(os.getenv("MAX_UPLOAD_MB", "200"))
ALLOWED_EXT = (".mp4", ".avi", ".mov", ".mkv")

# OCR Enhancement Settings
OCR_MULTI_PASS = os.getenv("OCR_MULTI_PASS", "true").lower() == "true"
OCR_MAX_ATTEMPTS = int(os.getenv("OCR_MAX_ATTEMPTS", "3"))

# Response Format Settings
INCLUDE_TRAJECTORY = os.getenv("INCLUDE_TRAJECTORY", "true").lower() == "true"
TRAJECTORY_SAMPLING = int(os.getenv("TRAJECTORY_SAMPLING", "10"))  # Every N frames


def get_violation_severity(overspeed_kmh: float) -> str:
    """
    Classify violation severity based on overspeed amount
    
    Args:
        overspeed_kmh: Amount over speed limit
    
    Returns:
        Severity level: "low", "medium", "high", "critical"
    """
    if overspeed_kmh < 10:
        return "low"
    elif overspeed_kmh < 20:
        return "medium"
    elif overspeed_kmh < 30:
        return "high"
    else:
        return "critical"


def validate_configuration() -> Tuple[bool, list]:
    """
    Validate configuration values
    
    Returns:
        (is_valid, list_of_errors)
    """
    errors = []
    
    # Check model files exist
    if not os.path.exists(VEHICLE_MODEL_PATH):
        errors.append(f"Vehicle model not found: {VEHICLE_MODEL_PATH}")
    
    if not os.path.exists(PLATE_MODEL_PATH):
        errors.append(f"Plate model not found: {PLATE_MODEL_PATH}")
    
    # Validate ranges
    if PIXEL_TO_METER <= 0:
        errors.append(f"PIXEL_TO_METER must be positive, got {PIXEL_TO_METER}")
    
    if SPEED_LIMIT <= 0:
        errors.append(f"SPEED_LIMIT must be positive, got {SPEED_LIMIT}")
    
    if MIN_TRACKED_FRAMES < 2:
        errors.append(f"MIN_TRACKED_FRAMES must be >= 2, got {MIN_TRACKED_FRAMES}")
    
    if not (0 < VEHICLE_CONFIDENCE < 1):
        errors.append(f"VEHICLE_CONFIDENCE must be in (0,1), got {VEHICLE_CONFIDENCE}")
    
    if not (0 < PLATE_CONFIDENCE < 1):
        errors.append(f"PLATE_CONFIDENCE must be in (0,1), got {PLATE_CONFIDENCE}")
    
    if not (0 < OCR_CONFIDENCE < 1):
        errors.append(f"OCR_CONFIDENCE must be in (0,1), got {OCR_CONFIDENCE}")
    
    if MAX_DISAPPEARED < 1:
        errors.append(f"MAX_DISAPPEARED must be >= 1, got {MAX_DISAPPEARED}")
    
    if MAX_DISTANCE <= 0:
        errors.append(f"MAX_DISTANCE must be positive, got {MAX_DISTANCE}")
    
    return len(errors) == 0, errors


def get_config_dict() -> dict:
    """
    Get current configuration as dictionary for API responses
    """
    return {
        "speed_limit_kmh": SPEED_LIMIT,
        "pixel_to_meter": PIXEL_TO_METER,
        "min_tracked_frames": MIN_TRACKED_FRAMES,
        "frame_skip": FRAME_SKIP,
        "vehicle_confidence": VEHICLE_CONFIDENCE,
        "plate_confidence": PLATE_CONFIDENCE,
        "ocr_confidence": OCR_CONFIDENCE,
        "max_disappeared": MAX_DISAPPEARED,
        "max_distance": MAX_DISTANCE,
        "ocr_multi_pass": OCR_MULTI_PASS,
        "ocr_max_attempts": OCR_MAX_ATTEMPTS
    }


def log_configuration(logger):
    """Log current configuration (for debugging)"""
    logger.info("=" * 50)
    logger.info("AI-Service Configuration v1.5")
    logger.info("=" * 50)
    logger.info(f"Models:")
    logger.info(f"  Vehicle: {VEHICLE_MODEL_PATH}")
    logger.info(f"  Plate:   {PLATE_MODEL_PATH}")
    logger.info(f"Speed:")
    logger.info(f"  Limit:         {SPEED_LIMIT} km/h")
    logger.info(f"  Calibration:   {PIXEL_TO_METER} m/pixel")
    logger.info(f"Processing:")
    logger.info(f"  Frame skip:    {FRAME_SKIP}")
    logger.info(f"  Min frames:    {MIN_TRACKED_FRAMES}")
    logger.info(f"Confidence Thresholds:")
    logger.info(f"  Vehicle:       {VEHICLE_CONFIDENCE}")
    logger.info(f"  Plate:         {PLATE_CONFIDENCE}")
    logger.info(f"  OCR:           {OCR_CONFIDENCE}")
    logger.info(f"Tracking:")
    logger.info(f"  Max disappeared: {MAX_DISAPPEARED}")
    logger.info(f"  Max distance:    {MAX_DISTANCE}")
    logger.info(f"OCR Enhancement:")
    logger.info(f"  Multi-pass:    {OCR_MULTI_PASS}")
    logger.info(f"  Max attempts:  {OCR_MAX_ATTEMPTS}")
    logger.info("=" * 50)