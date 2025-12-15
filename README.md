# 🚦 Traffic Monitoring System — MVP

An end-to-end **intelligent traffic violation detection system** that analyzes traffic videos to detect vehicles, recognize license plates, estimate speed, and identify traffic violations.

This project is developed as a **modular MVP**, designed to be easily extendable toward a production-grade, real-time system.

---

## 🎯 Project Objectives

- Detect vehicles in traffic videos
- Detect and recognize license plates
- Track vehicles across video frames
- Estimate vehicle speed
- Detect speeding violations
- Display results in a simple web dashboard
- Provide a clean technical foundation for future extensions

---

## 🧱 System Architecture

```text
[ Frontend Dashboard (React) ]
            ↓
[ Backend API (Ktor) ]
            ↓
[ AI-Service (FastAPI / Python) ]
```

Each module is independently developed and communicates via **HTTP REST APIs**.

---

## 📦 Project Modules

### 🤖 AI-Service (Python / FastAPI)

Responsible for all computer vision and AI processing:

- Vehicle detection (YOLO)
- License plate detection
- OCR (PaddleOCR)
- Vehicle tracking
- Speed estimation
- Speed violation detection

**API**
- `POST /api/process-video`

📁 **Location**: `ai-service/`  
📖 **Documentation**: [ai-service/README.md](./ai-service/README.md)

---

### ⚙️ Backend API (Ktor / Kotlin)

Acts as the system orchestrator:

- Receives videos from frontend
- Forwards videos to AI-Service
- Returns analysis results
- Centralized error handling
- Swagger / OpenAPI documentation

**API**
- `POST /api/upload-video`
- `GET /api/health`

📁 **Location**: `ktor-backend/`  
📖 **Documentation**: [ktor-backend/README.md](./ktor-backend/README.md)

---

### 🖥️ Dashboard Frontend (React / Vite)

Provides a minimal user interface:

- Video upload
- Violations table
- Raw JSON display (debug/demo)

📁 **Location**: `dashboard-frontend/`  
📖 **Documentation**: [dashboard-frontend/README.md](./dashboard-frontend/README.md)

---

## 🏗️ Repository Structure

```text
traffic-monitoring-system/
│
├── ai-service/
│   ├── app.py
│   ├── requirements.txt
│   └── README.md
│
├── ktor-backend/
│   ├── src/
│   ├── build.gradle.kts
│   └── README.md
│
├── dashboard-frontend/
│   ├── src/
│   ├── package.json
│   └── README.md
│
└── README.md   # this file
```

---

## 🔄 End-to-End Flow

```
User uploads a video
        ↓
Frontend sends video to Backend
        ↓
Backend forwards video to AI-Service
        ↓
AI analyzes the video
        ↓
Backend returns JSON response
        ↓
Frontend displays detected violations
```

---

## 📤 AI Response Format (MVP)

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
      "positions": [[412, 318], [430, 340]],
      "plate": "123TUN456"
    }
  }
}
```

---

## ⚡ Running the MVP (Local Development)

### Prerequisites
- Python 3.10.11
- Node.js 18+
- Kotlin / JDK 17+

---

### 1️⃣ Start AI-Service

```bash
cd ai-service

# Create and activate virtual environment
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run server
uvicorn app:app --host 0.0.0.0 --port 8000
```

---

### 2️⃣ Start Backend

```bash
cd ktor-backend

# Run with Gradle
./gradlew run
```

---

### 3️⃣ Start Frontend

```bash
cd dashboard-frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

---

## 🌐 Access URLs

Once all services are running:

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | Web dashboard |
| **Backend API** | http://localhost:8080 | REST API |
| **Swagger UI** | http://localhost:8080/swagger | API documentation |
| **AI API Docs** | http://localhost:8000/docs | FastAPI docs |

---

## 🧪 MVP Capabilities

✅ Offline video processing  
✅ Vehicle and license plate detection  
✅ OCR-based plate recognition  
✅ Vehicle tracking  
✅ Approximate speed estimation  
✅ Speed violation detection  
✅ End-to-end system integration  

---

## 🚧 Known Limitations (MVP)

⚠️ Speed estimation is approximate (pixel-based)  
⚠️ No authentication or user management  
⚠️ No database persistence  
⚠️ No real-time processing  
⚠️ No Docker or containerization yet  

---

## 🚀 Roadmap

### v1.5 — Stabilization
- [ ] Improve OCR accuracy
- [ ] Tracking stability improvements
- [ ] UI refinements
- [ ] Better error handling

### v2 — Functional Version
- [ ] Database integration (PostgreSQL)
- [ ] Authentication (JWT)
- [ ] Violation history & filtering
- [ ] Admin dashboard

### v3 — Advanced / Production
- [ ] Live camera streams (RTSP)
- [ ] WebSocket live updates
- [ ] GPU acceleration
- [ ] Multi-camera tracking
- [ ] Monitoring & observability

---

## 🧪 Testing

### Manual Testing

**Health Checks:**
```bash
# AI Service
curl http://localhost:8000/api/health

# Backend
curl http://localhost:8080/api/health
```

**Upload Test Video:**
```bash
curl -X POST http://localhost:8080/api/upload-video \
  -F "video=@test_video.mp4"
```

---

## 🐛 Troubleshooting

### Common Issues

**AI Service fails to start**
- Check Python version (3.10.11)
- Verify all dependencies are installed
- Check port 8000 is not in use

**Backend cannot connect to AI Service**
- Verify AI Service is running on port 8000
- Check `AI_SERVICE_URL` environment variable

**Video upload fails**
- Verify video format (mp4, avi, mov)
- Check file size limits
- Review browser console for errors

**Slow processing**
- Use shorter videos for testing
- Reduce video resolution
- Consider GPU acceleration (future)

---

## 📖 Documentation

- [AI Service Documentation](./ai-service/README.md)
- [Backend Documentation](./ktor-backend/README.md)
- [Frontend Documentation](./dashboard-frontend/README.md)

---

## 🙏 Acknowledgments

- **YOLOv8** - Object detection framework
- **PaddleOCR** - OCR solution
- **Ktor** - Kotlin web framework
- **React** - UI library
- **FastAPI** - Python web framework

---

## 📈 Project Status

**Current Version**: v1.0.0 (MVP)  
**Status**: ✅ Functional MVP  
**Next Release**: v1.5 (Stabilization)