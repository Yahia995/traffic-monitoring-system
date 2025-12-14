from fastapi import FastAPI, UploadFile, File, HTTPException, CORSMiddleware
from pathlib import Path
import cv2
import tempfile

from detectors.vehicle_detector import VehicleDetector
from detectors.plate_detector import PlateDetector
from tracker.centroid_tracker import CentroidTracker
from ocr.ocr_reader import plate_text
from utils.config import *
from utils.pre_process import safe_crop
from utils.speed_estimator import calculate_speed

app = FastAPI(title="AI Traffic Detection MVP")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"]
)

vehicle_detector = VehicleDetector()
plate_detector = PlateDetector()
tracker = CentroidTracker()

@app.get("/api/health")
def health_check():
    return {"status": "OK"}

@app.post("/api/process-video")
async def process_video(video : UploadFile = File()):
    # Validate video format
    if not video.filename.endswith(ALLOWED_EXT):
        raise HTTPException(400, "Invalid video format")
    
    # Save uploaded video to a temporary file
    suffix = Path(video.filename).suffix
    tmp = tempfile.NamedTemporaryFile(suffix=suffix, delete=False)
    while True:
        chunk = video.file.read(1024 ** 2)

        if not chunk:
            break

        tmp.write(chunk)
    tmp.close()

    # Process video frames
    cap = cv2.VideoCapture(tmp.name)


    frame_id = 0 # Frame counter
    fps = cap.get(cv2.CAP_PROP_FPS) # Video frames per second
    tracked = {} # vehicle_id : {first_frame, last_frame, positions[(cX, cY), ...], plate}

    # Read frames from video
    while True:
        ret , frame = cap.read() # ret indicates if frame is read correctly
        if not ret:
            break

        frame_id += 1 # Increment frame counter

        if FRAME_SKIP > 0 and frame_id % (FRAME_SKIP + 1) == 0:
            continue

        print(f"Processing frame {frame_id}")

        # Detect vehicles in the frame
        rects = vehicle_detector.detect(frame) # rects = [(x1, y1, x2, y2), ...]

        # Update tracker with detected bounding boxes
        vehicles = tracker.update(rects) # vehicles = {vehicle_id: (x1, y1, x2, y2), ...}

        print(f"Tracked {len(vehicles)} vehicles -> IDs: {list(vehicles.keys())}")

        # Update tracked vehicles information
        for vehicle_id, bbox in vehicles.items():
            x1, y1, x2, y2 = bbox
            
            cX, cY = (x1 + x2) // 2, (y1 + y2) // 2 # Centroid: center of the bounding box

            if vehicle_id not in tracked:
                # First time seeing this vehicle
                tracked[vehicle_id] = {
                    "first_frame": frame_id,
                    "last_frame": frame_id,
                    "positions": [(cX, cY)],
                    "plate": None
                }
            else:
                # Update existing vehicle info
                tracked[vehicle_id]["last_frame"] = frame_id
                tracked[vehicle_id]["positions"].append((cX, cY))

            if tracked[vehicle_id]["plate"] is None:
                v_crop = safe_crop(frame,bbox)
                if v_crop is None:
                    continue

                p_box = plate_detector.detect(v_crop)
                if p_box:
                    p_crop = safe_crop(v_crop,p_box)
                    if p_crop is not None:
                        tracked[vehicle_id]["plate"] = plate_text(p_crop)

    
    cap.release() # Release video capture object
    Path(tmp.name).unlink()  # Delete temporary video file

    violations = {}

    for vehicle_id, info in tracked.items():
        plate = info["plate"]
        positions_nbr = len(info["positions"])
        if plate and positions_nbr >= MIN_TRACKED_FRAMES:
            speed = calculate_speed(info["first_frame"],info["last_frame"], info["positions"], fps)
            if speed > SPEED_LIMIT:
                violations[plate] = {
                    "speed": speed,
                    "speed_limit": SPEED_LIMIT,
                    "timestamp": info["first_frame"] / fps
                }
    
    return {
        "violations_nbr": len(violations),       
        "violations": violations,
        "details" : tracked
    }