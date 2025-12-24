# 🚦 Dashboard Frontend — Traffic Monitoring System

**Current Version**: v1.0 (MVP) ✅  
**Next Version**: v1.5 (Stabilization & Enhancements) 🚧

This module represents the **Frontend Dashboard** of the Traffic Monitoring System. It provides a **simple web interface** to upload traffic videos, trigger analysis via the backend, and visualize detected traffic violations.

The frontend is built with **React + Vite** and communicates exclusively with the **Ktor backend API**.

---

## 🎯 Responsibilities

- ✅ Provide user interface for video upload
- ✅ Call backend `/api/upload-video` endpoint
- ✅ Display detected traffic violations in table format
- ✅ Show raw JSON response for debugging
- ✅ Handle loading states and errors
- ✅ Docker containerization
- 📅 Authentication (v2.0)
- 📅 Advanced filtering (v2.0)
- 📅 Charts & statistics (v2.0)

---

## 🧱 Tech Stack

- **React 19.2.0**
- **Vite 7.2.4**
- **JavaScript (ES Modules)**
- **Fetch API**
- **Plain CSS**
- **Node.js 22 (Docker)**

---

## 🏗️ Project Structure

```text
dashboard-frontend/
│
├── public/                               # Static assets
│
├── src/
│   ├── api/
│   │   └── backend.js                    # Backend API calls
│   │
│   ├── components/
│   │   ├── UploadForm.jsx                # Video upload form
│   │   └── ViolationsTable.jsx           # Violations table
│   │
│   ├── styles/
│   │   └── app.css                       # Global styles
│   │
│   ├── App.jsx                           # Main app component
│   └── main.jsx                          # React entry point
│
├── index.html                            # HTML entry
├── package.json                          # Dependencies
├── vite.config.js                        # Vite configuration
├── Dockerfile                            # Docker build
├── .dockerignore                         # Docker ignore rules
└── README.md                             # This file
```

---

## 🔄 Application Flow

```text
1. User selects video file
   ↓
2. UploadForm submits to backend
   ↓
3. POST /api/upload-video (Ktor)
   ↓
4. Backend forwards to AI-Service
   ↓
5. AI analyzes video
   ↓
6. Backend returns JSON
   ↓
7. Frontend displays violations table
   ↓
8. Raw JSON available for debugging
```

---

## 🧩 Component Overview

### 📤 UploadForm (`src/components/UploadForm.jsx`)

**Purpose**: Handle video file selection and upload

**Features**:
- File input with `video/*` accept filter
- Loading state during processing
- Error display
- Disabled button during upload

**Props**:
- `onResult(data)`: Callback with AI results

**Usage**:
```jsx
<UploadForm onResult={setData} />
```

---

### 📊 ViolationsTable (`src/components/ViolationsTable.jsx`)

**Purpose**: Display violations in tabular format

**Columns**:
- **Plate**: License plate number
- **Speed**: Detected speed (km/h)
- **Limit**: Configured speed limit
- **Timestamp**: Detection time (seconds)

**Props**:
- `violations`: Object of violations by plate

**Features**:
- Empty state handling
- Number formatting (2 decimals)
- Responsive table layout

**Usage**:
```jsx
<ViolationsTable violations={data.violations} />
```

---

### 🧠 App Component (`src/App.jsx`)

**Purpose**: Main application container

**State**:
- `data`: Stores AI analysis results

**Renders**:
1. Page title
2. Upload form
3. Violations count (if data exists)
4. Violations table
5. Expandable raw JSON view

---

## 🌐 Backend Communication

### API Configuration (`src/api/backend.js`)

```javascript
const API_BASE = import.meta.env.VITE_API_BASE;

export async function uploadVideo(file) {
  const formData = new FormData();
  formData.append("video", file);

  const res = await fetch(`${API_BASE}/api/upload-video`, {
    method: "POST",
    body: formData
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return res.json();
}
```

### Environment Variables

**Development** (`.env`):
```env
VITE_API_BASE=http://localhost:8080
```

**Docker**:
```env
VITE_API_BASE=http://traffic-ktor-backend:8080
```

---

## 📤 API Request/Response

### Request
```http
POST /api/upload-video HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="video"; filename="traffic.mp4"
Content-Type: video/mp4

[binary data]
------WebKitFormBoundary--
```

### Response
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
    }
  }
}
```

---

## 🎨 Styling (`src/styles/app.css`)

### Current Styles
```css
.container {
  padding: 20px;
  font-family: Arial, sans-serif;
}

table {
  border-collapse: collapse;
  margin-top: 10px;
}

th, td {
  border: 1px solid #ccc;
  padding: 8px;
}

.error {
  color: red;
}
```

### v1.5 Improvements
- Modern CSS framework (Tailwind)
- Better color scheme
- Responsive design
- Loading animations
- Improved error styling

---

## ⚡ Running Locally

### Prerequisites
- Node.js 18+
- npm or yarn

### 1️⃣ Install Dependencies
```bash
cd dashboard-frontend
npm install
```

### 2️⃣ Configure Environment
Create `.env` file:
```env
VITE_API_BASE=http://localhost:8080
```

### 3️⃣ Start Development Server
```bash
npm run dev
```

### 4️⃣ Access Dashboard
Open browser: **http://localhost:5173**

⚠️ **Ensure Ktor backend is running on port 8080**

---

## 🏗️ Build for Production

### 1️⃣ Build Optimized Bundle
```bash
npm run build
```

Output directory: `dist/`

### 2️⃣ Preview Production Build
```bash
npm run preview
```

### 3️⃣ Deploy
Upload `dist/` contents to your web server.

---

## 🐳 Running with Docker

### Build Image
```bash
cd dashboard-frontend
docker build -t traffic-dashboard-frontend .
```

### Run Container
```bash
docker run -p 5173:5173 \
  -e VITE_API_BASE=http://host.docker.internal:8080 \
  traffic-dashboard-frontend
```

### Run with Docker Compose
```bash
# From project root
docker-compose up traffic-frontend
```

---

## 🔧 Docker Configuration

### Dockerfile
```dockerfile
FROM node:22-alpine

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source
COPY . .

EXPOSE 5173

# Run dev server with host exposed
CMD ["npm", "run", "dev", "--", "--host"]
```

### .dockerignore
```
node_modules/
dist/
.env
```

### Why Alpine?
- ✅ Smaller image size (~150MB vs ~1GB)
- ✅ Faster builds
- ✅ Better security
- ✅ Production-ready

---

## 🧪 Testing

### Manual Testing Checklist

**Upload Functionality**:
- [ ] File input accepts video files
- [ ] Button disabled during upload
- [ ] Loading indicator shows
- [ ] Error messages display correctly

**Violations Display**:
- [ ] Table renders with correct columns
- [ ] Speed values formatted (2 decimals)
- [ ] Empty state shows "No violations detected"
- [ ] Raw JSON expandable works

**Error Handling**:
- [ ] Network errors handled gracefully
- [ ] Invalid file format rejected
- [ ] Backend down error displayed

### Test Videos
- **Short** (30s): Quick testing
- **Medium** (1-2min): Normal case
- **Large** (5+min): Performance testing

---

## 📊 User Interface

### Current UI (MVP)
```
┌─────────────────────────────────────┐
│ 🚦 Traffic Violations Dashboard    │
│                                     │
│ [Choose File] [Upload]              │
│                                     │
│ Violations (2)                      │
│ ┌─────────┬───────┬───────┬────┐  │
│ │ Plate   │ Speed │ Limit │Time│  │
│ ├─────────┼───────┼───────┼────┤  │
│ │123TUN456│ 72.40 │  50   │3.20│  │
│ │789TUN012│ 65.80 │  50   │8.50│  │
│ └─────────┴───────┴───────┴────┘  │
│                                     │
│ ▼ Raw JSON                          │
│   { ... }                           │
└─────────────────────────────────────┘
```

### v1.5 Planned UI
- Modern design with Tailwind CSS
- Card-based layout
- Color-coded severity
- Loading skeletons
- Toast notifications
- Responsive mobile view

---

## 🐛 Troubleshooting

### Issue: "Failed to fetch"
**Cause**: Backend not reachable

**Solution**:
```bash
# Check backend health
curl http://localhost:8080/health

# Verify VITE_API_BASE
echo $VITE_API_BASE

# Check CORS settings in backend
```

### Issue: Video upload fails
**Cause**: Invalid format or backend error

**Solution**:
- Check video format (.mp4, .avi, .mov supported)
- Verify file size (backend may have limits)
- Check browser console for errors
- Check backend logs

### Issue: Slow upload
**Cause**: Large video file

**Solution**:
- Use shorter test videos
- Reduce video resolution
- Check network speed
- Verify AI-Service is running

### Issue: Docker container won't start
**Solution**:
```bash
# Check logs
docker logs traffic-frontend

# Rebuild
docker build --no-cache -t traffic-frontend .

# Check port availability
lsof -i :5173
```

---

## 🚀 v1.5 Improvements (Coming Next)

### UI/UX Enhancements
- [ ] Modern CSS framework (Tailwind)
- [ ] Professional color scheme
- [ ] Loading animations
- [ ] Better error messages
- [ ] Responsive design
- [ ] Dark mode toggle

### Features
- [ ] Drag & drop file upload
- [ ] Video format validation
- [ ] Upload progress indicator
- [ ] Video preview thumbnail
- [ ] Filter violations by speed
- [ ] Export table to CSV

### Code Quality
- [ ] Component testing
- [ ] Error boundary
- [ ] Better state management
- [ ] Code documentation
- [ ] Accessibility improvements

---

## 📈 Roadmap

### v1.5 — Stabilization
- Modern UI with Tailwind
- Better error handling
- Improved UX
- Code cleanup

### v2.0 — Functional Complete
- User authentication
- Violation history
- Charts & statistics
- Advanced filtering
- PDF export
- Video playback with overlays

### v3.0 — Production Ready
- WebSocket real-time updates
- Multi-language support
- Admin dashboard
- Role-based access
- Advanced analytics
- Mobile app (Flutter)

---

## 📖 Dependencies

### Core Dependencies
```json
{
  "react": "^19.2.0",
  "react-dom": "^19.2.0"
}
```

### Dev Dependencies
```json
{
  "@vitejs/plugin-react": "^5.1.1",
  "vite": "^7.2.4",
  "eslint": "^9.39.1"
}
```

### v2.0 Planned Dependencies
- `tailwindcss`: Styling
- `recharts`: Charts
- `react-router-dom`: Routing
- `zustand` or `redux`: State management

---

## 🔗 Integration Points

### With Backend
- **Base URL**: Configured via `VITE_API_BASE`
- **Endpoint**: `POST /api/upload-video`
- **Format**: `multipart/form-data`
- **Field**: `video`

### CORS Requirements
Backend must allow:
- Origin: Frontend URL
- Methods: `GET`, `POST`
- Headers: `Content-Type`

---

## 📱 Browser Support

| Browser | Version | Status |
|---------|---------|--------|
| Chrome | 90+ | ✅ Supported |
| Firefox | 88+ | ✅ Supported |
| Safari | 14+ | ✅ Supported |
| Edge | 90+ | ✅ Supported |
| IE | Any | ❌ Not supported |

---

## 📝 Development Commands

### Install dependencies
```bash
npm install
```

### Start dev server
```bash
npm run dev
```

### Build production
```bash
npm run build
```

### Preview build
```bash
npm run preview
```

### Lint code
```bash
npm run lint
```

---

## 🎯 v1.5 Feature Breakdown

### Priority 1 (Critical)
1. **Tailwind CSS Integration**
   - Install and configure Tailwind
   - Convert existing styles
   - Add responsive breakpoints

2. **Loading States**
   - Upload progress bar
   - Processing indicator
   - Skeleton loaders

3. **Error Handling**
   - Toast notifications
   - Better error messages
   - Retry functionality

### Priority 2 (Important)
4. **File Validation**
   - Check file size
   - Validate format
   - Show preview thumbnail

5. **Table Enhancements**
   - Sortable columns
   - Severity color coding
   - Export to CSV

### Priority 3 (Nice to Have)
6. **UX Improvements**
   - Drag & drop upload
   - Keyboard shortcuts
   - Dark mode

---

## 🙏 Acknowledgments

- **React** - UI library
- **Vite** - Build tool
- **Fetch API** - HTTP client

---

## 📞 Support

For issues or questions:
- Check [Main README](../README.md)
- Review [Backend README](../ktor-backend/README.md)
- Review [AI-Service README](../ai-service/README.md)

---

**Ready for v1.5 improvements?** Let's build a beautiful interface! 🎨