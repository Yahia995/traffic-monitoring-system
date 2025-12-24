# 🚦 AI-Service — Traffic Violation Detection

**Current Version**: v1.0 (MVP) ✅  
**Next Version**: v1.5 (Stabilization & Enhancements) 🚧

This module represents the **Artificial Intelligence (AI) service** of the Traffic Monitoring System. It analyzes traffic videos to detect vehicles, recognize license plates, track vehicles, estimate speed, and identify traffic violations.

The service exposes a **FastAPI-based REST API** consumed by the **Ktor backend**.

---

## 📌 Current Capabilities (v1.0)

### ✅ Implemented Features
- ✅ **Offline video processing**: Analyzes pre-recorded video files
- ✅ **Vehicle detection**: Powered by YOLOv8
- ✅ **License plate detection**: Powered by YOLOv8
- ✅ **License plate OCR**: Character recognition using PaddleOCR
- ✅ **Vehicle tracking**: Centroid-based tracking algorithm
- ✅ **Speed estimation**: Pixel-distance calculation
- ✅ **Violation detection**: Flagging vehicles over speed limit
- ✅ **Structured JSON response**: For backend consumption
- ✅ **Docker containerization**: CPU-only deployment
- ✅ **Health check endpoint**: System monitoring

### ❌ Not Yet Implemented
- ❌ Real-world speed calibration (homography)
- ❌ Live camera streams (RTSP)
- ❌ GPU optimization
- ❌ Multi-camera tracking
- ❌ Advanced OCR post-processing
- ❌ Night/weather adaptation

---

## 🧠 Core Responsibilities

1. **Detect** vehicles in video frames using YOLO
2. **Detect** license plates inside vehicle bounding boxes
3. **Read** license plate numbers using OCR
4. **Assign** unique IDs to detected vehicles (tracking)
5. **Estimate** vehicle speed based on pixel movement
6. **Detect** speeding violations based on configured limits
7. **Return** structured JSON results to backend

---

## 🏗️ Project Architecture

### Directory Structure
```text
ai-service/
│
├── detectors/
│   ├── vehicle_detector.py      # YOLO vehicle detection
│   └── plate_detector.py        # YOLO license plate detection
│
├── tracker/
│   └── centroid_tracker.py      # Vehicle tracking logic
│
├── ocr/
│   └── ocr_reader.py            # PaddleOCR integration
│
├── utils/
│   ├── config.py                # Global configuration
│   ├── pre_process.py           # Safe crop & helpers
│   └── speed_estimator.py       # Speed computation
│
├── models/
│   ├── vehicle_yolo.pt          # Vehicle detection model
│   └── plate_yolo.pt            # License plate model
│
├── app.py                       # FastAPI entry point
├── requirements.txt             # Python dependencies
├── Dockerfile                   # Docker build instructions
└── README.md                    # This file
```

---

## 🔄 Video Processing Pipeline

```text
Video Upload (received via API)
         ↓
Frame Extraction (OpenCV)
         ↓
Vehicle Detection (YOLO)
         ↓
Vehicle Tracking (Centroid Tracker)
         ↓
License Plate Detection (YOLO on vehicle ROI)
         ↓
OCR (PaddleOCR on plate ROI)
         ↓
Speed Estimation (Distance/Time calculation)
         ↓
Violation Detection (Compare vs Speed Limit)
         ↓
JSON Response (Aggregation)
```

---

## 🧩 Component Overview

### 🚗 Vehicle Detector (`detectors/vehicle_detector.py`)
- **Technology**: YOLOv8 object detection
- **Classes**: car, bus, truck, motorcycle (COCO dataset)
- **Confidence**: 0.35 threshold
- **Output**: Bounding boxes (x1, y1, x2, y2)

### 🔢 License Plate Detector (`detectors/plate_detector.py`)
- **Technology**: YOLOv8 custom-trained model
- **Application**: Cropped vehicle regions (improved accuracy)
- **Logic**: Selects highest-confidence plate
- **Confidence**: 0.25 threshold

### 🧭 Vehicle Tracker (`tracker/centroid_tracker.py`)
- **Technology**: Centroid-based tracking with Euclidean distance
- **Function**: Assigns unique `vehicle_id` across frames
- **Parameters**:
  - `MAX_DISAPPEARED`: 60 frames
  - `MAX_DISTANCE`: 70 pixels
- **Logic**: Handles temporary disappearance/reappearance

### 🔍 OCR Engine (`ocr/ocr_reader.py`)
- **Technology**: PaddleOCR (CPU mode)
- **Function**: Reads text from plate region
- **Filtering**: Alphanumeric only (A-Z, 0-9)
- **Validation**: Minimum 4 characters

### 🚀 Speed Estimator (`utils/speed_estimator.py`)
- **Method**: Pixel-distance based movement
- **Formula**: 
  ```
  pixel_distance * PIXEL_TO_METER / time_elapsed * 3.6 = km/h
  ```
- **Note**: Currently using approximate calibration (0.05 m/pixel)

---

## ⚙️ Configuration (`utils/config.py`)

```python
# Model paths
VEHICLE_MODEL_PATH = "models/vehicle_yolo.pt"
PLATE_MODEL_PATH = "models/plate_yolo.pt"

# Speed calibration
PIXEL_TO_METER = 0.05    # Tune with real-world calibration
SPEED_LIMIT = 50.0       # km/h

# Performance settings
FRAME_SKIP = 1           # Process every N frames
MIN_TRACKED_FRAMES = 8   # Minimum frames to calculate speed

# Tracking parameters
MAX_DISAPPEARED = 60     # Max frames object can be lost
MAX_DISTANCE = 70        # Max distance for association (pixels)

# Upload limits
MAX_UPLOAD_MB = 200
ALLOWED_EXT = (".mp4", ".avi", ".mov", ".mkv")
```

---

## 📡 API Endpoints

### 1. Health Check
**URL**: `GET /health`

**Response**:
```json
{
  "status": "OK"
}
```

---

### 2. Process Video
**URL**: `POST /api/process-video`

**Request**:
- **Content-Type**: `multipart/form-data`
- **Field**: `video` (file)
- **Supported formats**: `.mp4`, `.avi`, `.mov`, `.mkv`
- **Max size**: 200 MB

**Response**:
```json
{
  "violations_nbr": 2,
  "violations": {
    "123TUN456": {
      "speed": 72.4,
      "speed_limit": 50,
      "timestamp": 3.2
    },
    "789TUN012": {
      "speed": 65.8,
      "speed_limit": 50,
      "timestamp": 8.5
    }
  },
  "details": {
    "0": {
      "first_frame": 10,
      "last_frame": 85,
      "positions": [[412, 318], [430, 340], [448, 362]],
      "plate": "123TUN456"
    },
    "1": {
      "first_frame": 25,
      "last_frame": 120,
      "positions": [[520, 280], [538, 295], [556, 310]],
      "plate": "789TUN012"
    }
  }
}
```

**Response Fields**:
- `violations_nbr`: Total number of violations
- `violations`: Dictionary of violations by plate number
  - `speed`: Detected speed (km/h)
  - `speed_limit`: Configured limit
  - `timestamp`: Time of detection (seconds)
- `details`: Low-level tracking data per vehicle ID
  - `first_frame`: First detection frame
  - `last_frame`: Last detection frame
  - `positions`: List of centroids [x, y]
  - `plate`: Recognized plate number (null if unreadable)

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

### 3️⃣ Run Server
```bash
uvicorn app:app --host 0.0.0.0 --port 8000
```

### 4️⃣ Access Documentation
Open browser: **http://localhost:8000/docs**

---

## 🐳 Running with Docker

### Build Image
```bash
cd ai-service
docker build -t traffic-ai-service .
```

### Run Container
```bash
docker run -p 8000:8000 traffic-ai-service
```

### Run with Docker Compose
```bash
# From project root
docker-compose up traffic-ai-service
```

---

## 🔧 Docker Configuration

### Dockerfile Highlights
```dockerfile
# CPU-only PyTorch
RUN pip install --no-cache-dir \
    torch==2.5.1+cpu \
    torchvision==0.20.1+cpu \
    --index-url https://download.pytorch.org/whl/cpu

# Environment variables
ENV CUDA_VISIBLE_DEVICES=""
ENV NVIDIA_VISIBLE_DEVICES=none
ENV TORCH_DEVICE=cpu
```

### Why CPU-only?
- ✅ Smaller image size (~2GB vs ~8GB)
- ✅ No CUDA dependencies
- ✅ Works on any machine
- ✅ Suitable for MVP/testing
- ⚠️ Slower processing (~2-3x slower than GPU)

---

## 🧪 Testing

### Manual Test with cURL
```bash
# Health check
curl http://localhost:8000/health

# Upload video
curl -X POST http://localhost:8000/api/process-video \
  -F "video=@test_video.mp4"
```

### Test with Python
```python
import requests

url = "http://localhost:8000/api/process-video"
files = {"video": open("test_video.mp4", "rb")}
response = requests.post(url, files=files)
print(response.json())
```

---

## 📊 Performance Metrics (CPU)

| Video Length | Resolution | Processing Time | Violations |
|-------------|-----------|----------------|------------|
| 30 seconds | 720p | ~45-60 sec | 2-5 |
| 1 minute | 720p | ~90-120 sec | 5-10 |
| 2 minutes | 720p | ~180-240 sec | 10-20 |

**Factors affecting performance**:
- Video resolution
- Number of vehicles
- Frame rate
- CPU speed

---

## 🐛 Troubleshooting

### Issue: "Module not found"
```bash
# Ensure virtual environment is activated
source venv/bin/activate

# Reinstall dependencies
pip install -r requirements.txt
```

### Issue: "CUDA not available" warning
```bash
# This is expected for CPU-only installation
# The system will automatically use CPU
```

### Issue: Slow processing
```bash
# Reduce video resolution
# Use shorter test videos
# Increase FRAME_SKIP in config.py
```

### Issue: OCR not detecting plates
```bash
# Check plate_yolo.pt model is present in models/
# Verify PaddleOCR installed correctly
# Try adjusting confidence thresholds
```

---

## 🚀 v1.5 Improvements (Coming Next)

### Enhanced OCR
- [ ] Post-processing filters
- [ ] Automatic correction (regex)
- [ ] Confidence-based retry
- [ ] Multiple OCR passes

### Better Tracking
- [ ] Kalman filter integration
- [ ] Handle occlusions better
- [ ] Reduce ID switches
- [ ] Multi-lane support

### Optimization
- [ ] Batch processing
- [ ] Frame caching
- [ ] Async video reading
- [ ] Memory optimization

### Error Handling
- [ ] Better logging
- [ ] Detailed error messages
- [ ] Graceful degradation
- [ ] Timeout handling

---

## 📈 Roadmap

### v1.5 — Stabilization
- OCR improvements
- Tracking stability
- Code cleanup
- Better logging

### v2.0 — Functional Complete
- Real-world calibration (homography)
- Multi-lane tracking
- Database integration
- Advanced statistics

### v3.0 — Production
- GPU acceleration
- RTSP live streams
- Multi-camera support
- Night/weather adaptation

---

## 🔌 Integration with Backend

The AI-Service is called by the Ktor backend:

```
Ktor Backend → POST /api/process-video → AI-Service
                         ↓
                   JSON Response
                         ↓
              Ktor Backend → Frontend
```

---

## 📖 Dependencies

### Core Libraries
```
fastapi==0.109.0          # Web framework
uvicorn[standard]==0.24.0 # ASGI server
torch==2.5.1+cpu          # PyTorch (CPU)
ultralytics==8.0.220      # YOLOv8
paddleocr==2.7.2          # OCR
opencv-python-headless    # Video processing
numpy==1.24.3             # Numerical computing
```

---

## 📝 Notes

- **CPU-only**: Optimized for development/testing
- **No GPU**: GPU support planned for v2.0
- **Model files**: YOLO models must be in `models/` directory
- **Memory**: Requires ~2-4GB RAM for processing

---

## 🙏 Acknowledgments

- **Ultralytics YOLOv8** - Object detection framework
- **PaddleOCR** - OCR solution
- **FastAPI** - Modern Python web framework
- **OpenCV** - Computer vision library

---

**Ready for v1.5 improvements?** See the roadmap above! 🚀