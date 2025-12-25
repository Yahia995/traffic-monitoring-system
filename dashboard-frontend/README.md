# 🚦 Dashboard Frontend — Traffic Monitoring System

**Current Version**: v1.5 (Stabilization) ✅  
**Next Version**: v2.0 (Database & Authentication) 🚧

This module represents the **Frontend Dashboard** of the Traffic Monitoring System. It provides a **modern, interactive web interface** to upload traffic videos, trigger analysis via the backend, and visualize detected traffic violations with comprehensive statistics and detailed vehicle information.

---

## 🆕 What's New in v1.5

### Complete UI Redesign
- ✅ **Modern Gradient Design**: Professional color schemes and visual effects
- ✅ **Tab-Based Interface**: Organized views (Violations / All Vehicles / Raw Data)
- ✅ **Summary Dashboard**: Visual statistics with metrics and icons
- ✅ **Interactive Tables**: Sortable, filterable violations with severity indicators
- ✅ **Vehicle Detail Cards**: Expandable cards with tracking information
- ✅ **Responsive Layout**: Mobile and tablet friendly

### Enhanced Features
- ✅ **Drag & Drop Upload**: Modern file upload with preview
- ✅ **Real-time Progress**: Visual progress indicators during processing
- ✅ **CSV Export**: Export violations data to CSV
- ✅ **Confidence Bars**: Visual confidence indicators
- ✅ **Severity Badges**: Color-coded violation severity
- ✅ **Validation Status**: Plate validation indicators
- ✅ **Empty States**: Informative no-data displays

### Updated Data Handling
- ✅ Migrated to v1.5 API response format
- ✅ Handle array-based violations and vehicles
- ✅ Display confidence scores and validation status
- ✅ Show severity classification
- ✅ Present processing metadata

---

## 🎯 Responsibilities

- ✅ Provide modern, intuitive user interface
- ✅ Handle video file upload with validation
- ✅ Display summary statistics and metrics
- ✅ Present violations in sortable, filterable table
- ✅ Show detailed vehicle tracking information
- ✅ Export data to CSV format
- ✅ Handle loading states and errors gracefully
- ✅ Support responsive design for all devices
- 📅 User authentication UI (v2.0)
- 📅 Historical data browsing (v2.0)
- 📅 Advanced filtering and search (v2.0)

---

## 🧱 Tech Stack

- **React 19.2.0**
- **Vite 7.2.4**
- **JavaScript (ES Modules)**
- **Fetch API**
- **Custom CSS** (gradient-based design)
- **Node.js 22 (Docker)**

---

## 🎨 User Interface Overview

### Main Sections

#### 1. Upload Section
- **Welcome Message**: Feature highlights
- **Drag & Drop Zone**: File upload area
- **File Preview**: Selected file information
- **Progress Bar**: Real-time upload/processing progress
- **Validation**: Format and size checks

#### 2. Results Dashboard
- **Header**: Video name and reset button
- **Summary Stats**: 4 metric cards showing:
  - Vehicles Tracked
  - Plates Detected
  - Violations Found
  - Average Speed

- **Processing Info**: Video and processing details

#### 3. Tabs Interface

**Tab 1: Violations** (⚠️)
- Sortable violations table
- Severity filter dropdown
- CSV export button
- Color-coded severity rows
- Confidence indicators
- Validation badges

**Tab 2: All Vehicles** (🚗)
- Vehicle detail cards grid
- Expandable information
- Violation indicators
- Plate validation status
- Tracking statistics
- Trajectory preview

**Tab 3: Raw Data** (📄)
- Complete JSON response
- Copy to clipboard button
- Syntax-highlighted display

---

## 🏗️ Component Architecture

### Core Components

#### App.jsx
**Purpose**: Main application container

**State Management**:
- `data`: AI analysis results
- `activeTab`: Current tab selection
- `showRawJson`: Toggle for JSON display

**Features**:
- Tab navigation
- Reset functionality
- Conditional rendering based on data state

---

#### UploadForm.jsx
**Purpose**: Handle video file selection and upload

**Features**:
- ✅ Drag & drop support
- ✅ File validation (format, size)
- ✅ File preview with metadata
- ✅ Progress indicator
- ✅ Error handling
- ✅ Visual states (dragging, has-file)

**Validation**:
- Allowed formats: `.mp4`, `.avi`, `.mov`, `.mkv`
- Max size: 200 MB
- Real-time validation feedback

---

#### SummaryStats.jsx
**Purpose**: Display summary statistics and metrics

**Features**:
- ✅ Visual metric cards with icons
- ✅ Color-coded statistics
- ✅ Detection and violation rates
- ✅ Processing metadata grid
- ✅ Duration formatting

**Metrics Displayed**:
- Total vehicles tracked
- Plates detected (with detection rate)
- Violations found (with violation rate)
- Average speed (compared to limit)
- Video duration
- Processing time
- Frame rate
- Frames processed

---

#### ViolationsTable.jsx
**Purpose**: Display violations in interactive table

**Features**:
- ✅ Sortable columns (severity, speed, overspeed, confidence, timestamp)
- ✅ Severity filter dropdown
- ✅ CSV export functionality
- ✅ Color-coded severity rows
- ✅ Confidence bars
- ✅ Validation indicators
- ✅ Empty state handling

**Columns**:
- Severity (with badge and icon)
- Plate Number (with validation status)
- Speed (km/h)
- Speed Limit
- Overspeed amount
- Timestamp
- Confidence (visual bar + percentage)

**Severity Icons**:
- 🟢 Low
- 🟡 Medium
- 🟠 High
- 🔴 Critical

---

#### VehicleDetails.jsx
**Purpose**: Display detailed vehicle tracking information

**Features**:
- ✅ Grid layout of vehicle cards
- ✅ Expandable detail view
- ✅ Violation indicators
- ✅ Plate validation status
- ✅ Confidence scoring
- ✅ Tracking statistics
- ✅ Trajectory preview
- ✅ Filter by validated plates

**Information Displayed**:
- Vehicle ID and status
- License plate (if detected)
- OCR confidence and validation
- Speed and violation status
- Tracking frames and trajectory
- Raw OCR text
- Applied corrections
- Sampled trajectory points

---

## 🎨 Styling System

### CSS Variables
```css
--primary: #2563eb;
--success: #10b981;
--danger: #ef4444;
--warning: #f59e0b;

--gray-50 through --gray-900
--shadow-sm through --shadow-xl
--radius-sm through --radius-lg
```

### Design Principles
- **Gradient Backgrounds**: Modern, eye-catching aesthetics
- **Card-Based Layout**: Organized, scannable information
- **Color Coding**: Immediate visual understanding
- **Shadows & Depth**: Clear visual hierarchy
- **Smooth Animations**: Professional feel
- **Responsive Grid**: Adapts to screen size

### Key Visual Elements
- Gradient header (purple to blue)
- White content cards with shadows
- Color-coded severity levels
- Animated hover states
- Progress indicators with gradients
- Icon-enhanced metrics

---

## 🔄 Data Flow

```text
1. User selects/drops video file
   ↓
2. File validation (format, size)
   ↓
3. Submit to backend POST /api/upload-video
   ↓
4. Show progress indicator (simulated)
   ↓
5. Backend forwards to AI-Service
   ↓
6. AI analyzes video (real progress)
   ↓
7. Backend returns v1.5 JSON response
   ↓
8. Frontend parses and displays:
   - Summary statistics
   - Violations table
   - Vehicle details
   - Raw JSON
```

---

## 📤 API Integration

### Backend Communication (`src/api/backend.js`)

```javascript
// Full analysis
const result = await uploadVideo(file);

// Summary only (faster)
const summary = await uploadVideoSummary(file);

// Health checks
const health = await checkHealth();
const detailedHealth = await checkDetailedHealth();
```

### Environment Configuration
```bash
# Development
VITE_API_BASE=http://localhost:8080

# Docker
VITE_API_BASE=http://traffic-ktor-backend:8080
```

---

## 📊 Response Handling

### v1.5 Response Structure
```javascript
{
  status: "success",
  processing_time_seconds: 45.3,
  video_info: { ... },
  summary: {
    total_vehicles_tracked: 12,
    vehicles_with_plates: 8,
    violations_detected: 2,
    average_speed_kmh: 48.5
  },
  violations: [
    {
      violation_id: "v_001",
      plate_number: "123TUN456",
      plate_confidence: 0.92,
      plate_validated: true,
      speed_kmh: 72.4,
      severity: "high",
      ...
    }
  ],
  tracked_vehicles: [ ... ],
  configuration: { ... }
}
```

### Key Changes from v1.0
- ✅ `violations` is now an array (not object)
- ✅ Added `summary` object
- ✅ Added `tracked_vehicles` array
- ✅ Added confidence and validation fields
- ✅ Added severity classification
- ✅ Removed `violations_nbr` and `details` root keys

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

⚠️ **Ensure backend is running on port 8080**

---

## 🏗️ Build for Production

### 1️⃣ Build Optimized Bundle
```bash
npm run build
```

Output: `dist/` directory

### 2️⃣ Preview Production Build
```bash
npm run preview
```

### 3️⃣ Deploy
Upload `dist/` contents to your web server or CDN.

---

## 🐳 Running with Docker

### Build Image
```bash
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
docker-compose up traffic-frontend
```

---

## 🧪 Testing

### Manual Testing Checklist

**Upload Flow**:
- [ ] Drag & drop works
- [ ] Browse file works
- [ ] File preview shows correctly
- [ ] Validation rejects invalid formats
- [ ] Validation rejects oversized files
- [ ] Progress bar animates
- [ ] Upload button disables during processing
- [ ] Error messages display correctly

**Results Display**:
- [ ] Summary stats calculate correctly
- [ ] Violations table sorts properly
- [ ] Severity filter works
- [ ] CSV export downloads
- [ ] Vehicle cards expand/collapse
- [ ] Confidence bars display
- [ ] Validation badges show
- [ ] Tab switching works
- [ ] Raw JSON displays

**Responsive Design**:
- [ ] Mobile view works
- [ ] Tablet view works
- [ ] Desktop view works
- [ ] Elements wrap properly
- [ ] Touch interactions work

---

## 🎯 Features Breakdown

### Priority 1 (Implemented in v1.5)
- ✅ Modern gradient-based UI
- ✅ Drag & drop file upload
- ✅ Real-time progress indicators
- ✅ Summary statistics dashboard
- ✅ Interactive violations table
- ✅ Vehicle detail cards
- ✅ CSV export
- ✅ Severity color coding
- ✅ Confidence indicators
- ✅ Validation badges
- ✅ Responsive design
- ✅ Empty state handling
- ✅ Error message improvements

### Priority 2 (Planned for v2.0)
- [ ] User authentication UI
- [ ] Historical data browsing
- [ ] Advanced filtering controls
- [ ] Date range picker
- [ ] Charts and graphs
- [ ] Video playback with overlays
- [ ] Export to PDF
- [ ] Dark mode toggle
- [ ] Keyboard shortcuts
- [ ] Print-friendly view

---

## 🐛 Troubleshooting

### Issue: "Failed to fetch"
**Cause**: Backend not reachable

**Solution**:
```bash
# Check backend health
curl http://localhost:8080/health

# Verify environment variable
echo $VITE_API_BASE

# Check browser console for CORS errors
```

### Issue: Upload fails
**Causes**:
- Invalid video format
- File too large
- Backend not running
- Network error

**Solutions**:
- Verify file format (.mp4, .avi, .mov, .mkv)
- Check file size (max 200 MB)
- Ensure backend is running
- Check browser network tab for errors

### Issue: Styling looks broken
**Solutions**:
```bash
# Clear browser cache
# Hard refresh: Ctrl+Shift+R (Windows) or Cmd+Shift+R (Mac)

# Rebuild
npm run build
npm run preview
```

### Issue: Docker container won't start
**Solutions**:
```bash
# Check logs
docker logs traffic-frontend

# Rebuild without cache
docker build --no-cache -t traffic-frontend .

# Check port availability
lsof -i :5173
```

---

## 📊 Performance Optimization

### Current Optimizations
- ✅ Code splitting by route
- ✅ Lazy loading components
- ✅ Optimized bundle size
- ✅ CSS minification
- ✅ Asset optimization

### Future Optimizations (v2.0)
- [ ] Image lazy loading
- [ ] Virtual scrolling for large tables
- [ ] Service worker for caching
- [ ] Progressive Web App (PWA)
- [ ] Code splitting improvements

---

## 📱 Browser Support

| Browser | Version | Status |
|---------|---------|--------|
| Chrome | 90+ | ✅ Fully supported |
| Firefox | 88+ | ✅ Fully supported |
| Safari | 14+ | ✅ Fully supported |
| Edge | 90+ | ✅ Fully supported |
| Mobile Safari | iOS 14+ | ✅ Fully supported |
| Chrome Mobile | Latest | ✅ Fully supported |
| IE | Any | ❌ Not supported |

---

## 📝 Development Commands

```bash
# Install dependencies
npm install

# Start dev server (with host access)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

---

## 🚀 v2.0 Roadmap

### Planned Features
- [ ] User authentication UI (login, register)
- [ ] Historical violations browser
- [ ] Advanced filtering (date range, severity, plate)
- [ ] Statistics charts (Chart.js)
- [ ] Video playback with violation overlays
- [ ] PDF report generation
- [ ] Dark mode with toggle
- [ ] Keyboard shortcuts
- [ ] Notification system
- [ ] Multi-language support
- [ ] Mobile app wrapper

---

## 📖 Dependencies

### Core
```json
{
  "react": "^19.2.0",
  "react-dom": "^19.2.0"
}
```

### Dev
```json
{
  "@vitejs/plugin-react": "^5.1.1",
  "vite": "^7.2.4",
  "eslint": "^9.39.1"
}
```

### Planned for v2.0
- `react-router-dom`: Routing
- `zustand` or `redux`: State management
- `recharts`: Charts and graphs
- `date-fns`: Date manipulation
- `react-hot-toast`: Notifications

---

## 🎨 Customization Guide

### Changing Color Scheme
Edit CSS variables in `src/styles/app.css`:
```css
:root {
  --primary: #your-color;
  --success: #your-color;
  --danger: #your-color;
}
```

### Modifying Upload Limits
Edit validation in `UploadForm.jsx`:
```javascript
const maxSizeMB = 200;  // Change this
const allowedFormats = ['.mp4', '.avi', '.mov', '.mkv'];
```

---

**Frontend Status**: v1.5 Stabilization Complete ✅  
**Ready for**: v2.0 Feature Expansion 🚀