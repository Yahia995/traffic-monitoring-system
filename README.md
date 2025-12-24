# 🚦 Traffic Monitoring System — v1.0 (MVP Complete)

An end-to-end **intelligent traffic violation detection system** that analyzes traffic videos to detect vehicles, recognize license plates, estimate speed, and identify traffic violations.

**Current Version**: v1.0 (MVP) ✅  
**Next Version**: v1.5 (Stabilization & Enhancements) 🚀

---

## 🎯 Project Objectives

- ✅ Detect vehicles in traffic videos
- ✅ Detect and recognize license plates using OCR
- ✅ Track vehicles across video frames
- ✅ Estimate vehicle speed (pixel-based approximation)
- ✅ Detect speeding violations
- ✅ Display results in a web dashboard
- ✅ Full Docker containerization

---

## 🧱 System Architecture

```text
┌─────────────────────────────────────────────────┐
│  Frontend Dashboard (React + Vite)              │
│  Port: 5173                                     │
└────────────────┬────────────────────────────────┘
                 │ HTTP REST
                 ↓
┌─────────────────────────────────────────────────┐
│  Backend API (Ktor + Kotlin)                    │
│  Port: 8080                                     │
└────────────────┬────────────────────────────────┘
                 │ HTTP REST
                 ↓
┌─────────────────────────────────────────────────┐
│  AI-Service (FastAPI + Python)                  │
│  Port: 8000                                     │
│  • YOLO Detection                               │
│  • PaddleOCR                                    │
│  • Centroid Tracker                             │
│  • Speed Estimator                              │
└─────────────────────────────────────────────────┘
```

---

## 📦 Project Modules

### 🤖 AI-Service (Python / FastAPI)
**Port**: 8000  
**Responsibilities**:
- Vehicle detection using YOLOv8
- License plate detection
- OCR with PaddleOCR
- Vehicle tracking (Centroid-based)
- Speed estimation (pixel-distance)
- Violation detection

📁 **Location**: `ai-service/`  
📖 **Documentation**: [ai-service/README.md](./ai-service/README.md)

---

### ⚙️ Backend API (Ktor / Kotlin)
**Port**: 8080  
**Responsibilities**:
- Video upload endpoint
- Communication with AI-Service
- Error handling & logging
- CORS management
- Swagger/OpenAPI documentation

📁 **Location**: `ktor-backend/`  
📖 **Documentation**: [ktor-backend/README.md](./ktor-backend/README.md)

---

### 🖥️ Dashboard Frontend (React / Vite)
**Port**: 5173  
**Responsibilities**:
- Video upload interface
- Violations table display
- Raw JSON debugging view
- Simple, functional MVP UI

📁 **Location**: `dashboard-frontend/`  
📖 **Documentation**: [dashboard-frontend/README.md](./dashboard-frontend/README.md)

---

## 🏗️ Repository Structure

```text
traffic-monitoring-system/
├── ai-service/              # Python AI processing service
│   ├── detectors/           # YOLO detectors
│   ├── tracker/             # Centroid tracker
│   ├── ocr/                 # PaddleOCR integration
│   ├── utils/               # Config & utilities
│   ├── models/              # YOLO model files (.pt)
│   ├── app.py               # FastAPI entry point
│   ├── requirements.txt
│   ├── Dockerfile
│   └── README.md
│
├── ktor-backend/            # Kotlin backend API
│   ├── src/
│   │   └── main/
│   │       ├── kotlin/
│   │       │   ├── client/      # AI client
│   │       │   ├── models/      # Data models
│   │       │   ├── plugins/     # Ktor plugins
│   │       │   ├── routes/      # API routes
│   │       │   └── Application.kt
│   │       └── resources/
│   │           ├── application.conf
│   │           ├── logback.xml
│   │           └── swagger/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── README.md
│
├── dashboard-frontend/      # React dashboard
│   ├── src/
│   │   ├── api/            # Backend API calls
│   │   ├── components/     # React components
│   │   ├── styles/         # CSS files
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── Dockerfile
│   └── README.md
│
├── docker-compose.yml       # Multi-service orchestration
├── .gitignore
└── README.md               # This file
```

---

## 🚀 Quick Start with Docker (Recommended)

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+

### 1️⃣ Clone Repository
```bash
git clone <repository-url>
cd traffic-monitoring-system
```

### 2️⃣ Start All Services
```bash
docker-compose up --build
```

This will start:
- **AI-Service** on `http://localhost:8000`
- **Backend** on `http://localhost:8080`
- **Frontend** on `http://localhost:5173`

### 3️⃣ Access the Dashboard
Open your browser to: **http://localhost:5173**

### 4️⃣ Stop Services
```bash
docker-compose down
```

---

## 🔧 Local Development (Without Docker)

### Prerequisites
- Python 3.10+
- Node.js 18+
- JDK 17+
- Gradle 8+

### 1️⃣ Start AI-Service
```bash
cd ai-service
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install torch==2.5.1 --index-url https://download.pytorch.org/whl/cpu
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000
```

### 2️⃣ Start Backend
```bash
cd ktor-backend
./gradlew run
```

### 3️⃣ Start Frontend
```bash
cd dashboard-frontend
npm install
npm run dev
```

---

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | Web dashboard |
| **Backend API** | http://localhost:8080 | REST API |
| **Backend Swagger** | http://localhost:8080/swagger | API documentation |
| **Backend Health** | http://localhost:8080/health | Health check |
| **AI-Service Docs** | http://localhost:8000/docs | FastAPI Swagger UI |
| **AI-Service Health** | http://localhost:8000/health | Health check |

---

## 📤 API Response Format

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
      "positions": [[520, 280], [538, 295]],
      "plate": "789TUN012"
    }
  }
}
```

---

## 🧪 Testing

### Health Checks
```bash
# AI Service
curl http://localhost:8000/health

# Backend
curl http://localhost:8080/health
```

### Upload Test Video
```bash
curl -X POST http://localhost:8080/api/upload-video \
  -F "video=@test_video.mp4"
```

---

## 🐳 Docker Configuration

### docker-compose.yml Structure
```yaml
services:
  traffic-ai-service:       # Port 8000
  traffic-ktor-backend:     # Port 8080
  traffic-frontend:         # Port 5173
```

### Environment Variables
- **Backend**: `KTOR_AI_ENDPOINT` - AI service URL
- **Frontend**: `VITE_API_BASE` - Backend API URL

---

## ✅ v1.0 MVP Capabilities

- ✅ Offline video processing
- ✅ Vehicle detection (YOLO)
- ✅ License plate detection (YOLO)
- ✅ OCR plate recognition (PaddleOCR)
- ✅ Vehicle tracking (Centroid-based)
- ✅ Approximate speed estimation
- ✅ Speed violation detection
- ✅ End-to-end system integration
- ✅ Docker containerization
- ✅ Web dashboard
- ✅ REST API with Swagger docs

---

## 🚧 Known Limitations (v1.0)

- ⚠️ Speed estimation is pixel-based (not calibrated to real-world)
- ⚠️ No authentication or user management
- ⚠️ No database persistence
- ⚠️ No real-time camera streams
- ⚠️ CPU-only processing (no GPU acceleration)
- ⚠️ Basic UI with minimal styling
- ⚠️ No video history or filtering

---

## 🚀 Roadmap

### 📦 v1.5 — Stabilization & Enhancements (Next)
**Focus**: Improve accuracy, UX, and code quality

**AI Improvements**:
- [ ] Enhanced OCR post-processing
- [ ] Automatic plate correction (regex validation)
- [ ] YOLO confidence threshold tuning
- [ ] Improved tracking stability
- [ ] Better error handling

**Backend Improvements**:
- [ ] Enhanced logging
- [ ] Better error messages
- [ ] API response optimization
- [ ] Code cleanup and refactoring

**Frontend Improvements**:
- [ ] Professional UI design
- [ ] Better loading indicators
- [ ] Improved error messages
- [ ] Responsive design
- [ ] Video format validation

---

### 📦 v2.0 — Functional Complete Version
**Focus**: Production-ready features

**Features**:
- [ ] PostgreSQL database integration
- [ ] User authentication (JWT)
- [ ] Admin dashboard
- [ ] Violation history & filtering
- [ ] Statistics & charts
- [ ] Export reports (PDF/CSV)
- [ ] Real-world speed calibration (homography)
- [ ] Multi-lane support

---

### 📦 v3.0 — Advanced/Production Version
**Focus**: Real-time and enterprise features

**Features**:
- [ ] Live camera streams (RTSP)
- [ ] WebSocket real-time updates
- [ ] GPU acceleration (CUDA)
- [ ] Multi-camera tracking
- [ ] Night/weather adaptation
- [ ] Monitoring & observability (Grafana, Prometheus)
- [ ] Kubernetes deployment
- [ ] HTTPS & NGINX reverse proxy

---

## 🐛 Troubleshooting

### Docker Issues

**Problem**: Containers won't start
```bash
# Check Docker is running
docker --version
docker-compose --version

# Check for port conflicts
lsof -i :5173  # Frontend
lsof -i :8080  # Backend
lsof -i :8000  # AI Service

# Rebuild from scratch
docker-compose down -v
docker-compose build --no-cache
docker-compose up
```

**Problem**: AI Service takes too long
- Use shorter test videos (30-60 seconds)
- Reduce video resolution
- Consider GPU support in v2.0

**Problem**: Frontend can't connect to backend
```bash
# Check backend is accessible
curl http://localhost:8080/health

# Check CORS settings in Ktor backend
# Verify VITE_API_BASE in frontend .env
```

---

## 📊 Performance Notes

### Video Processing Times (CPU-only)
- 30-second video: ~45-90 seconds
- 1-minute video: ~90-180 seconds
- 2-minute video: ~180-360 seconds

**Note**: Processing time depends on:
- Video resolution
- Number of vehicles
- Frame rate
- CPU performance

---

## 🙏 Acknowledgments

- **YOLOv8** (Ultralytics) - Object detection
- **PaddleOCR** - Optical character recognition
- **Ktor** - Kotlin web framework
- **FastAPI** - Python web framework
- **React + Vite** - Frontend framework

---

## 📖 Documentation

- [AI Service Documentation](./ai-service/README.md)
- [Backend Documentation](./ktor-backend/README.md)
- [Frontend Documentation](./dashboard-frontend/README.md)
---

**Ready to move to v1.5? See the roadmap section above for next steps!** 🚀