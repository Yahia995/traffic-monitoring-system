# 🚦 Dashboard Frontend — Traffic Monitoring System (MVP)

This module represents the **Frontend Dashboard** of the Traffic Monitoring System.  
It provides a **simple web interface** to upload traffic videos, trigger analysis via the backend, and visualize detected traffic violations.

The frontend is built with **React + Vite** and communicates exclusively with the **Ktor backend API**.

---

## 🎯 Responsibilities

- Provide a user interface for video upload
- Call the backend `/api/upload-video` endpoint
- Display detected traffic violations
- Show raw JSON response for debugging/demo purposes
- Serve as a demonstrable MVP dashboard

---

## 🧱 Tech Stack

- **React 19**
- **Vite**
- **JavaScript (ES Modules)**
- **Fetch API**
- **Plain CSS**

---

## 🏗️ Project Structure

```text
dashboard-frontend/
│
├── public/
│
├── src/
│   ├── api/
│   │   └── backend.js          # Backend API calls
│   │
│   ├── components/
│   │   ├── UploadForm.jsx      # Video upload form
│   │   └── ViolationsTable.jsx # Violations table
│   │
│   ├── styles/
│   │   └── app.css             # Global styles
│   │
│   ├── App.jsx                 # Main app component
│   └── main.jsx                # React entry point
│
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

---

## 🔄 Application Flow

```
User selects video
   ↓
UploadForm submits file
   ↓
POST /api/upload-video (Ktor Backend)
   ↓
Backend forwards video to AI-Service
   ↓
AI analyzes video and returns JSON
   ↓
Frontend displays violations
```

---

## 🧩 Component Overview

### 📤 UploadForm
- Handles file selection (`video/*`)
- Sends video using `multipart/form-data`
- Displays loading state and errors
- Emits AI result to parent component

### 📊 ViolationsTable
- Displays violations in a table format
- **Columns:**
  - Plate number
  - Detected speed
  - Speed limit
  - Timestamp
- Handles empty violation cases

### 🧠 App Component
- Holds global state (data)
- **Renders:**
  - Upload form
  - Violations table
  - Raw JSON response (expandable)

---

## 🌐 Backend Communication

All API calls are centralized in:
```
src/api/backend.js
```

### API Base URL
```js
const API_BASE = "http://localhost:8080";
```

### Upload Video Request
```bash
POST /api/upload-video
Content-Type: multipart/form-data
Field name: video
```

---

## 📤 Expected API Response (MVP)

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

## 🎨 Styling

- Minimal CSS for MVP clarity
- Located in `src/styles/app.css`
- Focus on readability and simplicity

---

## ⚡ Run Locally

### Install dependencies
```bash
npm install
```

### Start development server
```bash
npm run dev
```

Application will be available at:
```
http://localhost:5173
```

⚠️ **Make sure the Ktor backend is running on port 8080**

---

## 🏗️ Build for Production

### Build optimized bundle
```bash
npm run build
```

### Preview production build
```bash
npm run preview
```

The build output will be in the `dist/` directory.

---

## 🧪 Testing

### Manual Testing Checklist

- [ ] Upload a valid video file (.mp4, .avi, .mov)
- [ ] Verify loading state appears during processing
- [ ] Check violations table displays correctly
- [ ] Verify error messages show when backend is down
- [ ] Test with videos of different sizes
- [ ] Verify raw JSON response toggle works

### Example Test Videos
- Short video (30 seconds): Quick testing
- Medium video (1-2 minutes): Normal case
- Large video (5+ minutes): Performance testing

---

## 🚧 MVP Limitations

- ❌ No authentication
- ❌ No pagination or filtering
- ❌ No real-time updates
- ❌ No video preview or overlays
- ❌ No production styling

---

## 🚀 Planned Improvements

### v1.5 - Enhanced UI
- [ ] Professional UI (Tailwind / Material UI)
- [ ] Improved error messages
- [ ] Loading animations
- [ ] Responsive design improvements

### v2 - Advanced Features
- [ ] **Charts & statistics**
- [ ] **Authentication & roles**
- [ ] **Video playback with bounding boxes**
- [ ] **Real-time updates via WebSockets**
- [ ] **Filtering & history view**
- [ ] **Export to PDF/CSV**
- [ ] **Multi-language support**

---

## 📁 File Details

### `src/api/backend.js`
Centralized API communication logic:
```js
export async function uploadVideo(videoFile) {
  const formData = new FormData();
  formData.append('video', videoFile);
  
  const response = await fetch(`${API_BASE}/api/upload-video`, {
    method: 'POST',
    body: formData
  });
  
  return response.json();
}
```

### `src/components/UploadForm.jsx`
Handles file selection and upload:
```jsx
<input 
  type="file" 
  accept="video/*" 
  onChange={handleFileChange}
/>
<button onClick={handleUpload}>
  Upload Video
</button>
```

### `src/components/ViolationsTable.jsx`
Displays violations in tabular format:
```jsx
<table>
  <thead>
    <tr>
      <th>Plate</th>
      <th>Speed</th>
      <th>Limit</th>
      <th>Time</th>
    </tr>
  </thead>
  <tbody>
    {/* Violations data */}
  </tbody>
</table>
```

---

## 🐛 Troubleshooting

### Backend Connection Issues

**Problem**: "Failed to fetch" error

**Solutions**:
1. Verify backend is running: `curl http://localhost:8080/health`
2. Check CORS settings in backend
3. Verify API_BASE URL in `backend.js`

### Video Upload Fails

**Problem**: Upload returns error

**Solutions**:
1. Check video format (mp4, avi, mov supported)
2. Verify file size (backend may have limits)
3. Check browser console for detailed errors

### Slow Performance

**Problem**: Upload takes too long

**Solutions**:
1. Use shorter videos for testing
2. Reduce video resolution
3. Check AI service is running properly

---

## 📊 Browser Support

- ✅ Chrome 90+
- ✅ Firefox 88+
- ✅ Safari 14+
- ✅ Edge 90+

---

## 🔗 Integration Points

### With Backend
- **Endpoint**: `POST http://backend:8080/api/upload-video`
- **Format**: `multipart/form-data`
- **Field**: `video`

### Expected Response
```json
{
  "violations_nbr": number,
  "violations": { ... },
  "details": { ... }
}
```

---

## 📝 Development Notes

### State Management
Currently using React's `useState` for simplicity. Consider upgrading to:
- Redux (v2)
- Zustand (v2)
- Context API (v1.5)

### Code Style
- Use functional components
- Follow React hooks best practices
- Keep components small and focused
- Use meaningful variable names