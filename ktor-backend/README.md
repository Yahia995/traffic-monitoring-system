# 🚦 Ktor Backend — Traffic Monitoring System

**Current Version**: v2.0 (Database & Authentication) ✅  
**Previous Version**: v1.5 (Stabilization)

This module represents the **Backend service** of the Traffic Monitoring System. It orchestrates between the AI-Service (FastAPI) and Frontend Dashboard, handling video uploads, AI communication, database persistence, user authentication, and API exposure.

---

## 🆕 What's New in v2.0

### Database Persistence
- ✅ **PostgreSQL Integration**: All analysis results stored in database
- ✅ **Video History**: Track all uploaded videos
- ✅ **Violation Storage**: Searchable violation records
- ✅ **Vehicle Tracking**: Complete vehicle and trajectory data persistence
- ✅ **Data Relationships**: Proper foreign keys and relational integrity

### Authentication & Authorization
- ✅ **JWT Authentication**: Secure token-based auth
- ✅ **User Registration**: Self-service account creation
- ✅ **User Login**: Credential-based authentication
- ✅ **Protected Routes**: Auth required for video uploads and V2 endpoints
- ✅ **Password Hashing**: BCrypt for secure password storage

### API Enhancements
- ✅ **V2 Endpoints**: New RESTful endpoints for videos, violations, stats
- ✅ **Pagination Support**: Efficient data browsing
- ✅ **Advanced Filtering**: Search violations by multiple criteria
- ✅ **Statistics API**: Aggregate violation analytics
- ✅ **CSV Export**: Download violation data

### Backward Compatibility
- ✅ **V1.5 Endpoints Preserved**: All v1.5 endpoints still work
- ✅ **Feature Flags**: Enable/disable V2 features via environment variables
- ✅ **Optional Authentication**: Can run with or without auth
- ✅ **Graceful Degradation**: Works without database if V2 disabled

---

## 🎯 Responsibilities

### Core (V1.5)
- ✅ Expose REST APIs for frontend consumption
- ✅ Receive traffic videos from clients
- ✅ Forward videos to AI-Service with correlation tracking
- ✅ Validate and enrich AI analysis results
- ✅ Centralize logging and error handling
- ✅ Provide comprehensive health monitoring
- ✅ Serve Swagger/OpenAPI documentation
- ✅ CORS management for frontend access

### New in V2.0
- ✅ **User Management**: Registration, login, JWT tokens
- ✅ **Video Persistence**: Store all video analysis in PostgreSQL
- ✅ **Historical Data**: Query past uploads and results
- ✅ **Violation Tracking**: Advanced filtering and search
- ✅ **Statistics**: Aggregate analytics and reporting
- ✅ **Data Export**: CSV export for violations

---

## 🧱 Tech Stack

- **Kotlin 1.9.23**
- **Ktor 2.3.13**
- **PostgreSQL 15** (NEW in v2.0)
- **Exposed ORM** (NEW in v2.0)
- **HikariCP** (connection pooling)
- **JWT (Auth0)** (NEW in v2.0)
- **BCrypt** (password hashing)
- **Apache HTTP Client**
- **kotlinx.serialization**
- **Logback** (structured logging)
- **Swagger UI (OpenAPI 3)**
- **Gradle 8.14**
- **Docker** (multi-stage build)

---

## 📡 API Endpoints

### Health Endpoints

#### Basic Health Check
**URL**: `GET /health`

**Response**:
```json
{
  "status": "OK",
  "version": "2.0.0",
  "timestamp": 1640000000000
}
```

---

#### Detailed Health Check
**URL**: `GET /health/detailed`

**Description**: Check backend, AI service, and database health

**Response**:
```json
{
  "status": "OK",
  "version": "2.0.0",
  "timestamp": 1640000000000,
  "services": {
    "backend": "OK",
    "ai_service": "OK",
    "database": "OK"
  }
}
```

**Status Codes**:
- `200 OK`: All services healthy
- `503 Service Unavailable`: One or more services unavailable

---

### V1.5 Endpoints (Video Upload)

#### Upload & Analyze Video (Full Response)
**URL**: `POST /api/upload-video`

**Authentication**: **Required** when `ENABLE_AUTHENTICATION=true` (V2.0)

**Headers**:
```
Authorization: Bearer <jwt_token>  # Required in V2.0 if auth enabled
Content-Type: multipart/form-data
```

**Request**:
- **Field**: `video` (file)
- **Supported formats**: `.mp4`, `.avi`, `.mov`, `.mkv`
- **Max size**: 200 MB

**Response**: Full v1.5 AI response format (see AI-Service README)

**Status Codes**:
- `200 OK`: Video processed successfully
- `401 Unauthorized`: Missing or invalid token (V2.0)
- `400 Bad Request`: Invalid file format or too large
- `503 Service Unavailable`: AI-Service not reachable
- `504 Gateway Timeout`: Processing timeout (>10 min)

---

#### Upload & Analyze Video (Summary Only)
**URL**: `POST /api/upload-video/summary`

**Authentication**: **Required** when `ENABLE_AUTHENTICATION=true` (V2.0)

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

### V2.0 Endpoints

#### User Registration
**URL**: `POST /api/v2/auth/register`

**Authentication**: Not required

**Request**:
```json
{
  "email": "user@example.com",
  "password": "securepassword",
  "role": "USER"  // Optional, defaults to USER
}
```

**Response**:
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

---

#### User Login
**URL**: `POST /api/v2/auth/login`

**Authentication**: Not required

**Request**:
```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Response**: Same as registration

---

#### List Videos
**URL**: `GET /api/v2/videos?page=0&size=20`

**Authentication**: Required (JWT)

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Items per page (default: 20)

**Response**:
```json
{
  "data": [
    {
      "id": "uuid",
      "filename": "traffic.mp4",
      "uploadedAt": "2024-01-01T12:00:00",
      "durationSeconds": 30.5,
      "fps": 30.0,
      "totalFrames": 915,
      "processedFrames": 458,
      "processingTimeSeconds": 45.3,
      "status": "COMPLETED",
      "aiStatus": "SUCCESS",
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

---

#### Get Video Details
**URL**: `GET /api/v2/videos/{id}`

**Authentication**: Required (JWT)

**Response**: Single video object (same as list item)

---

#### Delete Video
**URL**: `DELETE /api/v2/videos/{id}`

**Authentication**: Required (JWT)

**Response**: `204 No Content` on success

---

#### Filter Violations
**URL**: `POST /api/v2/violations/filter`

**Authentication**: Required (JWT)

**Request**:
```json
{
  "severity": "HIGH",          // Optional: LOW, MEDIUM, HIGH, CRITICAL
  "plateNumber": "123",        // Optional: partial match
  "startDate": "2024-01-01T00:00:00",  // Optional: ISO format
  "endDate": "2024-01-31T23:59:59",    // Optional: ISO format
  "minSpeed": 60.0,            // Optional
  "maxSpeed": 100.0,           // Optional
  "validated": true,           // Optional: plate validation status
  "page": 0,
  "size": 20
}
```

**Response**: Paginated violation list

---

#### Get Violation Details
**URL**: `GET /api/v2/violations/{id}`

**Authentication**: Required (JWT)

**Response**: Single violation object

---

#### Get Statistics
**URL**: `GET /api/v2/stats`

**Authentication**: Required (JWT)

**Response**:
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

#### Export Violations CSV
**URL**: `GET /api/v2/violations/export/csv`

**Authentication**: Required (JWT)

**Response**: CSV file download

---

## 🔧 Configuration

### Environment Variables

#### Feature Flags
```bash
# Enable V2 database persistence
ENABLE_V2_PERSISTENCE=true

# Enable authentication for video uploads and V2 endpoints
ENABLE_AUTHENTICATION=true
```

#### Database Configuration
```bash
DB_HOST=traffic-postgres         # Database hostname
DB_PORT=5432                      # Database port
DB_NAME=traffic_monitoring        # Database name
DB_USER=postgres                  # Database user
DB_PASSWORD=postgres              # Database password
DB_POOL_SIZE=10                   # Connection pool size
```

#### JWT Configuration
```bash
JWT_SECRET=your-secret-key-change-in-production
JWT_ISSUER=traffic-monitoring-system
JWT_AUDIENCE=traffic-monitoring-api
JWT_REALM=Access to Traffic Monitoring API
JWT_VALIDITY_MS=3600000          # 1 hour
```

#### AI Service Configuration
```bash
KTOR_AI_ENDPOINT=http://traffic-ai-service:8000/api/process-video
KTOR_AI_TIMEOUT_MS=600000        # 10 minutes
```

#### Logging
```bash
LOG_LEVEL=INFO                    # DEBUG, INFO, WARN, ERROR
```

---

## 🏗️ Project Structure

```text
ktor-backend/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/traffic/
│       │       ├── auth/
│       │       │   └── AuthService.kt        # NEW: Authentication logic
│       │       ├── client/
│       │       │   └── AIClient.kt           # Enhanced AI communication
│       │       ├── database/
│       │       │   ├── DatabaseFactory.kt    # NEW: DB connection & health
│       │       │   └── Tables.kt             # NEW: Database schema
│       │       ├── dto/
│       │       │   ├── ai/                   # AI request/response models
│       │       │   ├── request/              # NEW: API request models
│       │       │   └── response/             # NEW: API response models
│       │       ├── models/
│       │       │   └── Enums.kt              # NEW: UserRole, VideoStatus, etc.
│       │       ├── plugins/
│       │       │   ├── CallLogging.kt
│       │       │   ├── CORS.kt
│       │       │   ├── JWT.kt                # NEW: JWT authentication
│       │       │   ├── Routing.kt            # Enhanced routing
│       │       │   ├── Serialization.kt
│       │       │   ├── StatusPages.kt        # Enhanced error handling
│       │       │   └── Swagger.kt
│       │       ├── routes/
│       │       │   ├── AIRoutes.kt           # Enhanced: Protected with auth
│       │       │   ├── HealthRoutes.kt       # Enhanced: DB health check
│       │       │   └── V2Routes.kt           # NEW: V2 API endpoints
│       │       ├── service/
│       │       │   ├── VideoService.kt       # NEW: Video persistence
│       │       │   └── ViolationService.kt   # NEW: Violation queries
│       │       └── Application.kt            # Enhanced: DB & auth setup
│       └── resources/
│           ├── application.conf
│           ├── logback.xml
│           └── swagger/
│               └── documentation.yaml        # Updated for V2
├── build.gradle.kts                          # Updated dependencies
├── Dockerfile
├── gradle.properties
└── README.md                                 # This file
```

---

## ⚡ Running Locally

### Prerequisites
- JDK 17+
- Gradle 8+
- PostgreSQL 15+ (for V2 features)

### 1️⃣ Setup Database (V2)
```bash
# Create database
createdb traffic_monitoring

# Or use Docker
docker run -d \
  --name traffic-postgres \
  -e POSTGRES_DB=traffic_monitoring \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2️⃣ Configure Environment
```bash
# Copy example env file
cp .env.example .env

# Edit configuration
nano .env

# Required for V2:
ENABLE_V2_PERSISTENCE=true
ENABLE_AUTHENTICATION=true
DB_HOST=localhost
DB_PASSWORD=your_password
JWT_SECRET=your-secret-key
```

### 3️⃣ Start the Server
```bash
cd ktor-backend
./gradlew run
```

### 4️⃣ Access the API
- **Base URL**: http://localhost:8080
- **Health**: http://localhost:8080/health/detailed
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
  -e ENABLE_V2_PERSISTENCE=true \
  -e ENABLE_AUTHENTICATION=true \
  -e DB_HOST=host.docker.internal \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=your-secret \
  -e KTOR_AI_ENDPOINT=http://host.docker.internal:8000/api/process-video \
  traffic-ktor-backend
```

### Run with Docker Compose
```bash
# From project root
docker-compose up traffic-ktor-backend
```

---

## 🧪 Testing

### Health Checks
```bash
# Basic health
curl http://localhost:8080/health

# Detailed health (includes AI service and database)
curl http://localhost:8080/health/detailed
```

### User Registration
```bash
curl -X POST http://localhost:8080/api/v2/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "securepass123"
  }'
```

### User Login
```bash
curl -X POST http://localhost:8080/api/v2/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "securepass123"
  }'
```

### Upload Video (Authenticated)
```bash
# Get token from login response
TOKEN="your-jwt-token"

curl -X POST http://localhost:8080/api/upload-video \
  -H "Authorization: Bearer $TOKEN" \
  -F "video=@test_video.mp4"
```

### List Videos
```bash
curl http://localhost:8080/api/v2/videos?page=0&size=10 \
  -H "Authorization: Bearer $TOKEN"
```

### Get Statistics
```bash
curl http://localhost:8080/api/v2/stats \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔒 Authentication Flow

### 1. Register User
```javascript
POST /api/v2/auth/register
{
  "email": "user@example.com",
  "password": "securepassword"
}

Response: { "token": "...", "user": {...} }
```

### 2. Use Token in Requests
```javascript
// Store token
localStorage.setItem('token', response.token);

// Include in subsequent requests
headers: {
  'Authorization': `Bearer ${token}`
}
```

### 3. Token Expiration
- Tokens expire after 1 hour (configurable)
- Frontend should handle 401 responses and redirect to login
- Refresh tokens coming in future version

---

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) DEFAULT 'USER',
  created_at TIMESTAMP DEFAULT NOW()
);
```

### Videos Table
```sql
CREATE TABLE videos (
  id UUID PRIMARY KEY,
  uploaded_by UUID REFERENCES users(id),
  filename VARCHAR(255),
  uploaded_at TIMESTAMP DEFAULT NOW(),
  duration_seconds DOUBLE PRECISION,
  fps DOUBLE PRECISION,
  total_frames INTEGER,
  processed_frames INTEGER,
  processing_time_seconds DOUBLE PRECISION,
  status VARCHAR(50),
  ai_status VARCHAR(20),
  speed_limit_kmh DOUBLE PRECISION,
  pixel_to_meter DOUBLE PRECISION
);
```

### Violations Table
```sql
CREATE TABLE violations (
  id UUID PRIMARY KEY,
  video_id UUID REFERENCES videos(id) ON DELETE CASCADE,
  vehicle_id UUID REFERENCES vehicles(id),
  plate_number VARCHAR(50),
  plate_confidence DOUBLE PRECISION,
  plate_validated BOOLEAN,
  speed_kmh DOUBLE PRECISION,
  speed_limit_kmh DOUBLE PRECISION,
  overspeed_kmh DOUBLE PRECISION,
  timestamp_seconds DOUBLE PRECISION,
  frame_number INTEGER,
  severity VARCHAR(20),
  detected_at TIMESTAMP DEFAULT NOW()
);
```

(See `Tables.kt` for complete schema)

---

## 🐛 Troubleshooting

### Database Connection Fails
```bash
# Check PostgreSQL is running
psql -U postgres -d traffic_monitoring

# Check connection settings
echo $DB_HOST
echo $DB_PORT

# Test connection
curl http://localhost:8080/health/detailed
```

### Authentication Not Working
```bash
# Check JWT secret is set
echo $JWT_SECRET

# Verify token in request headers
curl -v http://localhost:8080/api/v2/videos \
  -H "Authorization: Bearer YOUR_TOKEN"

# Check logs for JWT errors
docker logs traffic-ktor-backend
```

### Video Upload Returns 401
- Ensure authentication is enabled: `ENABLE_AUTHENTICATION=true`
- Verify you're including the JWT token in the Authorization header
- Check token hasn't expired (default: 1 hour)
- Try logging in again to get a fresh token

---

## 📈 Performance

### Resource Usage
- **Idle**: ~150-200 MB RAM
- **Processing**: +100-200 MB during video upload
- **Database**: ~50-100 MB RAM (depends on data volume)
- **Peak**: ~500 MB total

### Optimization Tips
- Increase DB connection pool for high concurrency
- Enable database indexing for faster queries
- Use pagination for large result sets
- Consider Redis for session caching (future enhancement)

---

## 🚀 Migration Guide (v1.5 → v2.0)

### Breaking Changes
- **Video uploads now require authentication** when `ENABLE_AUTHENTICATION=true`
- Frontend must pass JWT token in Authorization header
- Database is required for V2 features

### Migration Steps

1. **Setup Database**:
```bash
docker-compose up traffic-postgres
```

2. **Enable V2 Features**:
```bash
export ENABLE_V2_PERSISTENCE=true
export ENABLE_AUTHENTICATION=true
```

3. **Update Frontend**:
- Add login/register UI
- Store JWT token
- Pass token in upload requests

4. **Test**:
```bash
# Register user
curl -X POST http://localhost:8080/api/v2/auth/register \
  -d '{"email":"test@test.com","password":"pass123"}'

# Upload with token
curl -X POST http://localhost:8080/api/upload-video \
  -H "Authorization: Bearer TOKEN" \
  -F "video=@test.mp4"
```

### Backward Compatibility
To run in v1.5 mode (no auth, no database):
```bash
export ENABLE_V2_PERSISTENCE=false
export ENABLE_AUTHENTICATION=false
```

---

## 🔮 Roadmap

### v2.1 (Next)
- [ ] Refresh token support
- [ ] User profile management
- [ ] Role-based permissions
- [ ] Audit logging

### v3.0 (Future)
- [ ] Real-time notifications (WebSocket)
- [ ] Multi-tenancy support
- [ ] Advanced analytics dashboard
- [ ] Video streaming integration
- [ ] Kubernetes deployment

---

**Backend Status**: v2.0 Complete ✅  
**Features**: Database Persistence + JWT Authentication 🔒