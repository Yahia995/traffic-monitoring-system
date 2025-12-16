# 🚦 Ktor Backend — Traffic Monitoring System (MVP)

This module represents the **Backend service** of the Traffic Monitoring System.  
It acts as an **orchestrator** between the **AI-Service (FastAPI)** and the **Frontend Dashboard**, handling video uploads, AI communication, error management, and API exposure.

The backend is built with **Ktor (Kotlin)** and follows a clean, modular architecture suitable for MVP and future scalability.

---

## 🎯 Responsibilities

- Expose REST APIs for frontend consumption
- Receive traffic videos from clients
- Forward videos to the AI-Service
- Return AI analysis results
- Centralize logging and error handling
- Provide Swagger/OpenAPI documentation
- Prepare the ground for database & auth integration

---

## 🧱 Tech Stack

- **Kotlin 1.9.23**
- **Ktor 2.3.13**
- **Apache HTTP Client**
- **kotlinx.serialization**
- **Logback**
- **Swagger UI (OpenAPI 3)**

---

## 🏗️ Project Structure

```text
ktor-backend/
│
├── client/
│   └── AiClient.kt            # HTTP client to AI-Service
│
├── models/
│   ├── AiResponse.kt          # AI response models
│   └── HealthResponse.kt
│
├── plugins/
│   ├── CallLogging.kt
│   ├── Cors.kt
│   ├── Routing.kt
│   ├── Serialization.kt
│   ├── StatusPages.kt
│   └── Swagger.kt
│
├── routes/
│   ├── AiRoutes.kt            # Video upload endpoint
│   └── HealthRoutes.kt
│
├── Application.kt             # Ktor entry point
│
├── resources/
│   ├── application.conf
│   ├── logback.xml
│   └── swagger/documentation.yaml
│
└── README.md
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

---

## 🪵 Logging Configuration

- Console logging via **Logback**
- **INFO** level for application & Ktor
- **WARN** for Netty & HTTP internals
- **ERROR** for Swagger noise
- Health checks excluded from logs

```xml
<root level="INFO">
    <appender-ref ref="STDOUT"/>
</root>
```

---

## 🔄 Application Startup Flow

1. Ktor engine starts (EngineMain)
2. Configuration is loaded
3. AI client is initialized
4. Plugins are installed:
    - Serialization
    - CORS
    - StatusPages
    - CallLogging
    - Routing
    - Swagger
5. API is ready to serve requests

---

## 📡 API Endpoints

### ✅ Health Check

```bash
GET /health
```

**Response:**
```json
{
  "status": "OK"
}
```

---

### 🎥 Upload & Analyze Video

```bash
POST /api/upload-video
```

#### Request
- `multipart/form-data`
- Field: `video` (mp4, avi, mov, mkv)

#### Processing
1. Video is received and validated
2. Logged (name + size)
3. Forwarded to AI-Service
4. AI response is returned as-is

#### Response (Proxy from AI-Service)
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

## 🤖 AI-Service Integration

The backend communicates with the AI-Service via an internal HTTP client.

### AiClient Features
- Multipart video upload
- Dynamic content-type detection
- Long timeout support (up to 10 minutes)
- Robust error handling
- Structured logging

```
Frontend → Ktor → AI-Service → Ktor → Frontend
```

---

## 🧯 Error Handling

Centralized using **StatusPages**.

### Handled cases:
- Invalid requests
- AI unavailable
- Network errors
- AI timeouts
- Unexpected server errors

### Standard error format:
```json
{
  "code": "AI_UNAVAILABLE",
  "message": "FastAPI backend is not running"
}
```

---

## 📄 Swagger / OpenAPI

Swagger UI is enabled for API inspection.

```bash
GET /swagger
```

OpenAPI file:
```bash
resources/swagger/documentation.yaml
```

---

## 🔐 CORS Policy (MVP)

- All hosts allowed
- `GET` & `POST` methods enabled
- `Content-Type` header allowed

⚠️ **To be restricted in production**

---

## ⚡ Run Locally

```bash
./gradlew run
```

Application will be available at:
```
http://localhost:8080
```

Swagger UI:
```bash
http://localhost:8080/swagger
```

---

## 🚀 Future Enhancements

- [ ] **PostgreSQL integration**
- [ ] **JWT authentication**
- [ ] **WebSocket support** (live violations)
- [ ] **Role-based access** (admin / user)
- [ ] **Request tracing & monitoring**
- [ ] **Rate limiting & security hardening**

---

## 🛠️ Development

### Build
```bash
./gradlew build
```

### Run tests
```bash
./gradlew test
```

### Generate fat JAR
```bash
./gradlew buildFatJar
```

---

## 📝 Dependencies

### Core Dependencies
```kotlin
implementation("io.ktor:ktor-server-core-jvm")
implementation("io.ktor:ktor-server-netty-jvm")
implementation("io.ktor:ktor-server-content-negotiation-jvm")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
```

### Client Dependencies
```kotlin
implementation("io.ktor:ktor-client-core")
implementation("io.ktor:ktor-client-cio")
implementation("io.ktor:ktor-client-content-negotiation")
```

### Plugin Dependencies
```kotlin
implementation("io.ktor:ktor-server-cors-jvm")
implementation("io.ktor:ktor-server-call-logging-jvm")
implementation("io.ktor:ktor-server-status-pages-jvm")
```

---

## 🔗 Integration Points

### With AI-Service
- **Endpoint**: `POST http://ai-service:8000/api/process-video`
- **Format**: `multipart/form-data`
- **Timeout**: 600 seconds (10 minutes)

### With Frontend
- **Base URL**: `http://backend:8080`
- **CORS**: Enabled for all origins (MVP only)
- **Content-Type**: `application/json`

---

## 📊 Monitoring & Logging

### Log Levels
```
INFO  - Application events
WARN  - Network issues
ERROR - Critical failures
```

### Example Log Output
```
INFO  [Ktor] Application started
INFO  [AiClient] Uploading video: traffic_video.mp4 (15.2 MB)
INFO  [AiClient] AI processing completed in 45.3s
```

---

## 🧪 Testing

### Manual Testing with cURL

**Health Check:**
```bash
curl http://localhost:8080/health
```

**Upload Video:**
```bash
curl -X POST http://localhost:8080/api/upload-video \
  -F "video=@/path/to/video.mp4"
```