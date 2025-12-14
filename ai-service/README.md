# 🚦 AI-Service — Traffic Violation Detection

This module represents the **Artificial Intelligence (AI) component** of the global Traffic Monitoring System.
It is responsible for video analysis, vehicle detection, license plate recognition, vehicle tracking, speed estimation, and traffic violation detection.

The service exposes a **FastAPI-based REST API** consumed by the **Ktor backend**.

---

## 🎯 Module Responsibilities

* Vehicle detection in video streams (**YOLO**)
* License plate detection
* Vehicle tracking (**centroid-based tracking**)
* Speed estimation
* Traffic violation detection (speeding)
* License plate OCR (**PaddleOCR**)
* Exposure of results through a **REST API**

---

## 🧱 General Architecture

```text
ai-service/
│             
│
├── detectors/
│   ├── vehicle_detector.py   # Vehicle detection (YOLO)
│   └── plate_detector.py     # License plate detection (YOLO)
│
├── tracker/
│   └── centroid_tracker.py   # Vehicle tracking logic
│
├── ocr/
│   └── ocr_reader.py         # License plate OCR (PaddleOCR)
│
├── utils/
│   ├── config.py             # Global constants and parameters
│   ├── pre_process.py        # Utility functions (safe crop)
│   └── speed_estimator.py    # Speed calculation logic
│
├── models/
│   ├── vehicle_yolo.pt       # YOLO vehicle model
│   └── plate_yolo.pt         # YOLO license plate model
│
├── app.py                    # FastAPI entry point
├── requirements.txt
└── README.md
```

---

## 🔁 Video Processing Pipeline

1. Video upload (`/api/process-video`)
2. Frame-by-frame reading using **OpenCV**
3. Vehicle detection (**YOLO**)
4. Vehicle association & tracking (**CentroidTracker**)
5. License plate extraction from vehicles
6. License plate OCR (**PaddleOCR**)
7. Speed calculation (pixels → meters → km/h)
8. Traffic violation detection (speed limit comparison)
9. JSON response sent to the **Ktor backend**

---

## 🧠 Component Description

### 1️⃣ VehicleDetector

📁 `detectors/vehicle_detector.py`

* Based on **YOLO (Ultralytics)**
* Filters vehicle-related classes only (car, bus, truck, motorcycle)
* Returns bounding boxes `(x1, y1, x2, y2)`

---

### 2️⃣ PlateDetector

📁 `detectors/plate_detector.py`

* Detects license plates from vehicle crops
* Selects the bounding box with the highest confidence

---

### 3️⃣ CentroidTracker

📁 `tracker/centroid_tracker.py`

* Simple centroid-based tracking using Euclidean distance
* Assigns a unique `vehicle_id`
* Handles object disappearance and reappearance

---

### 4️⃣ OCR Reader

📁 `ocr/ocr_reader.py`

* Based on **PaddleOCR (CPU mode)**
* Text cleaning (A–Z, 0–9)
* Discards short or invalid license plates

---

### 5️⃣ Speed Estimator

📁 `utils/speed_estimator.py`

* Pixel distance → meter conversion
* Time calculation using FPS
* Speed conversion from m/s to km/h

---

### 6️⃣ Global Configuration

📁 `utils/config.py`

Centralizes:

* model paths
* speed limit
* pixel-to-meter calibration
* tracking parameters
* allowed video formats

---

## 📡 Exposed API

### ✅ Health Check

```
GET /api/health
```

```json
{ "status": "OK" }
```

---

### 🎥 Video Processing

```
POST /api/process-video
```

#### Input

* `multipart/form-data`
* `video`: `.mp4`, `.avi`, `.mkv` file

#### Output

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
      "positions": [["x", "y"], ...],
      "plate": "123TUN456"
    }
  }
}
```

---

## 📦 Main Dependencies

See `requirements.txt`

* FastAPI + Uvicorn
* OpenCV (headless)
* YOLO (Ultralytics + Torch)
* PaddleOCR
* NumPy

---

## 🔗 Integration with Ktor

* The **Ktor backend** uploads the video
* Receives a structured JSON response
* Stores traffic violations and statistics
* Serves data to the **dashboard**

---

## 🚀 Future Improvements

* Real-world speed calibration (homography)
* Multi-lane vehicle tracking
* Local license plate recognition (Tunisia)
* GPU acceleration
* Real-time streaming (RTSP)