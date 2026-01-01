from fastapi import FastAPI, UploadFile, File, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from pathlib import Path
import cv2
import tempfile
import logging
import time
import uuid
from typing import Dict, List, Optional

from detectors.vehicle_detector import VehicleDetector
from detectors.plate_detector import PlateDetector
from tracker.centroid_tracker import CentroidTracker
from ocr.ocr_reader import read_plate_enhanced, multi_pass_ocr, OCRResult
from utils.config import *
from utils.pre_process import safe_crop
from utils.speed_estimator import calculate_speed

# Application Setup
app = FastAPI(
    title="AI Traffic Detection Service",
    version="2.0.0",
    description="API service for processing traffic videos and detecting overspeeding vehicles."
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"]
)

# Logging Setup
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)-8s | %(name)-15s | %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
)
logger = logging.getLogger("ai-service")
logger.setLevel(logging.DEBUG)
logger.propagate = True

# Initialize Models
vehicle_detector = VehicleDetector()
plate_detector = PlateDetector()
tracker = CentroidTracker()

# Validate and log configuration
config_valid, config_errors = validate_configuration()
if not config_valid:
    logger.error("Configuration validation failed:")
    for error in config_errors:
        logger.error(f"  - {error}")
    raise RuntimeError("Invalid configuration")

log_configuration(logger)


# Helper Functions
def sample_trajectory(positions: List[tuple], sampling_rate: int) -> List[Dict]:
    """Sample trajectory points for compact response"""
    if not INCLUDE_TRAJECTORY:
        return []
    
    sampled = []
    for i, (frame, pos) in enumerate(positions):
        if i % sampling_rate == 0 or i == len(positions) - 1:
            sampled.append({
                "frame": frame,
                "x": pos[0],
                "y": pos[1]
            })
    
    return sampled


def calculate_trajectory_length(positions: List[tuple]) -> float:
    """Calculate total trajectory length in pixels"""
    if len(positions) < 2:
        return 0.0
    
    total_distance = 0.0
    for i in range(1, len(positions)):
        x1, y1 = positions[i-1][1]
        x2, y2 = positions[i][1]
        distance = ((x2 - x1) ** 2 + (y2 - y1) ** 2) ** 0.5
        total_distance += distance
    
    return round(total_distance, 2)


def build_violation_record(
    violation_id: str,
    plate_info: Dict,
    speed_kmh: float,
    timestamp_seconds: float,
    frame_number: int
) -> Dict:
    """Build a violation record in the new format"""
    overspeed = round(speed_kmh - SPEED_LIMIT, 2)
    severity = get_violation_severity(overspeed)
    
    return {
        "violation_id": violation_id,
        "plate_number": plate_info["plate_number"],
        "plate_confidence": plate_info["confidence"],
        "plate_validated": plate_info["validated"],
        "speed_kmh": speed_kmh,
        "speed_limit_kmh": SPEED_LIMIT,
        "overspeed_kmh": overspeed,
        "timestamp_seconds": round(timestamp_seconds, 2),
        "frame_number": frame_number,
        "severity": severity
    }


def build_vehicle_record(
    vehicle_id: str,
    tracked_info: Dict,
    ocr_result: Optional[OCRResult],
    speed_kmh: float,
    fps: float
) -> Dict:
    """Build a tracked vehicle record in the new format"""
    
    first_frame = tracked_info["first_frame"]
    last_frame = tracked_info["last_frame"]
    positions_with_frames = [
        (first_frame + i, pos) 
        for i, pos in enumerate(tracked_info["positions"])
    ]
    
    trajectory_length = calculate_trajectory_length(positions_with_frames)
    
    # Build vehicle record
    vehicle = {
        "vehicle_id": vehicle_id,
        "tracking_info": {
            "first_frame": first_frame,
            "last_frame": last_frame,
            "frames_tracked": last_frame - first_frame + 1,
            "trajectory_length_pixels": trajectory_length
        },
        "speed_info": {
            "speed_kmh": speed_kmh,
            "is_violation": speed_kmh > SPEED_LIMIT,
            "calculation_valid": len(tracked_info["positions"]) >= MIN_TRACKED_FRAMES
        }
    }
    
    # Add plate information
    if ocr_result:
        vehicle["plate_info"] = ocr_result.to_dict()
        vehicle["plate_info"]["detection_frame"] = tracked_info.get("plate_detected_frame")
    else:
        vehicle["plate_info"] = {
            "plate_number": None,
            "raw_ocr_text": "",
            "confidence": 0.0,
            "validated": False,
            "validation_errors": ["not_detected"]
        }
    
    # Add sampled trajectory
    if INCLUDE_TRAJECTORY:
        vehicle["positions"] = sample_trajectory(
            positions_with_frames,
            TRAJECTORY_SAMPLING
        )
    
    return vehicle


# API Endpoints
@app.get("/health")
def health_check():
    """Health check endpoint"""
    return {
        "status": "OK",
        "version": "1.5.0",
        "config_valid": config_valid
    }


@app.get("/config")
def get_configuration():
    """Get current configuration"""
    return get_config_dict()


@app.post("/api/process-video")
async def process_video(request: Request, video: UploadFile = File(...)):    
    # Extract correlation ID from headers
    correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
    
    start_time = time.time()
    
    logger.info(
        "[%s] ▶ Received video: filename='%s' content_type='%s'",
        correlation_id,
        video.filename,
        video.content_type
    )
    
    # Validate video format
    if not video.filename.endswith(ALLOWED_EXT):
        logger.warning(
            "[%s] ❌ Invalid format: '%s' (allowed: %s)",
            correlation_id,
            video.filename,
            ALLOWED_EXT
        )
        raise HTTPException(400, f"Invalid video format. Allowed: {ALLOWED_EXT}")
    
    # Save uploaded video
    suffix = Path(video.filename).suffix
    tmp = tempfile.NamedTemporaryFile(suffix=suffix, delete=False)
    
    file_size = 0
    while True:
        chunk = video.file.read(1024 ** 2)  # 1MB chunks
        if not chunk:
            break
        tmp.write(chunk)
        file_size += len(chunk)
    
    tmp.close()
    size_mb = round(file_size / 1024 / 1024, 2)
    
    logger.info("[%s] 💾 Saved to '%s' (%.2f MB)", correlation_id, tmp.name, size_mb)
    
    # Open video
    cap = cv2.VideoCapture(tmp.name)
    fps = cap.get(cv2.CAP_PROP_FPS)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    duration = total_frames / fps if fps > 0 else 0
    
    logger.info(
        "[%s] 📹 Video info: fps=%.1f total_frames=%d duration=%.1fs",
        correlation_id, fps, total_frames, duration
    )
    
    # Processing state
    frame_id = 0
    processed_frames = 0
    tracked = {}  # vehicle_id -> tracking info
    ocr_results = {}  # vehicle_id -> OCRResult
    
    # Process frames
    logger.info("[%s] 🔄 Starting frame processing...", correlation_id)
    
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        
        frame_id += 1
        
        # Frame skipping
        if FRAME_SKIP > 0 and frame_id % (FRAME_SKIP + 1) != 0:
            continue
        
        processed_frames += 1
        
        # Detect vehicles
        rects = vehicle_detector.detect(frame)
        
        # Update tracker
        vehicles = tracker.update(rects)
        
        if processed_frames % 100 == 0:
            logger.debug(
                "[%s] Frame %d/%d → detected=%d tracked=%d",
                correlation_id, frame_id, total_frames, len(rects), len(vehicles)
            )
        
        # Update tracked vehicles
        for vehicle_id, bbox in vehicles.items():
            x1, y1, x2, y2 = bbox
            cX, cY = (x1 + x2) // 2, (y1 + y2) // 2
            
            # Initialize tracking info
            if vehicle_id not in tracked:
                tracked[vehicle_id] = {
                    "first_frame": frame_id,
                    "last_frame": frame_id,
                    "positions": [(cX, cY)],
                    "plate_detected_frame": None
                }
            else:
                tracked[vehicle_id]["last_frame"] = frame_id
                tracked[vehicle_id]["positions"].append((cX, cY))
            
            # Try OCR if not yet done
            if vehicle_id not in ocr_results:
                v_crop = safe_crop(frame, bbox)
                if v_crop is None:
                    continue
                
                p_box = plate_detector.detect(v_crop)
                if p_box:
                    p_crop = safe_crop(v_crop, p_box)
                    if p_crop is not None:
                        # Use multi-pass OCR if enabled
                        if OCR_MULTI_PASS:
                            ocr_result = multi_pass_ocr(p_crop, OCR_MAX_ATTEMPTS)
                        else:
                            ocr_result = read_plate_enhanced(p_crop, OCR_CONFIDENCE)
                        
                        ocr_results[vehicle_id] = ocr_result
                        tracked[vehicle_id]["plate_detected_frame"] = frame_id
                        
                        if ocr_result.plate_number:
                            logger.debug(
                                "[%s] 🔍 Vehicle %s → Plate '%s' (conf=%.2f, valid=%s)",
                                correlation_id,
                                vehicle_id,
                                ocr_result.plate_number,
                                ocr_result.confidence,
                                ocr_result.validated
                            )
    
    cap.release()
    Path(tmp.name).unlink()
    
    logger.info("[%s] ✅ Frame processing complete", correlation_id)
    
    # Build Response
    violations = []
    tracked_vehicles = []
    violation_counter = 1
    total_speeds = []
    
    for vehicle_id, info in tracked.items():
        ocr_result = ocr_results.get(vehicle_id)
        
        # Calculate speed
        positions_count = len(info["positions"])
        if positions_count >= MIN_TRACKED_FRAMES:
            speed_kmh = calculate_speed(
                info["first_frame"],
                info["last_frame"],
                info["positions"],
                fps
            )
            total_speeds.append(speed_kmh)
        else:
            speed_kmh = 0.0
        
        # Build vehicle record
        vehicle_record = build_vehicle_record(
            f"veh_{vehicle_id:03d}",
            info,
            ocr_result,
            speed_kmh,
            fps
        )
        
        tracked_vehicles.append(vehicle_record)
        
        # Check for violation
        if (ocr_result and 
            ocr_result.plate_number and 
            positions_count >= MIN_TRACKED_FRAMES and
            speed_kmh > SPEED_LIMIT):
            
            violation = build_violation_record(
                f"v_{violation_counter:03d}",
                vehicle_record["plate_info"],
                speed_kmh,
                info["first_frame"] / fps,
                info["first_frame"]
            )
            
            violations.append(violation)
            violation_counter += 1
    
    # Calculate statistics
    vehicles_with_plates = sum(
        1 for v in tracked_vehicles 
        if v["plate_info"]["plate_number"] is not None
    )
    
    avg_speed = round(sum(total_speeds) / len(total_speeds), 2) if total_speeds else 0.0
    
    processing_time = time.time() - start_time
    
    logger.info(
        "[%s] 📤 Results: vehicles=%d plates=%d violations=%d time=%.1fs",
        correlation_id,
        len(tracked_vehicles),
        vehicles_with_plates,
        len(violations),
        processing_time
    )
    
    # Build final response
    response = {
        "status": "success",
        "processing_time_seconds": round(processing_time, 2),
        "video_info": {
            "filename": video.filename,
            "duration_seconds": round(duration, 2),
            "fps": round(fps, 1),
            "total_frames": total_frames,
            "processed_frames": processed_frames
        },
        "summary": {
            "total_vehicles_tracked": len(tracked_vehicles),
            "vehicles_with_plates": vehicles_with_plates,
            "violations_detected": len(violations),
            "average_speed_kmh": avg_speed
        },
        "violations": violations,
        "tracked_vehicles": tracked_vehicles,
        "configuration": get_config_dict()
    }
    
    return response


@app.get("/")
def root():
    """Root endpoint with API information"""
    return {
        "service": "AI Traffic Detection",
        "version": "2.0.0",
        "status": "operational",
        "endpoints": {
            "health": "/health",
            "config": "/config",
            "process": "/api/process-video",
            "docs": "/docs"
        }
    }