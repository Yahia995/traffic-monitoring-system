# 🚦 Traffic Monitoring System — v1.5 (Stabilization Complete)

An end-to-end **intelligent traffic violation detection system** that analyzes traffic videos to detect vehicles, recognize license plates, estimate speed, and identify traffic violations with enhanced accuracy and validation.

**Current Version**: v1.5 (Stabilization) ✅  
**Next Version**: v2.0 (Database & Authentication) 🚀

---

## 🎯 Project Objectives

- ✅ Detect vehicles in traffic videos using YOLOv8
- ✅ Detect and recognize license plates with enhanced OCR
- ✅ Track vehicles across video frames with centroid tracking
- ✅ Estimate vehicle speed (pixel-based approximation)
- ✅ Detect speeding violations with severity classification
- ✅ Validate license plates with pattern matching and correction
- ✅ Display results in modern, interactive web dashboard
- ✅ Full Docker containerization with health checks

---

## 🆕 What's New in v1.5

### AI Service
- ✅ Enhanced OCR with multi-pass processing and validation
- ✅ Automatic license plate correction (common OCR errors)
- ✅ Pattern-based validation with confidence tracking
- ✅ Improved error handling and detailed logging
- ✅ Configurable via environment variables

### Backend
- ✅ Request correlation IDs for tracing
- ✅ Enhanced error messages with user-friendly details
- ✅ Response validation and consistency checks
- ✅ Detailed health endpoint with AI service status
- ✅ Summary-only endpoint for quick statistics

### Frontend
- ✅ Modern gradient-based UI with professional styling
- ✅ Tab-based interface (Violations / All Vehicles / Raw Data)
- ✅ Interactive summary dashboard with visual metrics
- ✅ Sortable, filterable violations table
- ✅ Expandable vehicle detail cards
- ✅ CSV export functionality
- ✅ Drag & drop file upload
- ✅ Real-time progress indicators
- ✅ Severity color coding and confidence bars
- ✅ Responsive design for all screen sizes

### Infrastructure
- ✅ Docker health checks for all services
- ✅ Resource limits and reservations
- ✅ Enhanced networking configuration
- ✅ Comprehensive environment variable support

---

## 🧱 System Architecture

```text
┌─────────────────────────────────────────────────┐
│  Frontend Dashboard (React + Vite)              │
│  Port: 5173                                     │
│  • Modern UI with tabs & statistics             │
│  • Drag & drop upload                           │
│  • Interactive visualizations                   │
└────────────────┬────────────────────────────────┘
                 │ HTTP REST
                 ↓
┌─────────────────────────────────────────────────┐
│  Backend API (Ktor + Kotlin)                    │
│  Port: 8080                                     │
│  • Request correlation tracking                 │
│  • Enhanced error handling                      │
│  • Response validation                          │
└────────────────┬────────────────────────────────┘
                 │ HTTP REST
                 ↓
┌─────────────────────────────────────────────────┐
│  AI-Service (FastAPI + Python)                  │
│  Port: 8000                                     │
│  • Enhanced YOLOv8 Detection                    │
│  • Multi-pass PaddleOCR with validation         │
│  • Automatic plate correction                   │
│  • Centroid Tracker                             │
│  • Speed Estimator with severity classification │
└─────────────────────────────────────────────────┘
```

---

## 📦 Project Modules

### 🤖 AI-Service (Python / FastAPI)
**Port**: 8000  
**Version**: 1.5.0  
**Responsibilities**:
- Vehicle detection using YOLOv8
- License plate detection
- Enhanced OCR with PaddleOCR (multi-pass)
- Automatic plate correction and validation
- Vehicle tracking (Centroid-based)
- Speed estimation (pixel-distance)
- Violation detection with severity classification

📁 **Location**: `ai-service/`  
📖 **Documentation**: [ai-service/README.md](./ai-service/README.md)

---

### ⚙️ Backend API (Ktor / Kotlin)
**Port**: 8080  
**Version**: 1.5.0  
**Responsibilities**:
- Video upload endpoints (full & summary)
- Communication with AI-Service
- Request correlation tracking
- Enhanced error handling
- Response validation
- Health monitoring (basic & detailed)
- CORS management
- Swagger/OpenAPI documentation

📁 **Location**: `ktor-backend/`  
📖 **Documentation**: [ktor-backend/README.md](./ktor-backend/README.md)

---

### 🖥️ Dashboard Frontend (React / Vite)
**Port**: 5173  
**Version**: 1.5.0  
**Responsibilities**:
- Modern, responsive web interface
- Video upload with drag & drop
- Summary statistics dashboard
- Interactive violations table
- Detailed vehicle tracking view
- CSV export functionality
- Real-time progress tracking

📁 **Location**: `dashboard-frontend/`  
📖 **Documentation**: [dashboard-frontend/README.md](./dashboard-frontend/README.md)

---

## 🚀 Quick Start with Docker (Recommended)

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+

### 1️⃣ Clone Repository
```bash
git clone https://github.com/Yahia995/traffic-monitoring-system.git
cd traffic-monitoring-system
```

### 2️⃣ Configure Environment (Optional)
```bash
cp .env.example .env
# Edit .env if you need custom configuration
```

### 3️⃣ Start All Services
```bash
docker-compose up --build
```

This will start:
- **AI-Service** on `http://localhost:8000`
- **Backend** on `http://localhost:8080`
- **Frontend** on `http://localhost:5173`

### 4️⃣ Access the Dashboard
Open your browser to: **http://localhost:5173**

### 5️⃣ Stop Services
```bash
docker-compose down
```

---

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | Web dashboard |
| **Backend API** | http://localhost:8080 | REST API |
| **Backend Health** | http://localhost:8080/health | Basic health check |
| **Backend Detailed Health** | http://localhost:8080/health/detailed | Detailed health with AI status |
| **Backend Swagger** | http://localhost:8080/swagger | API documentation |
| **AI-Service Docs** | http://localhost:8000/docs | FastAPI Swagger UI |
| **AI-Service Health** | http://localhost:8000/health | Health check |

---

## 📤 API Response Format (v1.5)

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
        "detection_frame": 25
      },
      "speed_info": {
        "speed_kmh": 72.4,
        "is_violation": true,
        "calculation_valid": true
      },
      "positions": [
        { "frame": 10, "x": 412, "y": 318 },
        { "frame": 20, "x": 430, "y": 340 }
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
- `violations` is now an array (not object) with full metadata
- Added `tracked_vehicles` array with complete tracking data
- Added `summary` object with aggregated statistics
- Added `video_info` with processing metadata
- Added confidence scores and validation status
- Added severity classification (low/medium/high/critical)

---

## ✅ v1.5 Capabilities

### Detection & Recognition
- ✅ YOLOv8-based vehicle detection
- ✅ YOLOv8-based license plate detection
- ✅ Multi-pass PaddleOCR with validation
- ✅ Automatic OCR error correction
- ✅ Pattern-based plate validation

### Tracking & Analysis
- ✅ Centroid-based vehicle tracking
- ✅ Pixel-based speed estimation
- ✅ Violation severity classification
- ✅ Confidence score tracking
- ✅ Trajectory recording

### User Interface
- ✅ Modern, gradient-based design
- ✅ Drag & drop video upload
- ✅ Real-time progress tracking
- ✅ Summary statistics dashboard
- ✅ Interactive violations table
- ✅ Expandable vehicle details
- ✅ CSV export functionality
- ✅ Responsive mobile design

### System Features
- ✅ Docker containerization
- ✅ Health check monitoring
- ✅ Request correlation tracking
- ✅ Enhanced error handling
- ✅ Response validation
- ✅ Comprehensive logging

---

## ⚠️ Known Limitations (v1.5)

- ⚠️ Speed estimation is pixel-based (not calibrated to real-world)
- ⚠️ No authentication or user management
- ⚠️ No database persistence
- ⚠️ No real-time camera streams
- ⚠️ CPU-only processing (no GPU acceleration)
- ⚠️ No video history or filtering

---

## 🚀 Roadmap

### 📦 v2.0 — Database & Authentication (Next)
**Focus**: Persistence and security

**Features**:
- [ ] PostgreSQL database integration
- [ ] User authentication (JWT)
- [ ] User management and roles
- [ ] Violation history storage
- [ ] Advanced filtering and search
- [ ] Statistics and analytics
- [ ] Report generation (PDF/CSV)
- [ ] Real-world speed calibration (homography)

---

### 📦 v3.0 — Real-time & Production
**Focus**: Live monitoring and scalability

**Features**:
- [ ] Live camera streams (RTSP)
- [ ] WebSocket real-time updates
- [ ] GPU acceleration (CUDA)
- [ ] Multi-camera tracking
- [ ] Night/weather adaptation
- [ ] Monitoring & observability (Grafana, Prometheus)
- [ ] Kubernetes deployment
- [ ] HTTPS & NGINX reverse proxy
- [ ] Rate limiting and caching

---

## 🔧 Configuration

### Environment Variables
All services can be configured via environment variables. See `.env.example` for details.

**Key Configuration**:
```bash
# Speed Detection
SPEED_LIMIT=50.0              # Speed limit in km/h
PIXEL_TO_METER=0.05           # Calibration factor

# Detection Confidence
VEHICLE_CONFIDENCE=0.35       # Vehicle detection threshold
PLATE_CONFIDENCE=0.25         # Plate detection threshold
OCR_CONFIDENCE=0.5            # OCR confidence threshold

# OCR Enhancement
OCR_MULTI_PASS=true           # Enable multi-pass OCR
OCR_MAX_ATTEMPTS=3            # Max OCR attempts

# Processing
FRAME_SKIP=1                  # Process every N frames
MIN_TRACKED_FRAMES=8          # Minimum frames for speed calculation
```

---

## 🐛 Troubleshooting

### Docker Issues

**Services won't start**:
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up
```

**Port conflicts**:
```bash
# Check for processes using ports
lsof -i :5173  # Frontend
lsof -i :8080  # Backend
lsof -i :8000  # AI Service
```

**AI Service timeout**:
- Use shorter test videos (30-60 seconds)
- Reduce video resolution
- Check AI service logs: `docker logs traffic-ai-service`

### Frontend Issues

**Cannot connect to backend**:
```bash
# Check backend health
curl http://localhost:8080/health

# Check detailed health (including AI service)
curl http://localhost:8080/health/detailed
```

**Upload fails**:
- Ensure video format is supported (.mp4, .avi, .mov, .mkv)
- Check file size (max 200 MB)
- Verify backend logs for errors

---

## 📊 Performance Notes

### Video Processing Times (CPU-only)
- 30-second video (720p): ~45-90 seconds
- 1-minute video (720p): ~90-180 seconds
- 2-minute video (720p): ~180-360 seconds

**Factors affecting performance**:
- Video resolution (720p recommended)
- Number of vehicles in frame
- Frame rate (30 fps optimal)
- CPU performance

**Optimization tips**:
- Use H.264 encoded videos
- Reduce resolution if processing is slow
- Adjust `FRAME_SKIP` to process every 2nd frame
- Ensure adequate CPU resources in Docker

---

## 🧪 Testing

### Health Checks
```bash
# Backend basic health
curl http://localhost:8080/health

# Backend detailed health (includes AI service)
curl http://localhost:8080/health/detailed

# AI service health
curl http://localhost:8000/health
```

### Upload Test Video
```bash
curl -X POST http://localhost:8080/api/upload-video \
  -F "video=@test_video.mp4"
```

### Summary-only endpoint (faster)
```bash
curl -X POST http://localhost:8080/api/upload-video/summary \
  -F "video=@test_video.mp4"
```

---

## 📖 Documentation

- [AI Service Documentation](./ai-service/README.md) - OCR, detection, tracking
- [Backend Documentation](./ktor-backend/README.md) - API, error handling, health
- [Frontend Documentation](./dashboard-frontend/README.md) - UI, components, styling

---

## 🙏 Acknowledgments

- **YOLOv8** (Ultralytics) - Object detection framework
- **PaddleOCR** - Optical character recognition
- **Ktor** - Kotlin web framework
- **FastAPI** - Python web framework
- **React + Vite** - Frontend framework

---

**System Status**: v1.5 Stabilization Complete ✅  
**Ready for**: v2.0 Database Integration 🚀