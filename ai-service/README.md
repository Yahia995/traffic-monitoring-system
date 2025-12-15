# 🚦 AI-Service — Traffic Violation Detection (MVP)

This module represents the **Artificial Intelligence (AI) service** of the Traffic Monitoring System.  
It is responsible for analyzing traffic videos to **detect vehicles**, **recognize license plates**, **track vehicles**, **estimate speed**, and **identify traffic violations**.

The service exposes a **FastAPI-based REST API** consumed by the **Ktor backend**.

---

## 📌 Scope — MVP Version

### ✅ Included
* **Offline video processing**: Analyzes pre-recorded video files.
* **Vehicle detection**: Powered by YOLO.
* **License plate detection**: Powered by YOLO.
* **License plate OCR**: Character recognition using PaddleOCR.
* **Vehicle tracking**: Centroid-based tracking algorithm.
* **Approximate speed estimation**: Pixel-distance calculation.
* **Speed violation detection**: Flagging vehicles over the limit.
* **JSON response**: Structured output for backend consumption.

### ❌ Not Included (Planned Later)
* Real-world speed calibration (Homography).
* Live camera streams (RTSP).
* GPU optimization.
* Multi-camera tracking.

---

## 🧠 Core Responsibilities

1. **Detect** vehicles in video frames.
2. **Detect** license plates inside vehicle bounding boxes.
3. **Read** license plate numbers using OCR.
4. **Assign** a unique ID to each detected vehicle (Tracking).
5. **Estimate** vehicle speed based on movement between frames.
6. **Detect** speeding violations based on configured limits.
7. **Return** structured JSON results to the backend.

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
└── README.md                    # Documentation
```

---

## 🔄 Video Processing Pipeline

```
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

### 🚗 Vehicle Detector
* **Technology**: YOLO-based object detection.
* **Function**: Filters vehicle-related classes (car, bus, truck, motorcycle).
* **Output**: Bounding boxes (x1, y1, x2, y2).

### 🔢 License Plate Detector
* **Technology**: YOLO-based license plate detection.
* **Function**: Applied specifically on cropped vehicle regions to improve accuracy.
* **Logic**: Selects the highest-confidence plate if multiple are found.

### 🧭 Vehicle Tracker
* **Technology**: Centroid-based tracking using Euclidean distance.
* **Function**: Assigns a unique `vehicle_id` to objects across frames.
* **Logic**: Handles temporary disappearance and reappearance of vehicles.

### 🔍 OCR Engine
* **Technology**: PaddleOCR (CPU mode).
* **Function**: Reads text from the plate region.
* **Logic**: Applies alphanumeric filtering (A–Z, 0–9) and discards invalid or short plate numbers.

### 🚀 Speed Estimator
* **Technology**: Physics-based calculation.
* **Method**: Pixel-distance based movement measurement.
* **Logic**: Uses Video FPS for time calculation to convert pixels/sec to km/h.

---

## ⚙️ Configuration

Centralized in `utils/config.py`:

* YOLO model paths.
* Speed limit settings.
* Pixel-to-meter calibration value.
* Tracking thresholds.
* Allowed video formats.

---

## 📡 API Endpoints

### 1. Health Check
Checks if the service is running.

* **URL**: `/api/health`
* **Method**: `GET`
* **Response**:
```json
{
  "status": "OK"
}
```

### 2. Process Video
Upload a video file for analysis.

* **URL**: `/api/process-video`
* **Method**: `POST`
* **Body**: `multipart/form-data`
* **Field**: `video` (Supports `.mp4`, `.avi`, `.mkv`)

**Response (JSON Structure)**:
```json
{
  "violations_nbr": 2,
  "violations": {
    "123TUN456": {
      "speed": 72.4,
      "speed_limit": 50,
      "timestamp": 3.2
    }
  },
  "details": {
    "0": {
      "first_frame": 10,
      "last_frame": 85,
      "positions": [["x", "y"], ["x", "y"]],
      "plate": "123TUN456"
    }
  }
}
```

---

## 📄 JSON Design Notes

* **`violations_nbr`**: Total number of detected violations in the video.
* **`violations`**: A dictionary indexed by license plate for O(1) access to speeding data.
* **`details`**: Low-level tracking and trajectory data per vehicle ID (used for debugging or advanced visualization).

---

## 🔌 Integration with Ktor Backend

1. The **Ktor Backend** uploads a raw video file to this service.
2. The **AI Service** processes the video and returns the structured JSON response.
3. The **Backend** stores the violations and statistics in the database.
4. Data is exposed to the **Dashboard Frontend**.

---

## ⚡ Quick Start (Local)

### Prerequisites
* Python 3.10.12
* Virtual Environment (Recommended)

### Installation

```bash
# 1. Clone the repository
git clone <repository-url>
cd ai-service

# 2. Create virtual environment
python -m venv venv

# 3. Activate environment
# Linux / macOS:
source venv/bin/activate
# Windows:
venv\Scripts\activate

# 4. Install dependencies
pip install -r requirements.txt
```

### Running the Server

```bash
uvicorn app:app --host 0.0.0.0 --port 8000
```

### Accessing Documentation
Once running, open your browser to view the interactive Swagger UI:

```
http://localhost:8000/docs
```

## 🚀 Future Improvements

- [ ] **Real-world calibration**: Implement Homography for accurate distance measurement on different camera angles.
- [ ] **GPU Acceleration**: Add CUDA support for faster inference.
- [ ] **Complex Tracking**: Support multi-lane and multi-camera re-identification.
- [ ] **Validation**: Add local license plate format regex validation.
- [ ] **RTSP Support**: Enable real-time video stream processing.