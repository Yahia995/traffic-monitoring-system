# 🚦 Ktor Backend — Traffic Monitoring System

**Current Version**: v1.5 (Stabilization) ✅  
**Next Version**: v2.0 (Database & Authentication) 🚧

This module represents the **Backend service** of the Traffic Monitoring System. It acts as an **orchestrator** between the **AI-Service (FastAPI)** and the **Frontend Dashboard**, handling video uploads, AI communication, error management, and API exposure with enhanced reliability and observability.

---

## 🆕 What's New in v1.5

### Enhanced Features
- ✅ **Request Correlation Tracking**: Unique IDs for tracing requests through the system
- ✅ **Enhanced Error Handling**: User-friendly error messages with detailed context
- ✅ **Response Validation**: Automatic consistency checks for AI responses
- ✅ **Detailed Health Checks**: Monitor both backend and AI service status
- ✅ **Summary Endpoint**: Quick statistics without full response
- ✅ **Structured Logging**: Better debugging with correlation IDs and metrics
- ✅ **Graceful Shutdown**: Proper resource cleanup on service stop

### Updated Data Models
- ✅ Migrated to v1.5 AI response format (arrays instead of objects)
- ✅ Added confidence scores and validation status
- ✅ Added severity classification
- ✅ Added processing metadata and statistics

---

## 🎯 Responsibilities

- ✅ Expose REST APIs for frontend consumption
- ✅ Receive traffic videos from clients
- ✅ Forward videos to AI-Service with correlation tracking
- ✅ Validate and enrich AI analysis results
- ✅ Centralize logging and error handling
- ✅ Provide comprehensive health monitoring
- ✅ Serve Swagger/OpenAPI documentation
- ✅ CORS management for frontend access
- ✅ Docker containerization
- 📅 Database integration (v2.0)
- 📅 User authentication (v2.0)

---

## 🧱 Tech Stack

- **Kotlin 1.9.23**
- **Ktor 2.3.13**
- **Apache HTTP Client**
- **kotlinx.serialization**
- **Logback** (structured logging)
- **Swagger UI (OpenAPI 3)**
- **Gradle 8.14**
- **Docker** (multi-stage build)

---

## 📡 API Endpoints

### ✅ Basic Health Check
**URL**: `GET /health`

**Description**: Check if backend is running

**Response**:
```json
{
  "status": "OK",
  "version": "1.5.0",
  "timestamp": 1640000000000
}
```

---

### ✅ Detailed Health Check (NEW in v1.5)
**URL**: `GET /health/detailed`

**Description**: Check backend and AI service health

**Response**:
```json
{
  "status": "OK",
  "version": "1.5.0",
  "timestamp": 1640000000000,
  "services": {
    "backend": "OK",
    "ai_service": "OK"
  }
}
```

**Status Codes**:
- `200 OK`: All services healthy
- `503 Service Unavailable`: AI service unavailable

---

### 🎥 Upload & Analyze Video (Enhanced)
**URL**: `POST /api/upload-video`

**Description**: Upload video for complete AI analysis

**Request**:
- **Content-Type**: `multipart/form-data`
- **Field**: `video` (file)
- **Supported formats**: `.mp4`, `.avi`, `.mov`, `.mkv`
- **Max size**: 200 MB

**Enhanced Processing**:
1. Request correlation ID generated
2. File validation (format, size)
3. Forward to AI-Service with tracking
4. Receive and validate AI response
5. Log metrics and statistics
6. Return enriched response

**Response** (v1.5 format):
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

**Status Codes**:
- `200 OK`: Video processed successfully
- `400 Bad Request`: Invalid file format or too large
- `503 Service Unavailable`: AI-Service not reachable
- `504 Gateway Timeout`: Processing timeout (>10 min)
- `502 Bad Gateway`: AI-Service error

---

### 📊 Upload Summary Only (NEW in v1.5)
**URL**: `POST /api/upload-video/summary`

**Description**: Upload video and get summary statistics only (faster response)

**Request**: Same as full upload

**Response**:
```json
{
  "success": true,
  "violations_count": 2,
  "processing_time_seconds": 45.3,
  "vehicles_tracked": 12,
  "plates_detected": 8,
  "video_duration_seconds": 30.5
}
```

---

## 🚀 Enhanced Features (v1.5)

### 1. Request Correlation Tracking
Every request gets a unique correlation ID for end-to-end tracing:

**Log Example**:
```
[abc-123-def] ▶ Received upload request
[abc-123-def] ℹ Processing video 'traffic.mp4' (15.34 MB)
[abc-123-def] ✓ Upload complete in 45.3s: violations=2 vehicles=12
```

### 2. Enhanced Error Handling
User-friendly error messages with actionable details:

**Example Error Response**:
```json
{
  "code": "AI_TIMEOUT",
  "message": "AI processing took too long",
  "timestamp": 1640000000000,
  "details": "The video analysis exceeded the maximum processing time (10 minutes). Try uploading a shorter video or reducing its resolution."
}
```

**Error Types**:
- `BAD_REQUEST`: Invalid file format or parameters
- `AI_UNAVAILABLE`: AI service not reachable
- `AI_UNREACHABLE`: Invalid AI endpoint
- `AI_TIMEOUT`: Processing timeout
- `AI_ERROR`: AI service returned error
- `INTERNAL_ERROR`: Backend error
- `UNEXPECTED_ERROR`: Unknown error

### 3. Response Validation
Automatic validation of AI responses:
- Status verification
- Count consistency checks
- Confidence threshold warnings
- Validation status tracking

**Validation Warnings**:
```
[abc-123-def] Response validation warnings:
[abc-123-def]   - Low confidence violation: plate=123TUN456 confidence=0.45
[abc-123-def]   - Unvalidated violation: plate=789ABC012
```

### 4. Structured Logging
Enhanced logging with context:

**Log Format**:
```
HH:mm:ss LEVEL logger-name - message
```

**Example**:
```
14:30:45 INFO  AIClient - [abc-123] AI ▶ Sending video 'traffic.mp4' (15.34 MB)
14:31:30 INFO  AIClient - [abc-123] AI ◀ Received response 200 OK in 45.0s
14:31:30 INFO  AIClient - [abc-123] AI ℹ Results: violations=2 vehicles=12 plates=8
```

---

## 🔧 Configuration

### application.conf
```hocon
ktor {
  deployment {
    port = 8080
    host = "0.0.0.0"
  }

  application {
    modules = [ com.traffic.ApplicationKt.module ]
  }

  ai {
    endpoint = "http://localhost:8000/api/process-video"
    timeout = 600000  # 10 minutes in milliseconds
  }
}
```

### Environment Variables
- `KTOR_AI_ENDPOINT`: Override AI service URL
- `KTOR_AI_TIMEOUT_MS`: Override timeout (default: 600000)
- `LOG_LEVEL`: Logging level (default: INFO)

**Docker Example**:
```yaml
environment:
  KTOR_AI_ENDPOINT: http://traffic-ai-service:8000/api/process-video
  KTOR_AI_TIMEOUT_MS: 600000
  LOG_LEVEL: INFO
```

---

## 🏗️ Project Structure

```text
ktor-backend/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/traffic/
│       │       ├── client/
│       │       │   └── AIClient.kt          # Enhanced AI communication
│       │       ├── models/
│       │       │   ├── AIRequest.kt         
│       │       │   ├── AIResponse.kt        # v1.5 data models
│       │       │   ├── ErrorResponse.kt     # Enhanced error format
│       │       │   └── HealthResponse.kt    # Enhanced health format
│       │       ├── plugins/
│       │       │   ├── CallLogging.kt       # Request logging
│       │       │   ├── CORS.kt              # Cross-origin config
│       │       │   ├── Routing.kt           # Route registration
│       │       │   ├── Serialization.kt     # JSON config
│       │       │   ├── StatusPages.kt       # Enhanced error handling
│       │       │   └── Swagger.kt           # API docs
│       │       ├── routes/
│       │       │   ├── AIRoutes.kt          # Video upload endpoints
│       │       │   └── HealthRoutes.kt      # Health endpoints
│       │       └── Application.kt           # Enhanced entry point
│       └── resources/
│           ├── application.conf
│           ├── logback.xml                  # Structured logging
│           └── swagger/
│               └── documentation.yaml       # v1.5 API spec
├── build.gradle.kts
├── Dockerfile                               # Multi-stage build
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

## ⚡ Running Locally

### Prerequisites
- JDK 17+
- Gradle 8+ (or use wrapper)

### 1️⃣ Start the Server
```bash
cd ktor-backend
./gradlew run
```

### 2️⃣ Access the API
- **Base URL**: http://localhost:8080
- **Health**: http://localhost:8080/health
- **Detailed Health**: http://localhost:8080/health/detailed
- **Swagger**: http://localhost:8080/swagger

---

## 🐳 Running with Docker

### Build Image
```bash
docker build -t traffic-ktor-backend .
```

### Run Container
```bash
docker run -p 8080:8080 \
  -e KTOR_AI_ENDPOINT=http://host.docker.internal:8000/api/process-video \
  traffic-ktor-backend
```

### Multi-Stage Build Benefits
- ✅ Smaller final image (~200MB)
- ✅ No build tools in runtime
- ✅ Faster deployments
- ✅ Better security

---

## 🧪 Testing

### Health Checks
```bash
# Basic health
curl http://localhost:8080/health

# Detailed health (includes AI service)
curl http://localhost:8080/health/detailed
```

### Upload Video (Full Response)
```bash
curl -X POST http://localhost:8080/api/upload-video \
  -F "video=@test_video.mp4"
```

### Upload Video (Summary Only)
```bash
curl -X POST http://localhost:8080/api/upload-video/summary \
  -F "video=@test_video.mp4"
```

---

## 🐛 Troubleshooting

### Issue: "AI_UNAVAILABLE"
**Cause**: AI service not reachable

**Solution**:
```bash
# Check AI service
curl http://localhost:8000/health

# Check detailed health to see AI status
curl http://localhost:8080/health/detailed

# Verify KTOR_AI_ENDPOINT
echo $KTOR_AI_ENDPOINT
```

### Issue: Port 8080 already in use
**Solution**:
```bash
# Find process
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Issue: Logs not showing
**Solution**:
Check `logback.xml` configuration or adjust `LOG_LEVEL` environment variable.

---

## 📊 Performance Metrics

### Request Processing
- Video receive: < 1 second
- AI forward: ~0.5 seconds
- AI processing: 45-180 seconds (video-dependent)
- Response validation: < 0.1 seconds
- Response return: < 1 second

### Memory Usage
- Idle: ~150 MB
- Processing video: +100-200 MB
- Peak: ~400 MB

---

## 🚀 v2.0 Roadmap

### Planned Features
- [ ] PostgreSQL database integration
- [ ] JWT authentication
- [ ] User management
- [ ] Violation history storage
- [ ] Advanced filtering and pagination
- [ ] Statistics endpoints
- [ ] Report generation
- [ ] Caching layer (Redis)
- [ ] Rate limiting
- [ ] WebSocket support for real-time updates

---

## 📖 Dependencies

```kotlin
// Server
implementation("io.ktor:ktor-server-core-jvm:2.3.13")
implementation("io.ktor:ktor-server-netty-jvm:2.3.13")

// Serialization
implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.13")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.13")

// Plugins
implementation("io.ktor:ktor-server-call-logging-jvm:2.3.13")
implementation("io.ktor:ktor-server-cors-jvm:2.3.13")
implementation("io.ktor:ktor-server-status-pages:2.3.13")
implementation("io.ktor:ktor-server-swagger:2.3.13")

// Client
implementation("io.ktor:ktor-client-core-jvm:2.3.13")
implementation("io.ktor:ktor-client-apache-jvm:2.3.13")
implementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.13")

// Logging
implementation("ch.qos.logback:logback-classic:1.5.6")
```

---

## 🔗 Integration Points

### With AI-Service
- **Endpoint**: `POST http://traffic-ai-service:8000/api/process-video`
- **Format**: `multipart/form-data`
- **Timeout**: 600 seconds (10 minutes)
- **Headers**: `X-Correlation-ID` for tracking

### With Frontend
- **Base URL**: `http://traffic-ktor-backend:8080`
- **CORS**: Enabled for all origins (MVP)
- **Content-Type**: `application/json`

---

**Backend Status**: v1.5 Stabilization Complete ✅  
**Ready for**: v2.0 Database Integration 🚀