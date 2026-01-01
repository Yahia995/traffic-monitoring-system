# 🚦 Traffic Monitoring System — v2.0 (Complete)

An end-to-end **intelligent traffic violation detection system** that analyzes traffic videos to detect vehicles, recognize license plates, estimate speed, and identify traffic violations with enhanced accuracy, validation, **database persistence**, and **user authentication**.

**Current Version**: v2.0 (Database & Authentication) ✅  
**Previous Version**: v1.5 (Stabilization)

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
- ✅ **PostgreSQL database for data persistence** (NEW v2.0)
- ✅ **JWT-based user authentication and authorization** (NEW v2.0)
- ✅ **Video history and violation tracking** (NEW v2.0)
- ✅ **Advanced filtering and statistics** (NEW v2.0)

---

## 🆕 What's New in v2.0

### Database Persistence
- ✅ **PostgreSQL Integration**: All analysis results stored in database
- ✅ **Video History**: Track all uploaded videos with metadata
- ✅ **Violation Storage**: Searchable violation records with filtering
- ✅ **Vehicle Tracking**: Complete vehicle and trajectory data persistence
- ✅ **Data Relationships**: Proper foreign keys and relational integrity
- ✅ **Pagination Support**: Efficient browsing of large datasets

### Authentication & Authorization
- ✅ **JWT Authentication**: Secure token-based authentication
- ✅ **User Registration**: Self-service account creation
- ✅ **User Login**: Credential-based authentication with BCrypt
- ✅ **Protected Routes**: Auth required for video uploads and V2 endpoints
- ✅ **User Isolation**: Each user sees only their own videos

### Frontend Enhancements
- ✅ **Login/Register UI**: Modern authentication interface
- ✅ **Video History View**: Browse previously analyzed videos
- ✅ **Statistics Dashboard**: Aggregate analytics and charts
- ✅ **Advanced Filtering**: Search violations by severity, date, plate, etc.
- ✅ **CSV Export**: Download violation data for reporting
- ✅ **Responsive Design**: Works on desktop, tablet, and mobile

### API Improvements
- ✅ **V2 REST Endpoints**: Complete RESTful API for videos, violations, stats
- ✅ **Pagination**: Efficient data retrieval for large datasets
- ✅ **Advanced Filtering**: Multi-criteria violation search
- ✅ **Statistics API**: Real-time analytics aggregation
- ✅ **Export Endpoints**: CSV download for violations

### Infrastructure
- ✅ **Feature Flags**: Enable/disable V2 features independently
- ✅ **Backward Compatibility**: V1.5 endpoints still work
- ✅ **Enhanced Health Checks**: Monitor database and AI service status
- ✅ **Graceful Degradation**: System works without database if V2 disabled

---

## 🧱 System Architecture

```text
┌─────────────────────────────────────────────────┐
│  Frontend Dashboard (React + Vite)              │
│  Port: 5173                                     │
│  • Modern UI with authentication                │
│  • Video history and statistics                 │
│  • Advanced filtering and export                │
└────────────────┬────────────────────────────────┘
                 │ HTTP REST + JWT
                 ↓
┌─────────────────────────────────────────────────┐
│  Backend API (Ktor + Kotlin)                    │
│  Port: 8080                                     │
│  • JWT authentication & authorization           │
│  • Request correlation tracking                 │
│  • Database persistence layer                   │
│  • Enhanced error handling                      │
└────────┬───────────────────┬────────────────────┘
         │                   │
         │ HTTP REST         │ PostgreSQL
         ↓                   ↓
┌────────────────┐   ┌──────────────────┐
│  AI-Service    │   │  PostgreSQL DB   │
│  Port: 8000    │   │  Port: 5432      │
│  • YOLOv8      │   │  • Users         │
│  • PaddleOCR   │   │  • Videos        │
│  • Tracking    │   │  • Violations    │
│  • Speed Est.  │   │  • Vehicles      │
└────────────────┘   └──────────────────┘
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
**Version**: 2.0.0  
**Responsibilities**:
- Video upload endpoints (full & summary)
- JWT authentication and user management
- Database persistence (videos, violations, vehicles)
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
**Version**: 2.0.0  
**Responsibilities**:
- Modern, responsive web interface
- User authentication (login/register)
- Video upload with drag & drop
- Summary statistics dashboard
- Interactive violations table
- Detailed vehicle tracking view
- Video history browser
- Statistics and analytics dashboard
- Advanced violation filtering
- CSV export functionality
- Real-time progress tracking

📁 **Location**: `dashboard-frontend/`  
📖 **Documentation**: [dashboard-frontend/README.md](./dashboard-frontend/README.md)

---

### 💾 Database (PostgreSQL)
**Port**: 5432  
**Version**: 15-alpine  
**Responsibilities**:
- Store user accounts and credentials
- Persist video analysis results
- Store violations with full metadata
- Track vehicle data and trajectories
- Support advanced querying and filtering
- Maintain data relationships and integrity

📁 **Location**: Managed by Docker Compose  
**Schema**: Defined in `ktor-backend/src/main/kotlin/database/Tables.kt`

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

### 2️⃣ Configure Environment
```bash
# Copy example environment file
cp .env.example .env

# Edit if needed (defaults work for development)
nano .env
```

Key configurations:
```bash
# Feature Flags
ENABLE_V2_PERSISTENCE=true       # Enable database
ENABLE_AUTHENTICATION=true       # Enable auth

# Database
DB_PASSWORD=postgres             # Change in production

# JWT
JWT_SECRET=your-secret-key       # MUST change in production
```

### 3️⃣ Start All Services
```bash
docker-compose up --build
```

This will start:
- **PostgreSQL Database** on `http://localhost:5432`
- **AI-Service** on `http://localhost:8000`
- **Backend API** on `http://localhost:8080`
- **Frontend Dashboard** on `http://localhost:5173`

### 4️⃣ Access the Dashboard
Open your browser to: **http://localhost:5173**

1. **Register** a new account (email + password)
2. **Login** with your credentials
3. **Upload** a traffic video for analysis
4. **View** results, violations, and statistics
5. **Browse** video history
6. **Filter** violations by various criteria
7. **Export** violation data as CSV

### 5️⃣ Stop Services
```bash
docker-compose down
```

To also remove volumes (database data):
```bash
docker-compose down -v
```

---

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | Web dashboard |
| **Backend API** | http://localhost:8080 | REST API |
| **Backend Health** | http://localhost:8080/health | Basic health check |
| **Backend Detailed Health** | http://localhost:8080/health/detailed | Health with DB/AI status |
| **Backend Swagger** | http://localhost:8080/swagger | API documentation |
| **AI-Service Docs** | http://localhost:8000/docs | FastAPI Swagger UI |
| **AI-Service Health** | http://localhost:8000/health | Health check |
| **PostgreSQL** | localhost:5432 | Database (credentials in .env) |

---

## 📤 API Response Format (v2.0)

### Video Upload Response (V1.5 Compatible)
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
  "tracked_vehicles": [...],
  "configuration": {...}
}
```

### V2 API Responses

#### Authentication Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresAt": 1640003600000,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "role": "USER",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

#### Video History Response
```json
{
  "data": [
    {
      "id": "uuid",
      "filename": "traffic.mp4",
      "uploadedAt": "2024-01-01T12:00:00",
      "summary": {
        "totalVehicles": 12,
        "vehiclesWithPlates": 8,
        "violations": 2,
        "averageSpeed": 48.5
      }
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 50,
  "totalPages": 3
}
```

#### Statistics Response
```json
{
  "total": 150,
  "averageSpeed": 65.3,
  "bySeverity": {
    "LOW": 50,
    "MEDIUM": 60,
    "HIGH": 30,
    "CRITICAL": 10
  }
}
```

---

## ✅ v2.0 Features

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

### User Management (NEW v2.0)
- ✅ User registration with email/password
- ✅ Secure login with JWT tokens
- ✅ Password hashing with BCrypt
- ✅ Token expiration (1 hour default)
- ✅ User isolation (see only own data)

### Data Persistence (NEW v2.0)
- ✅ PostgreSQL database integration
- ✅ Video metadata storage
- ✅ Violation records with full details
- ✅ Vehicle tracking data persistence
- ✅ Trajectory point storage
- ✅ User-video relationships

### User Interface
- ✅ Modern, gradient-based design
- ✅ Login/Register authentication pages
- ✅ Drag & drop video upload
- ✅ Real-time progress tracking
- ✅ Summary statistics dashboard
- ✅ Interactive violations table with sorting
- ✅ Expandable vehicle details
- ✅ Video history browser with pagination
- ✅ Statistics dashboard with charts
- ✅ Advanced violation filtering
- ✅ CSV export functionality
- ✅ Responsive mobile design

### System Features
- ✅ Docker containerization
- ✅ Health check monitoring (DB + AI)
- ✅ Request correlation tracking
- ✅ Enhanced error handling
- ✅ Response validation
- ✅ Comprehensive logging
- ✅ Feature flags for V2 features
- ✅ Backward compatibility with V1.5

---

## 🔧 Configuration

All services are configured via environment variables in `.env` file:

### Feature Flags
```bash
ENABLE_V2_PERSISTENCE=true       # Enable database features
ENABLE_AUTHENTICATION=true       # Enable user authentication
```

### Database Configuration
```bash
DB_HOST=traffic-postgres
DB_PORT=5432
DB_NAME=traffic_monitoring
DB_USER=postgres
DB_PASSWORD=postgres             # Change in production!
DB_POOL_SIZE=10
```

### JWT Configuration
```bash
JWT_SECRET=your-secret-key-change-in-production
JWT_ISSUER=traffic-monitoring-system
JWT_AUDIENCE=traffic-monitoring-api
JWT_VALIDITY_MS=3600000          # 1 hour
```

### AI Configuration
```bash
SPEED_LIMIT=50.0                 # Speed limit in km/h
PIXEL_TO_METER=0.05             # Calibration factor
VEHICLE_CONFIDENCE=0.35         # Detection threshold
PLATE_CONFIDENCE=0.25           # Plate detection threshold
OCR_CONFIDENCE=0.5              # OCR threshold
OCR_MULTI_PASS=true             # Enable multi-pass OCR
```

See `.env.example` for full configuration options.

---

## 🧪 Testing

### Health Checks
```bash
# Backend basic health
curl http://localhost:8080/health

# Backend detailed health (includes DB and AI)
curl http://localhost:8080/health/detailed

# AI service health
curl http://localhost:8000/health

# Database health (via backend)
curl http://localhost:8080/health/detailed | jq '.services.database'
```

### Authentication Flow
```bash
# 1. Register user
curl -X POST http://localhost:8080/api/v2/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# 2. Login (save the token from response)
curl -X POST http://localhost:8080/api/v2/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Response contains token
```

### Video Upload (Authenticated)
```bash
TOKEN="your-jwt-token-here"

# Upload video
curl -X POST http://localhost:8080/api/upload-video \
  -H "Authorization: Bearer $TOKEN" \
  -F "video=@test_video.mp4"
```

### V2 API Endpoints
```bash
# List user's videos
curl http://localhost:8080/api/v2/videos?page=0&size=10 \
  -H "Authorization: Bearer $TOKEN"

# Get statistics
curl http://localhost:8080/api/v2/stats \
  -H "Authorization: Bearer $TOKEN"

# Filter violations
curl -X POST http://localhost:8080/api/v2/violations/filter \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "severity": "HIGH",
    "page": 0,
    "size": 20
  }'

# Export violations as CSV
curl http://localhost:8080/api/v2/violations/export/csv \
  -H "Authorization: Bearer $TOKEN" \
  -o violations.csv
```

---

## ⚠️ Known Limitations

### V2.0
- ⚠️ Speed estimation is pixel-based (not calibrated to real-world)
- ⚠️ No refresh token mechanism (tokens expire after 1 hour)
- ⚠️ No password reset functionality
- ⚠️ No email verification
- ⚠️ No role-based permissions (all users have same access)
- ⚠️ No real-time camera streams
- ⚠️ CPU-only processing (no GPU acceleration)
- ⚠️ No multi-tenancy support

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
- OCR processing (multi-pass increases time)

**Optimization tips**:
- Use H.264 encoded videos
- Reduce resolution if processing is slow
- Adjust `FRAME_SKIP` to process every 2nd frame
- Disable `OCR_MULTI_PASS` for faster (less accurate) OCR
- Ensure adequate CPU resources in Docker

### Database Performance
- **Connection Pool**: 10 connections (configurable)
- **Typical Query Time**: < 100ms for paginated lists
- **Video Save Time**: ~200-500ms depending on data size
- **Recommended Index**: Automatic on foreign keys and timestamps

---

## 🚀 Roadmap

### ✅ v2.0 — Database & Authentication (COMPLETED)
- ✅ PostgreSQL integration
- ✅ JWT authentication
- ✅ User management
- ✅ Video history
- ✅ Violation tracking
- ✅ Statistics API
- ✅ Advanced filtering
- ✅ CSV export

### 📦 v2.1 — Enhanced Security & UX (Next)
**Focus**: Security improvements and better user experience

**Features**:
- [ ] Refresh token support
- [ ] Password reset via email
- [ ] Email verification
- [ ] User profile management
- [ ] Remember me functionality
- [ ] Session management
- [ ] Audit logging
- [ ] Rate limiting

### 📦 v3.0 — Real-time & Production
**Focus**: Live monitoring and enterprise features

**Features**:
- [ ] Live camera streams (RTSP)
- [ ] WebSocket real-time updates
- [ ] GPU acceleration (CUDA)
- [ ] Multi-camera tracking
- [ ] Night/weather adaptation
- [ ] Real-world speed calibration (homography)
- [ ] Role-based access control (RBAC)
- [ ] Multi-tenancy support
- [ ] Monitoring & observability (Grafana, Prometheus)
- [ ] Kubernetes deployment
- [ ] HTTPS & NGINX reverse proxy
- [ ] Rate limiting and caching
- [ ] Video streaming integration
- [ ] Advanced analytics dashboard
- [ ] Mobile app

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
lsof -i :5432  # PostgreSQL
```

**Database connection fails**:
```bash
# Check PostgreSQL is running
docker-compose ps

# Check database logs
docker-compose logs traffic-postgres

# Test connection
docker-compose exec traffic-postgres psql -U postgres -d traffic_monitoring -c "SELECT 1;"
```

### Authentication Issues

**Cannot login after registration**:
- Check backend logs: `docker-compose logs traffic-ktor-backend`
- Verify JWT_SECRET is set in .env
- Ensure ENABLE_AUTHENTICATION=true
- Check database has users table

**Token expired errors**:
- JWT tokens expire after 1 hour by default
- Login again to get a new token
- Configure JWT_VALIDITY_MS for longer expiration

**Upload returns 401 Unauthorized**:
- Ensure you're logged in
- Check token is being sent in Authorization header
- Verify token hasn't expired
- Check browser console for errors

### Frontend Issues

**Cannot connect to backend**:
```bash
# Check backend health
curl http://localhost:8080/health/detailed

# Check VITE_API_BASE in frontend
docker-compose logs traffic-frontend | grep VITE_API_BASE
```

**History page is empty**:
- Ensure you're logged in with the same account that uploaded videos
- Check backend logs for errors
- Verify ENABLE_V2_PERSISTENCE=true
- Check database has data: `docker-compose exec traffic-postgres psql -U postgres -d traffic_monitoring -c "SELECT COUNT(*) FROM videos;"`

**Upload fails**:
- Ensure video format is supported (.mp4, .avi, .mov, .mkv)
- Check file size (max 200 MB)
- Verify backend and AI service are running
- Check browser network tab for errors

---

## 📖 Documentation

- [Main README](./README.md) - This file
- [AI Service Documentation](./ai-service/README.md) - OCR, detection, tracking
- [Backend Documentation](./ktor-backend/README.md) - API, auth, database
- [Frontend Documentation](./dashboard-frontend/README.md) - UI, components, styling

---

## 🙏 Acknowledgments

- **YOLOv8** (Ultralytics) - Object detection framework
- **PaddleOCR** - Optical character recognition
- **Ktor** - Kotlin web framework
- **FastAPI** - Python web framework
- **React + Vite** - Frontend framework
- **PostgreSQL** - Database system
- **JWT (Auth0)** - Authentication library

---

**System Status**: v2.0 Complete ✅  
**Features**: Full-stack with Authentication + Database + Advanced Analytics 🚀  
**Next**: v2.1 Enhanced Security & UX 📋