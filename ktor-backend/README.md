# 🚦 Ktor Backend — Traffic Monitoring System

**Current Version**: v1.0 (MVP) ✅  
**Next Version**: v1.5 (Stabilization & Enhancements) 🚧

This module represents the **Backend service** of the Traffic Monitoring System. It acts as an **orchestrator** between the **AI-Service (FastAPI)** and the **Frontend Dashboard**, handling video uploads, AI communication, error management, and API exposure.

The backend is built with **Ktor (Kotlin)** and follows a clean, modular architecture suitable for MVP and future scalability.

---

## 🎯 Responsibilities

- ✅ Expose REST APIs for frontend consumption
- ✅ Receive traffic videos from clients
- ✅ Forward videos to AI-Service
- ✅ Return AI analysis results
- ✅ Centralize logging and error handling
- ✅ Provide Swagger/OpenAPI documentation
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
- **Logback**
- **Swagger UI (OpenAPI 3)**
- **Gradle 8.14**
- **Docker**

---

## 🏗️ Project Structure

```text
ktor-backend/
│
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/traffic/
│       │       ├── client/
│       │       │   └── AIClient.kt           # HTTP client to AI-Service
│       │       │
│       │       ├── models/
│       │       │   ├── AIResponse.kt         # AI response models
│       │       │   ├── HealthResponse.kt
│       │       │   └── AIRequest.kt
│       │       │
│       │       ├── plugins/
│       │       │   ├── CallLogging.kt        # Request logging
│       │       │   ├── CORS.kt               # Cross-origin config
│       │       │   ├── Routing.kt            # Route registration
│       │       │   ├── Serialization.kt      # JSON config
│       │       │   ├── StatusPages.kt        # Error handling
│       │       │   └── Swagger.kt            # API docs
│       │       │
│       │       ├── routes/
│       │       │   ├── AIRoutes.kt           # Video upload endpoint
│       │       │   └── HealthRoutes.kt       # Health check
│       │       │
│       │       └── Application.kt            # Ktor entry point
│       │
│       └── resources/
│           ├── application.conf              # Server config
│           ├── logback.xml                   # Logging config
│           └── swagger/
│               └── documentation.yaml        # API spec
│
├── gradle/                                   # Gradle wrapper
├── build.gradle.kts                          # Build configuration
├── gradle.properties                         # Gradle properties
├── settings.gradle.kts
├── Dockerfile                                # Docker build
└── README.md                                 # This file
```

---

## ⚙️ Configuration

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
  }
}
```

### Environment Variables (Docker)
- `KTOR_AI_ENDPOINT`: Override AI service URL
  - Default: `http://localhost:8000/api/process-video`
  - Docker: `http://traffic-ai-service:8000/api/process-video`

---

## 🪵 Logging Configuration

### Logback Setup (`logback.xml`)
```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss} %-5level %logger{20} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="STDOUT"/>
  </root>

  <!-- Ktor logs -->
  <logger name="io.ktor" level="INFO"/>
  
  <!-- Netty (quiet) -->
  <logger name="io.netty" level="WARN"/>
</configuration>
```

### Log Levels
- **INFO**: Application events, AI communication
- **WARN**: Network issues
- **ERROR**: Critical failures

### Example Logs
```
INFO  [Application] Using AI endpoint: http://ai-service:8000
INFO  [AIRoutes] Received video 'traffic.mp4' (15.34 MB)
INFO  [AIClient] AI ▶ Sending video 'traffic.mp4' (15.34 MB)
INFO  [AIClient] AI ◀ Received response 200 OK for 'traffic.mp4'
```

---

## 🔄 Application Startup Flow

```text
1. EngineMain.main(args)
   ↓
2. Load application.conf
   ↓
3. Get AI_ENDPOINT (env or config)
   ↓
4. Initialize AIClient
   ↓
5. Install Plugins:
   - Serialization (JSON)
   - CORS
   - StatusPages (error handling)
   - CallLogging
   - Routing
   - Swagger
   ↓
6. Server ready on 0.0.0.0:8080
```

---

## 📡 API Endpoints

### ✅ Health Check
**URL**: `GET /health`

**Description**: Check if backend is running

**Response**:
```json
{
  "status": "OK"
}
```

**Status Codes**:
- `200 OK`: Service is healthy

---

### 🎥 Upload & Analyze Video
**URL**: `POST /api/upload-video`

**Description**: Upload a video for traffic analysis

**Request**:
- **Content-Type**: `multipart/form-data`
- **Field**: `video` (file)
- **Supported formats**: `.mp4`, `.avi`, `.mov`, `.mkv`

**Processing Flow**:
```
1. Backend receives video from frontend
2. Validates file format
3. Logs video info (name, size)
4. Forwards to AI-Service
5. Receives AI response
6. Returns JSON to frontend
```

**Response** (Proxied from AI-Service):
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
      "positions": [[412, 318], [430, 340]],
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

**Status Codes**:
- `200 OK`: Video processed successfully
- `400 Bad Request`: Invalid file format
- `503 Service Unavailable`: AI-Service not reachable
- `504 Gateway Timeout`: AI-Service timeout (>10 min)
- `502 Bad Gateway`: AI-Service error

---

## 🤖 AI-Service Integration

### AIClient Features
- ✅ Multipart video upload
- ✅ Dynamic content-type detection
- ✅ Long timeout support (10 minutes)
- ✅ Robust error handling
- ✅ Structured logging
- ✅ Auto-retry logic (future)

### Communication Flow
```
Frontend → Ktor Backend → AI-Service
   (video)      (video)        ↓
                            Processing
                               ↓
Frontend ← Ktor Backend ← AI-Service
  (JSON)      (JSON)       (JSON)
```

### Timeout Configuration
```kotlin
HttpTimeout {
  requestTimeoutMillis = 600_000  // 10 minutes
  connectTimeoutMillis = 30_000   // 30 seconds
  socketTimeoutMillis = 600_000   // 10 minutes
}
```

---

## 🧯 Error Handling

### Centralized Error Management
All errors are handled by **StatusPages** plugin.

### Error Response Format
```json
{
  "code": "ERROR_CODE",
  "message": "Human-readable description"
}
```

### Handled Exceptions

| Exception | HTTP Status | Code | Description |
|-----------|-------------|------|-------------|
| `IllegalArgumentException` | 400 | `BAD_REQUEST` | Invalid file format |
| `ConnectException` | 503 | `AI_UNAVAILABLE` | AI-Service not running |
| `UnresolvedAddressException` | 503 | `AI_UNREACHABLE` | Invalid AI endpoint |
| `HttpRequestTimeoutException` | 504 | `AI_TIMEOUT` | AI took too long |
| `ResponseException` | 502 | `AI_ERROR` | AI returned error |
| `RuntimeException` | 500 | `INTERNAL_ERROR` | Backend error |
| `Throwable` | 500 | `UNEXPECTED_ERROR` | Unknown error |

### Error Examples

**AI-Service Down**:
```json
{
  "code": "AI_UNAVAILABLE",
  "message": "FastAPI backend is not running"
}
```

**Invalid Video Format**:
```json
{
  "code": "BAD_REQUEST",
  "message": "Invalid file in request"
}
```

**AI Timeout**:
```json
{
  "code": "AI_TIMEOUT",
  "message": "FastAPI did not respond in time"
}
```

---

## 🔐 CORS Policy (MVP)

### Current Configuration (Development)
```kotlin
install(CORS) {
  allowHeader(HttpHeaders.ContentType)
  allowMethod(HttpMethod.Get)
  allowMethod(HttpMethod.Post)
  anyHost()  // ⚠️ Allow all origins
}
```

### Production Configuration (v2.0)
```kotlin
install(CORS) {
  allowHost("frontend-domain.com", schemes = listOf("https"))
  allowMethod(HttpMethod.Get)
  allowMethod(HttpMethod.Post)
  allowHeader(HttpHeaders.ContentType)
  allowCredentials = true
}
```

---

## 📄 Swagger / OpenAPI

### Access Swagger UI
```
http://localhost:8080/swagger
```

### OpenAPI Specification
Located at: `src/main/resources/swagger/documentation.yaml`

```yaml
openapi: 3.0.3
info:
  title: Traffic Monitoring API
  version: "1.0.0"

paths:
  /health:
    get:
      summary: Health check
      responses:
        "200":
          description: OK

  /api/upload-video:
    post:
      summary: Analyze traffic video
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                video:
                  type: string
                  format: binary
      responses:
        "200":
          description: Analysis result
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
- **Swagger**: http://localhost:8080/swagger

---

## 🐳 Running with Docker

### Build Image
```bash
cd ktor-backend
docker build -t traffic-ktor-backend .
```

### Run Container
```bash
docker run -p 8080:8080 \
  -e KTOR_AI_ENDPOINT=http://host.docker.internal:8000/api/process-video \
  traffic-ktor-backend
```

### Run with Docker Compose
```bash
# From project root
docker-compose up traffic-ktor-backend
```

---

## 🔧 Docker Configuration

### Multi-Stage Dockerfile

**Stage 1: Build**
```dockerfile
FROM gradle:8.14-jdk17 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle shadowJar --no-daemon
```

**Stage 2: Runtime**
```dockerfile
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### Benefits
- ✅ Smaller final image (~200MB)
- ✅ No build tools in runtime
- ✅ Faster deployments
- ✅ Better security

---

## 🧪 Testing

### Manual Testing with cURL

**Health Check**:
```bash
curl http://localhost:8080/health
```

**Upload Video**:
```bash
curl -X POST http://localhost:8080/api/upload-video \
  -F "video=@test_video.mp4"
```

### Testing with Postman
1. Import OpenAPI spec from `/swagger`
2. Send `POST /api/upload-video` with video file
3. Verify JSON response

---

## 📊 Performance Metrics

### Request Processing
- Video receive: < 1 second
- AI forward: ~0.5 seconds
- AI processing: 45-180 seconds (depends on video)
- Response return: < 1 second

### Memory Usage
- Idle: ~150 MB
- Processing video: +100-200 MB
- Peak: ~400 MB

---

## 🐛 Troubleshooting

### Issue: "AI_UNAVAILABLE"
**Solution**:
```bash
# Check AI-Service is running
curl http://localhost:8000/health

# Check KTOR_AI_ENDPOINT is correct
echo $KTOR_AI_ENDPOINT
```

### Issue: Port 8080 already in use
**Solution**:
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in application.conf
```

### Issue: Docker container won't start
**Solution**:
```bash
# Check logs
docker logs traffic-ktor-backend

# Rebuild without cache
docker build --no-cache -t traffic-ktor-backend .
```

### Issue: "Invalid file in request"
**Solution**:
- Verify file field name is "video"
- Check Content-Type is multipart/form-data
- Ensure file has valid extension (.mp4, .avi, etc.)

---

## 🚀 v1.5 Improvements (Coming Next)

### Enhanced Logging
- [ ] Structured logging (JSON format)
- [ ] Request/response correlation IDs
- [ ] Performance metrics logging
- [ ] Error aggregation

### Better Error Handling
- [ ] More detailed error messages
- [ ] Client-friendly error responses
- [ ] Retry logic for transient failures
- [ ] Circuit breaker pattern

### Code Quality
- [ ] Unit tests
- [ ] Integration tests
- [ ] Code documentation
- [ ] API versioning

### Performance
- [ ] Response caching
- [ ] Async processing queue
- [ ] Video compression before forwarding
- [ ] Progress updates (WebSocket)

---

## 📈 Roadmap

### v1.5 — Stabilization
- Enhanced logging & monitoring
- Better error messages
- Code cleanup & documentation
- Performance optimizations

### v2.0 — Functional Complete
- PostgreSQL integration
- JWT authentication
- User management
- Violation history API
- Statistics endpoints

### v3.0 — Production Ready
- WebSocket support
- Real-time updates
- Advanced monitoring
- Load balancing
- Rate limiting

---

## 📖 Dependencies

### Core Dependencies
```kotlin
// Server
implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

// Serialization
implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

// Middlewares
implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
implementation("io.ktor:ktor-server-status-pages:$ktor_version")

// Client
implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")

// Logging
implementation("ch.qos.logback:logback-classic:$logback_version")

// Swagger
implementation("io.ktor:ktor-server-swagger:$ktor_version")
```

---

## 🔗 Integration Points

### With AI-Service
- **Endpoint**: `POST http://ai-service:8000/api/process-video`
- **Format**: `multipart/form-data`
- **Timeout**: 600 seconds (10 minutes)

### With Frontend
- **Base URL**: `http://backend:8080`
- **CORS**: Enabled for all origins (MVP)
- **Content-Type**: `application/json`

---

## 📝 Development Commands

### Build
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Generate Fat JAR
```bash
./gradlew shadowJar
```

### Clean Build
```bash
./gradlew clean build
```

---

## 🙏 Acknowledgments

- **Ktor** - Modern Kotlin web framework
- **kotlinx.serialization** - JSON handling
- **Apache HTTP Client** - HTTP communication
- **Logback** - Logging framework

---

**Ready for v1.5 improvements?** See the roadmap above! 🚀