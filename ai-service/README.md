# 🚦 AI-Service — Traffic Violation Detection

**Current Version**: v1.5 (Stabilization) ✅  
**Next Version**: v2.0 (Real-world Calibration & Database) 🚧

This module represents the **Artificial Intelligence (AI) service** of the Traffic Monitoring System. It analyzes traffic videos to detect vehicles, recognize license plates with enhanced OCR, track vehicles, estimate speed, and identify traffic violations with severity classification and validation.

---

## 🆕 What's New in v1.5

### Enhanced OCR System
- ✅ **Multi-pass OCR Processing**: Up to 3 attempts with different preprocessing
- ✅ **Automatic Error Correction**: Fix common OCR mistakes (I→1, O→0, etc.)
- ✅ **Pattern-based Validation**: Validate plates against expected formats
- ✅ **Confidence Tracking**: Track OCR confidence scores
- ✅ **Preprocessing Enhancement**: Contrast enhancement and sharpening

### Improved Response Format
- ✅ **Semantic Arrays**: Violations and vehicles as arrays (not objects)
- ✅ **Comprehensive Metadata**: Full tracking and confidence information
- ✅ **Severity Classification**: Low/Medium/High/Critical violation levels
- ✅ **Validation Status**: Track if plates are validated against patterns
- ✅ **Processing Statistics**: Complete video and processing metadata

### Enhanced Configuration
- ✅ **Environment Variables**: Full configuration via environment
- ✅ **Validation System**: Automatic config validation on startup
- ✅ **Flexible Thresholds**: Adjustable confidence and tracking parameters
- ✅ **OCR Control**: Toggle multi-pass and set max attempts

### Better Error Handling
- ✅ **Detailed Logging**: Structured logging with clear progression
- ✅ **Error Tracking**: OCR errors tracked per vehicle
- ✅ **Graceful Degradation**: Continue processing even with OCR failures
- ✅ **Configuration Validation**: Validate settings before processing

---

## 📌 Current Capabilities (v1.5)

### ✅ Implemented Features
- ✅ **Offline video processing**: Analyzes pre-recorded video files
- ✅ **Vehicle detection**: YOLOv8-based detection
- ✅ **License plate detection**: YOLOv8 custom model
- ✅ **Enhanced OCR**: Multi-pass PaddleOCR with validation
- ✅ **Automatic correction**: Fix common OCR errors
- ✅ **Pattern validation**: Validate plates against known formats
- ✅ **Vehicle tracking**: Centroid-based tracking algorithm
- ✅ **Speed estimation**: Pixel-distance calculation
- ✅ **Violation detection**: Flagging with severity classification
- ✅ **Structured JSON response**: v1.5 format with full metadata
- ✅ **Docker containerization**: CPU-only deployment
- ✅ **Health check endpoint**: System monitoring
- ✅ **Configuration endpoint**: Runtime configuration inspection

### ❌ Not Yet Implemented
- ❌ Real-world speed calibration (homography)
- ❌ Live camera streams (RTSP)
- ❌ GPU optimization
- ❌ Multi-camera tracking
- ❌ Night/weather adaptation
- ❌ Database integration

---

## 🧠 Core Responsibilities

1. **Detect** vehicles in video frames using YOLOv8
2. **Detect** license plates inside vehicle bounding boxes
3. **Read** license plate numbers using multi-pass OCR
4. **Correct** common OCR errors automatically
5. **Validate** plates against expected patterns
6. **Assign** unique IDs to detected vehicles (tracking)
7. **Estimate** vehicle speed based on pixel movement
8. **Classify** violation severity based on overspeed amount
9. **Return** structured JSON results with full metadata

---

## 🏗️ Project Architecture

### Directory Structure
```text
ai-service/
│
├── detectors/
│   ├── vehicle_detector.py      # YOLOv8 vehicle detection
│   └── plate_detector.py        # YOLOv8 license plate detection
│
├── tracker/
│   └── centroid_tracker.py      # Centroid-based vehicle tracking
│
├── ocr/
│   └── ocr_reader.py            # Enhanced PaddleOCR with validation
│                                # - Multi-pass processing
│                                # - Automatic error correction
│                                # - Pattern validation
│
├── utils/
│   ├── config.py                # Environment-based configuration
│   │                            # - Validation system
│   │                            # - Severity classification
│   ├── pre_process.py           # Safe crop & helpers
│   └── speed_estimator.py       # Speed computation
│
├── models/
│   ├── vehicle_yolo.pt          # Vehicle detection model
│   └── plate_yolo.pt            # License plate model
│
├── app.py                       # FastAPI v1.5 entry point
│                                # - Enhanced response format
│                                # - Detailed logging
│                                # - Configuration endpoint
│
├── requirements.txt             # Python dependencies
├── Dockerfile                   # CPU-only Docker build
└── README.md                    # This file
```

---

## 🔄 Video Processing Pipeline

```text
Video Upload (received via API)
         ↓
Frame Extraction (OpenCV with FRAME_SKIP)
         ↓
Vehicle Detection (YOLOv8 with VEHICLE_CONFIDENCE)
         ↓
Vehicle Tracking (Centroid Tracker)
         ↓
License Plate Detection (YOLOv8 on vehicle ROI with PLATE_CONFIDENCE)
         ↓
Multi-pass OCR (PaddleOCR with preprocessing)
  ├─ Pass 1: Original image
  ├─ Pass 2: Contrast enhanced
  └─ Pass 3: Sharpened
         ↓
Automatic Error Correction (I→1, O→0, etc.)
         ↓
Pattern Validation (against PLATE_PATTERNS)
         ↓
Speed Estimation (Distance/Time calculation)
         ↓
Violation Detection & Severity Classification
  ├─ Low: <10 km/h over
  ├─ Medium: 10-20 km/h over
  ├─ High: 20-30 km/h over
  └─ Critical: >30 km/h over
         ↓
JSON Response (v1.5 format with full metadata)
```

---

## 🧩 Component Overview

### 🚗 Vehicle Detector (`detectors/vehicle_detector.py`)
- **Technology**: YOLOv8 object detection
- **Classes**: car, bus, truck, motorcycle (COCO dataset)
- **Confidence**: Configurable via `VEHICLE_CONFIDENCE` (default: 0.35)
- **Output**: Bounding boxes (x1, y1, x2, y2)

### 🔢 License Plate Detector (`detectors/plate_detector.py`)
- **Technology**: YOLOv8 custom-trained model
- **Application**: Cropped vehicle regions (improved accuracy)
- **Logic**: Selects highest-confidence plate
- **Confidence**: Configurable via `PLATE_CONFIDENCE` (default: 0.25)

### 🧭 Vehicle Tracker (`tracker/centroid_tracker.py`)
- **Technology**: Centroid-based tracking with Euclidean distance
- **Function**: Assigns unique `vehicle_id` across frames
- **Parameters**:
  - `MAX_DISAPPEARED`: Configurable (default: 60 frames)
  - `MAX_DISTANCE`: Configurable (default: 70 pixels)
- **Logic**: Handles temporary disappearance/reappearance

### 🔍 Enhanced OCR Engine (`ocr/ocr_reader.py`) ⭐ NEW in v1.5

**Multi-pass Processing**:
```python
# Pass 1: Original image
result1 = read_plate_enhanced(plate_crop)

# Pass 2: Contrast enhancement (CLAHE)
enhanced = enhance_contrast(plate_crop)
result2 = read_plate_enhanced(enhanced)

# Pass 3: Sharpening
sharpened = sharpen_image(plate_crop)
result3 = read_plate_enhanced(sharpened)

# Return best result (validated > highest confidence)
```

**Automatic Error Correction**:
```python
OCR_CORRECTIONS = {
    'O': '0',  # Letter O → Number 0
    'I': '1',  # Letter I → Number 1
    'l': '1',  # Lowercase L → Number 1
    'S': '5',  # Letter S → Number 5
    'B': '8',  # Letter B → Number 8
    'Z': '2',  # Letter Z → Number 2
    'G': '6',  # Letter G → Number 6
}
```

**Pattern Validation**:
```python
PLATE_PATTERNS = [
    r'^[0-9]{1,4}TUN[0-9]{1,4}$',  # 123TUN456
    r'^TUN[0-9]{4,6}$',             # TUN123456
    r'^[A-Z]{2}[0-9]{3,4}$',        # AB1234
]
```

**OCRResult Object**:
```python
{
    "plate_number": "123TUN456",
    "raw_ocr_text": "I23TUN456",
    "confidence": 0.92,
    "validated": true,
    "corrections_applied": ["I→1"],
    "validation_errors": []
}
```

### 🚀 Speed Estimator (`utils/speed_estimator.py`)
- **Method**: Pixel-distance based movement
- **Formula**: 
  ```
  pixel_distance * PIXEL_TO_METER / time_elapsed * 3.6 = km/h
  ```
- **Calibration**: Configurable via `PIXEL_TO_METER` (default: 0.05)

### ⚙️ Enhanced Configuration (`utils/config.py`) ⭐ NEW in v1.5

**Environment-based Configuration**:
```python
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

# OCR Enhancement Settings
OCR_MULTI_PASS = os.getenv("OCR_MULTI_PASS", "true").lower() == "true"
OCR_MAX_ATTEMPTS = int(os.getenv("OCR_MAX_ATTEMPTS", "3"))

# Response Format Settings
INCLUDE_TRAJECTORY = os.getenv("INCLUDE_TRAJECTORY", "true").lower() == "true"
TRAJECTORY_SAMPLING = int(os.getenv("TRAJECTORY_SAMPLING", "10"))
```

**Severity Classification**:
```python
def get_violation_severity(overspeed_kmh: float) -> str:
    if overspeed_kmh < 10:
        return "low"
    elif overspeed_kmh < 20:
        return "medium"
    elif overspeed_kmh < 30:
        return "high"
    else:
        return "critical"
```

**Configuration Validation**:
```python
def validate_configuration() -> Tuple[bool, list]:
    errors = []
    
    if not os.path.exists(VEHICLE_MODEL_PATH):
        errors.append(f"Vehicle model not found: {VEHICLE_MODEL_PATH}")
    
    if PIXEL_TO_METER <= 0:
        errors.append(f"PIXEL_TO_METER must be positive, got {PIXEL_TO_METER}")
    
    # ... more validations
    
    return len(errors) == 0, errors
```

---

## 📡 API Endpoints

### 1. Health Check
**URL**: `GET /health`

**Response**:
```json
{
  "status": "OK",
  "version": "1.5.0",
  "config_valid": true
}
```

---

### 2. Configuration Info ⭐ NEW in v1.5
**URL**: `GET /config`

**Response**:
```json
{
  "speed_limit_kmh": 50.0,
  "pixel_to_meter": 0.05,
  "min_tracked_frames": 8,
  "frame_skip": 1,
  "vehicle_confidence": 0.35,
  "plate_confidence": 0.25,
  "ocr_confidence": 0.5,
  "max_disappeared": 60,
  "max_distance": 70.0,
  "ocr_multi_pass": true,
  "ocr_max_attempts": 3
}
```

---

### 3. Process Video (Enhanced v1.5)
**URL**: `POST /api/process-video`

**Request**:
- **Content-Type**: `multipart/form-data`
- **Field**: `video` (file)
- **Supported formats**: `.mp4`, `.avi`, `.mov`, `.mkv`
- **Max size**: Configurable via `MAX_UPLOAD_MB` (default: 200 MB)

**Response (v1.5 Format)**:
```json
{
  "status": "success",
  "processing_time_seconds": 45.3,
  "video_info": {
    "filename": "traffic_video.mp4",
    "duration_seconds": 30.5,
    "fps": 30.0,
    "total_frames": 915,
    "processed_frames": 458
  },
  "summary": {
    "total_vehicles_tracked": 12,
    "vehicles_with_plates": 8,
    "violations_detected": 2,
    "average_speed_kmh": 48.5
  },
  "violations": [
    {
      "violation_id": "v_001",
      "plate_number": "123TUN456",
      "plate_confidence": 0.92,
      "plate_validated": true,
      "speed_kmh": 72.4,
      "speed_limit_kmh": 50.0,
      "overspeed_kmh": 22.4,
      "timestamp_seconds": 3.2,
      "frame_number": 96,
      "severity": "high"
    }
  ],
  "tracked_vehicles": [
    {
      "vehicle_id": "veh_001",
      "tracking_info": {
        "first_frame": 10,
        "last_frame": 85,
        "frames_tracked": 76,
        "trajectory_length_pixels": 450.3
      },
      "plate_info": {
        "plate_number": "123TUN456",
        "raw_ocr_text": "I23TUN456",
        "confidence": 0.92,
        "validated": true,
        "corrections_applied": ["I→1"],
        "validation_errors": [],
        "detection_frame": 25
      },
      "speed_info": {
        "speed_kmh": 72.4,
        "is_violation": true,
        "calculation_valid": true
      },
      "positions": [
        {"frame": 10, "x": 412, "y": 318},
        {"frame": 20, "x": 430, "y": 340}
      ]
    }
  ],
  "configuration": {
    "speed_limit_kmh": 50.0,
    "pixel_to_meter": 0.05,
    "min_tracked_frames": 8,
    "frame_skip": 1,
    "vehicle_confidence": 0.35,
    "plate_confidence": 0.25,
    "ocr_confidence": 0.5
  }
}
```

**Key Changes from v1.0**:
- ✅ `violations` is now an array (not object)
- ✅ Added `violations` array with full metadata
- ✅ Added `tracked_vehicles` array with complete tracking data
- ✅ Added `summary` object with aggregated statistics
- ✅ Added `video_info` with processing metadata
- ✅ Added confidence scores and validation status
- ✅ Added severity classification
- ✅ Added OCR corrections tracking
- ✅ Removed `violations_nbr` and `details` root keys

---

## 🚀 Running Locally (CPU-only)

### Prerequisites
- Python 3.10+
- CPU with AVX support

### 1️⃣ Setup Virtual Environment
```bash
cd ai-service
python -m venv venv

# Activate
source venv/bin/activate  # Linux/macOS
venv\Scripts\activate     # Windows
```

### 2️⃣ Install Dependencies
```bash
# Install CPU-only PyTorch first (important!)
pip install torch==2.5.1 --index-url https://download.pytorch.org/whl/cpu

# Install remaining dependencies
pip install -r requirements.txt
```

### 3️⃣ Configure (Optional)
```bash
# Create .env file for custom configuration
cp .env.example .env

# Edit configuration
nano .env
```

### 4️⃣ Run Server
```bash
uvicorn app:app --host 0.0.0.0 --port 8000
```

### 5️⃣ Access Documentation
- **Swagger UI**: http://localhost:8000/docs
- **Health Check**: http://localhost:8000/health
- **Configuration**: http://localhost:8000/config

---

## 🐳 Running with Docker

### Build Image
```bash
cd ai-service
docker build -t traffic-ai-service .
```

### Run Container
```bash
docker run -p 8000:8000 \
  -e SPEED_LIMIT=60 \
  -e OCR_MULTI_PASS=true \
  traffic-ai-service
```

### Run with Docker Compose
```bash
# From project root
docker-compose up traffic-ai-service
```

---

## ⚙️ Configuration Options

### Environment Variables

**Model Configuration**:
```bash
VEHICLE_MODEL_PATH=models/vehicle_yolo.pt
PLATE_MODEL_PATH=models/plate_yolo.pt
```

**Speed Detection**:
```bash
PIXEL_TO_METER=0.05      # Calibration factor (meters per pixel)
SPEED_LIMIT=50.0         # Speed limit in km/h
```

**Processing Settings**:
```bash
FRAME_SKIP=1             # Process every N frames (1 = every frame)
MIN_TRACKED_FRAMES=8     # Minimum frames for speed calculation
```

**Detection Confidence** (0.0 to 1.0):
```bash
VEHICLE_CONFIDENCE=0.35  # Vehicle detection threshold
PLATE_CONFIDENCE=0.25    # Plate detection threshold
OCR_CONFIDENCE=0.5       # OCR confidence threshold
```

**Tracking Settings**:
```bash
MAX_DISAPPEARED=60       # Max frames vehicle can disappear
MAX_DISTANCE=70.0        # Max pixel distance for tracking
```

**OCR Enhancement** ⭐ NEW in v1.5:
```bash
OCR_MULTI_PASS=true      # Enable multi-pass OCR
OCR_MAX_ATTEMPTS=3       # Max OCR attempts per plate
```

**Response Configuration**:
```bash
INCLUDE_TRAJECTORY=true  # Include trajectory points in response
TRAJECTORY_SAMPLING=10   # Sample every N frames
```

**Upload Limits**:
```bash
MAX_UPLOAD_MB=200        # Maximum upload size in MB
```

---

## 📊 Enhanced Logging (v1.5)

### Startup Logs
```
2025-01-01 14:30:00 | INFO  | ai-service | ==================================================
2025-01-01 14:30:00 | INFO  | ai-service | AI-Service Configuration v1.5
2025-01-01 14:30:00 | INFO  | ai-service | ==================================================
2025-01-01 14:30:00 | INFO  | ai-service | Models:
2025-01-01 14:30:00 | INFO  | ai-service |   Vehicle: models/vehicle_yolo.pt
2025-01-01 14:30:00 | INFO  | ai-service |   Plate:   models/plate_yolo.pt
2025-01-01 14:30:00 | INFO  | ai-service | Speed:
2025-01-01 14:30:00 | INFO  | ai-service |   Limit:         50.0 km/h
2025-01-01 14:30:00 | INFO  | ai-service |   Calibration:   0.05 m/pixel
2025-01-01 14:30:00 | INFO  | ai-service | OCR Enhancement:
2025-01-01 14:30:00 | INFO  | ai-service |   Multi-pass:    True
2025-01-01 14:30:00 | INFO  | ai-service |   Max attempts:  3
2025-01-01 14:30:00 | INFO  | ai-service | ==================================================
```

### Processing Logs
```
2025-01-01 14:31:00 | INFO  | ai-service | ▶ Received video: filename='traffic.mp4' content_type='video/mp4'
2025-01-01 14:31:00 | INFO  | ai-service | 💾 Saved to '/tmp/xyz.mp4' (15.34 MB)
2025-01-01 14:31:00 | INFO  | ai-service | 📹 Video info: fps=30.0 total_frames=915 duration=30.5s
2025-01-01 14:31:00 | INFO  | ai-service | 🔄 Starting frame processing...
2025-01-01 14:31:15 | DEBUG | ai-service | Frame 100/915 → detected=3 tracked=2
2025-01-01 14:31:15 | DEBUG | ai-service | 🔍 Vehicle 0 → Plate '123TUN456' (conf=0.92, valid=True)
2025-01-01 14:31:45 | INFO  | ai-service | ✅ Frame processing complete
2025-01-01 14:31:45 | INFO  | ai-service | 📤 Results: vehicles=12 plates=8 violations=2 time=45.3s
```

---

## 🧪 Testing

### Health Check
```bash
curl http://localhost:8000/health
```

**Expected Response**:
```json
{
  "status": "OK",
  "version": "1.5.0",
  "config_valid": true
}
```

### Get Configuration
```bash
curl http://localhost:8000/config
```

### Upload Test Video
```bash
curl -X POST http://localhost:8000/api/process-video \
  -F "video=@test_video.mp4"
```

### Test with Python
```python
import requests

url = "http://localhost:8000/api/process-video"
files = {"video": open("test_video.mp4", "rb")}
response = requests.post(url, files=files)

result = response.json()
print(f"Violations: {result['summary']['violations_detected']}")
print(f"Vehicles: {result['summary']['total_vehicles_tracked']}")
```

---

## 📊 Performance Metrics (CPU)

### Video Processing Times
| Video Length | Resolution | Processing Time | Notes |
|-------------|-----------|----------------|-------|
| 30 seconds | 720p | ~45-60 sec | Optimal |
| 1 minute | 720p | ~90-120 sec | Good |
| 2 minutes | 720p | ~180-240 sec | Acceptable |
| 5 minutes | 720p | ~450-600 sec | Near max |

**Factors affecting performance**:
- Video resolution (720p recommended)
- Number of vehicles per frame
- Frame rate (30 fps optimal)
- CPU performance
- `FRAME_SKIP` setting
- `OCR_MULTI_PASS` enabled/disabled

**Optimization Tips**:
- Set `FRAME_SKIP=1` (process every 2nd frame) for 2x speedup
- Set `OCR_MULTI_PASS=false` for faster but less accurate OCR
- Reduce video resolution to 720p
- Use H.264 encoded videos
- Ensure adequate CPU resources

---

## 🐛 Troubleshooting

### Issue: "Module not found"
```bash
# Ensure virtual environment is activated
source venv/bin/activate

# Reinstall dependencies
pip install -r requirements.txt
```

### Issue: OCR not detecting plates
**Solutions**:
- Check `PLATE_CONFIDENCE` threshold (try lowering to 0.20)
- Verify `plate_yolo.pt` model exists
- Enable `OCR_MULTI_PASS` for better accuracy
- Check plate is visible in video
- Verify PaddleOCR installed correctly

### Issue: Slow processing
**Solutions**:
- Increase `FRAME_SKIP` to process every 2nd frame
- Disable `OCR_MULTI_PASS` for faster processing
- Reduce video resolution
- Use shorter test videos
- Check CPU resources

### Issue: Configuration validation errors
**Check logs on startup**:
```bash
uvicorn app:app --host 0.0.0.0 --port 8000
```

Look for:
```
ERROR: Configuration validation failed:
  - PIXEL_TO_METER must be positive, got -0.05
  - Vehicle model not found: models/vehicle_yolo.pt
```

Fix the reported errors in your environment variables or configuration.

### Issue: Low OCR accuracy
**Solutions**:
- Enable `OCR_MULTI_PASS=true`
- Increase `OCR_MAX_ATTEMPTS` to 5
- Lower `OCR_CONFIDENCE` threshold
- Check video quality and plate visibility
- Adjust `PLATE_PATTERNS` for your region

---

## 🚀 v2.0 Roadmap

### Planned Features
- [ ] **Real-world Calibration**: Homography-based speed calculation
- [ ] **Multi-lane Support**: Separate tracking per lane
- [ ] **GPU Acceleration**: CUDA support for faster processing
- [ ] **Database Integration**: Store results in PostgreSQL
- [ ] **Advanced OCR**: Deep learning-based plate reading
- [ ] **Night Mode**: Adaptation for low-light conditions
- [ ] **Weather Adaptation**: Handle rain, fog, snow
- [ ] **RTSP Streams**: Support live camera feeds
- [ ] **Batch Processing**: Process multiple videos in parallel

---

## 📖 Dependencies

```
fastapi==0.109.0          # Web framework
uvicorn[standard]==0.24.0 # ASGI server
torch==2.5.1+cpu          # PyTorch (CPU-only)
torchvision==0.20.1+cpu   # PyTorch vision (CPU-only)
ultralytics==8.0.220      # YOLOv8
paddleocr==2.7.2          # OCR engine
paddlepaddle==2.6.2       # PaddlePaddle framework
easyocr==1.7.1            # Alternative OCR (backup)
opencv-python-headless    # Video processing
numpy==1.24.3             # Numerical computing
Pillow==10.1.0            # Image processing
python-multipart==0.0.6   # Multipart form support
```

---

## 📝 Notes

- **CPU-only**: Optimized for development/testing without GPU
- **Model files**: YOLO models must be in `models/` directory
- **Memory**: Requires ~2-4GB RAM for processing
- **OCR Language**: Currently configured for English alphanumeric
- **Plate Patterns**: Adjust `PLATE_PATTERNS` in `config.py` for your region

---

## 🙏 Acknowledgments

- **Ultralytics YOLOv8** - Object detection framework
- **PaddleOCR** - OCR solution with high accuracy
- **FastAPI** - Modern Python web framework
- **OpenCV** - Computer vision library

---

**AI-Service Status**: v1.5 Stabilization Complete ✅  
**Ready for**: v2.0 Real-world Calibration 🚀