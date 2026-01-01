# 🚦 Dashboard Frontend — Traffic Monitoring System

**Current Version**: v2.0 (Authentication & History) ✅  
**Previous Version**: v1.5 (Stabilization)

This module represents the **Frontend Dashboard** of the Traffic Monitoring System. It provides a **modern, interactive web interface** with user authentication, video history, statistics, and comprehensive violation analysis.

---

## 🆕 What's New in v2.0

### Authentication UI
- ✅ **Login Page**: Modern authentication interface
- ✅ **Registration**: Self-service account creation
- ✅ **Token Management**: Automatic JWT token storage
- ✅ **Session Persistence**: Remember logged-in users
- ✅ **Logout Functionality**: Secure session termination

### Video History
- ✅ **History Browser**: View all previously analyzed videos
- ✅ **Pagination**: Navigate through large video collections
- ✅ **Video Cards**: Rich metadata display with summaries
- ✅ **Delete Functionality**: Remove unwanted videos
- ✅ **Status Indicators**: Processing and AI status badges

### Statistics Dashboard
- ✅ **Overview Cards**: Key metrics at a glance
- ✅ **Bar Charts**: Visual severity distribution
- ✅ **Advanced Filters**: Search by multiple criteria
- ✅ **Date Range**: Filter violations by time period
- ✅ **Recent Violations**: Quick access to latest data

### Enhanced Features
- ✅ **Protected Routes**: Authentication required for uploads
- ✅ **User Context**: All data scoped to logged-in user
- ✅ **Navigation**: Tab-based interface (Upload/History/Stats)
- ✅ **Export**: CSV download for violations
- ✅ **Responsive**: Mobile and tablet friendly

---

## 🎯 Responsibilities

### Core (V1.5)
- ✅ Provide modern, intuitive user interface
- ✅ Handle video file upload with validation
- ✅ Display summary statistics and metrics
- ✅ Present violations in sortable, filterable table
- ✅ Show detailed vehicle tracking information
- ✅ Export data to CSV format
- ✅ Handle loading states and errors gracefully
- ✅ Support responsive design for all devices

### New in V2.0
- ✅ **User Authentication**: Login and registration flows
- ✅ **Session Management**: Token storage and validation
- ✅ **Video History**: Browse past uploads with pagination
- ✅ **Statistics**: Visual analytics and charts
- ✅ **Advanced Filtering**: Multi-criteria violation search
- ✅ **User Isolation**: Display only user's own data

---

## 🧱 Tech Stack

- **React 19.2.0**
- **Vite 7.2.4**
- **JavaScript (ES Modules)**
- **Fetch API**
- **localStorage** (token persistence)
- **Custom CSS** (gradient-based design)
- **Node.js 22 (Docker)**

---

## 🎨 User Interface Overview

### Authentication Flow

#### Login Page
- Email/password form
- Registration toggle
- Error handling
- Loading states
- Secure password input

#### Features After Login
- User email display
- Logout button
- Token-based requests
- Automatic re-authentication

### Main Sections

#### 1. Upload Section (Tab 1)
- **Welcome Message**: Feature highlights
- **Drag & Drop Zone**: File upload area
- **File Preview**: Selected file information
- **Progress Bar**: Real-time upload/processing progress
- **Validation**: Format and size checks
- **Authentication**: JWT token included in requests

#### 2. Results Dashboard
- **Header**: Video name and reset button
- **Summary Stats**: 4 metric cards showing:
  - Vehicles Tracked
  - Plates Detected
  - Violations Found
  - Average Speed
- **Processing Info**: Video and processing details
- **Tabs**: Violations / All Vehicles / Raw Data

#### 3. History Section (Tab 2) ⭐ NEW
- **Video Grid**: Card-based layout
- **Video Cards**: 
  - Filename and upload date
  - Duration, FPS, frame count
  - Vehicle and violation summary
  - Delete button
  - Status badges
- **Pagination**: Navigate through pages
- **Empty State**: Friendly no-data message

#### 4. Statistics Section (Tab 3) ⭐ NEW
- **Overview Cards**: Total violations, average speed, severity counts
- **Bar Chart**: Visual severity distribution
- **Filter Panel**: 
  - Severity dropdown
  - Plate number search
  - Date range picker
  - Validation status
- **Recent Violations Table**: Compact violation list
- **Export Button**: Download CSV

---

## 🏗️ Component Architecture

### Core Components

#### AppV2.jsx (NEW)
**Purpose**: Main V2 application container with authentication

**State Management**:
- `user`: Current user object
- `token`: JWT authentication token
- `data`: AI analysis results
- `activeTab`: Current tab selection (violations/vehicles/raw)
- `currentView`: Main view selection (upload/history/stats)

**Features**:
- Authentication flow handling
- Token persistence in localStorage
- Protected route logic
- View navigation
- Logout functionality

---

#### Login.jsx (NEW)
**Purpose**: User authentication interface

**Features**:
- ✅ Email/password form
- ✅ Login/register toggle
- ✅ Form validation
- ✅ Error display
- ✅ Loading states
- ✅ Token callback to parent

**State**:
- `isRegister`: Toggle between login/register
- `email`: User email input
- `password`: User password input
- `loading`: Form submission state
- `error`: Error message display

---

#### History.jsx (NEW)
**Purpose**: Display user's video history

**Features**:
- ✅ Paginated video list
- ✅ Video metadata cards
- ✅ Delete functionality
- ✅ Status indicators
- ✅ Empty state handling
- ✅ Error handling
- ✅ Loading spinner

**API Integration**:
```javascript
// Load videos
const videos = await getVideos(token, page, size);

// Delete video
await deleteVideo(token, videoId);
```

---

#### Stats.jsx (NEW)
**Purpose**: Display statistics and analytics

**Features**:
- ✅ Overview metrics
- ✅ Bar chart visualization
- ✅ Advanced filter form
- ✅ Recent violations table
- ✅ CSV export
- ✅ Real-time updates

**API Integration**:
```javascript
// Load statistics
const stats = await getStats(token);

// Filter violations
const violations = await filterViolations(token, filters);

// Export CSV
await exportViolationsCSV(token);
```

---

#### UploadForm.jsx (Enhanced)
**Purpose**: Handle video upload with authentication

**NEW Props**:
- `token`: JWT token for authenticated uploads

**Features**:
- ✅ Drag & drop support
- ✅ File validation (format, size)
- ✅ File preview with metadata
- ✅ Progress indicator
- ✅ Error handling
- ✅ **Token-based authentication** (NEW)

**Authentication**:
```javascript
// Upload with token
const result = await uploadVideo(file, token);
```

---

### API Client (Enhanced)

#### api/v2.js (NEW)
**Purpose**: V2 API integration with authentication

**Functions**:
```javascript
// Authentication
register(email, password)
login(email, password)

// Videos
getVideos(token, page, size)
getVideo(token, id)
deleteVideo(token, id)

// Violations
filterViolations(token, filter)
getViolation(token, id)

// Statistics
getStats(token)

// Export
exportViolationsCSV(token)
```

---

#### api/backend.js (Enhanced)
**Purpose**: Backend API with optional authentication

**Enhanced Functions**:
```javascript
// Upload with optional token
uploadVideo(file, token = null)
uploadVideoSummary(file, token = null)

// Health checks
checkHealth()
checkDetailedHealth()
```

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

### V2 Styles (NEW)
**File**: `src/styles/v2.css`

**New Components**:
- `.login-container`, `.login-card`
- `.history-container`, `.video-grid`, `.video-card`
- `.stats-container`, `.stats-overview`, `.bar-chart`
- `.filter-section`, `.violations-table-compact`
- `.pagination`, `.nav-btn`

---

## 🔄 Data Flow

### Authentication Flow
```text
1. User opens app
   ↓
2. Check localStorage for token
   ↓
3. If token exists → Auto-login
   ↓
4. If no token → Show login page
   ↓
5. User registers/logs in
   ↓
6. Receive JWT token from backend
   ↓
7. Store token in localStorage
   ↓
8. Include token in all API requests
```

### Video Upload Flow (Authenticated)
```text
1. User selects/drops video file
   ↓
2. File validation (format, size)
   ↓
3. Submit to backend POST /api/upload-video
   Headers: { Authorization: Bearer <token> }
   ↓
4. Backend validates token
   ↓
5. Backend forwards to AI-Service
   ↓
6. Backend saves result to DB with userId
   ↓
7. Frontend displays results
   ↓
8. Video appears in user's history
```

---

## 📤 API Integration

### Authentication APIs (NEW)

```javascript
// Register
const result = await register(email, password);
// Returns: { token, user, expiresAt }

// Login
const result = await login(email, password);
// Returns: { token, user, expiresAt }

// Store token
localStorage.setItem('token', result.token);
localStorage.setItem('user', JSON.stringify(result.user));
```

### V2 APIs (NEW)

```javascript
// Get videos with pagination
const videos = await getVideos(token, 0, 20);
// Returns: { data, page, size, totalElements, totalPages }

// Get statistics
const stats = await getStats(token);
// Returns: { total, averageSpeed, bySeverity }

// Filter violations
const violations = await filterViolations(token, {
  severity: 'HIGH',
  startDate: '2024-01-01T00:00:00',
  page: 0,
  size: 20
});
```

### V1.5 APIs (Enhanced)

```javascript
// Upload video (now with token)
const result = await uploadVideo(file, token);
// Returns: Full AI analysis response

// Upload video summary
const summary = await uploadVideoSummary(file, token);
// Returns: Summary statistics only
```

---

## 📊 Feature Flags

### Environment Variables

```bash
# Enable V2 features
VITE_ENABLE_V2=true              # Enable history and stats
VITE_ENABLE_AUTH=true            # Require authentication

# Backend URL
VITE_API_BASE=http://localhost:8080
```

### Conditional Features

The frontend adapts based on feature flags:

```javascript
const enableV2 = import.meta.env.VITE_ENABLE_V2 === 'true';
const enableAuth = import.meta.env.VITE_ENABLE_AUTH === 'true';

// Show login page only if auth enabled
if (enableAuth && !token) {
  return <Login onLogin={handleLogin} />;
}

// Show history/stats tabs only if V2 enabled
{enableV2 && (
  <>
    <NavButton view="history">History</NavButton>
    <NavButton view="stats">Statistics</NavButton>
  </>
)}
```

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
VITE_ENABLE_V2=true
VITE_ENABLE_AUTH=true
```

### 3️⃣ Start Development Server
```bash
npm run dev
```

### 4️⃣ Access Dashboard
Open browser: **http://localhost:5173**

⚠️ **Ensure backend and database are running**

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
docker build -t traffic-dashboard-frontend \
  --build-arg VITE_API_BASE=http://localhost:8080 \
  --build-arg VITE_ENABLE_V2=true \
  --build-arg VITE_ENABLE_AUTH=true \
  .
```

### Run Container
```bash
docker run -p 5173:5173 traffic-dashboard-frontend
```

### Run with Docker Compose
```bash
docker-compose up traffic-frontend
```

---

## 🧪 Testing

### Manual Testing Checklist

**Authentication**:
- [ ] Registration form works
- [ ] Login form works
- [ ] Token is stored in localStorage
- [ ] Logout clears token
- [ ] Auto-login on refresh works
- [ ] Invalid credentials show error
- [ ] Token expiration handled

**Upload Flow**:
- [ ] Drag & drop works
- [ ] Browse file works
- [ ] File preview shows correctly
- [ ] Validation rejects invalid formats
- [ ] Validation rejects oversized files
- [ ] Progress bar animates
- [ ] Token included in request
- [ ] 401 error redirects to login
- [ ] Upload button disables during processing
- [ ] Error messages display correctly

**History**:
- [ ] Videos load correctly
- [ ] Pagination works
- [ ] Video cards display metadata
- [ ] Delete confirmation appears
- [ ] Delete removes video
- [ ] Empty state shows when no videos
- [ ] Loading spinner appears

**Statistics**:
- [ ] Overview cards show correct data
- [ ] Bar chart renders correctly
- [ ] Filters work (severity, plate, date)
- [ ] CSV export downloads
- [ ] Recent violations table displays

**Responsive Design**:
- [ ] Mobile view works
- [ ] Tablet view works
- [ ] Desktop view works
- [ ] Touch interactions work
- [ ] Navigation menu works on mobile

---

## 🎯 Features Breakdown

### Priority 1 (Implemented in v2.0)
- ✅ User authentication UI
- ✅ Login and registration pages
- ✅ Token management
- ✅ Video history browser
- ✅ Statistics dashboard
- ✅ Advanced filtering
- ✅ CSV export
- ✅ Navigation tabs
- ✅ Protected routes
- ✅ User-scoped data

### Priority 2 (Planned for v2.1)
- [ ] Password reset flow
- [ ] Email verification
- [ ] User profile page
- [ ] Settings panel
- [ ] Remember me option
- [ ] Session timeout warning
- [ ] Dark mode toggle
- [ ] Keyboard shortcuts
- [ ] Notification system

### Priority 3 (Planned for v3.0)
- [ ] Real-time updates (WebSocket)
- [ ] Video playback with overlays
- [ ] Charts and graphs
- [ ] Advanced analytics
- [ ] Multi-language support
- [ ] Mobile app wrapper
- [ ] Offline support
- [ ] PDF report generation

---

## 🐛 Troubleshooting

### Issue: "Failed to fetch" on login
**Cause**: Backend not reachable or authentication disabled

**Solution**:
```bash
# Check backend health
curl http://localhost:8080/health/detailed

# Verify backend has authentication enabled
curl http://localhost:8080/ | grep "Authentication"

# Check environment variable
echo $VITE_API_BASE
```

### Issue: History page is empty
**Causes**:
- No videos uploaded yet
- Videos uploaded by different user
- Database empty
- Backend V2 features disabled

**Solutions**:
- Upload a video first
- Ensure logged in with correct account
- Check backend logs: `docker logs traffic-ktor-backend`
- Verify ENABLE_V2_PERSISTENCE=true in backend

### Issue: Upload returns 401 Unauthorized
**Causes**:
- Not logged in
- Token expired
- Token not included in request

**Solutions**:
```bash
# Check if logged in
localStorage.getItem('token')

# Login again
# Check browser network tab for Authorization header
# Verify token format: "Bearer eyJ..."
```

### Issue: Token expired
**Solution**:
```javascript
// Token expires after 1 hour (default)
// Login again to get fresh token
// Or configure JWT_VALIDITY_MS for longer expiration
```

### Issue: Statistics not loading
**Check**:
- Ensure logged in
- Check browser console for errors
- Verify backend /api/v2/stats endpoint
- Check network tab for 401/403 errors

---

## 📊 Performance Optimization

### Current Optimizations
- ✅ Code splitting by route
- ✅ Lazy loading components
- ✅ Optimized bundle size (~150KB gzipped)
- ✅ CSS minification
- ✅ Asset optimization
- ✅ Token-based caching

### Future Optimizations (v2.1)
- [ ] Virtual scrolling for large tables
- [ ] Service worker for caching
- [ ] Progressive Web App (PWA)
- [ ] Image lazy loading
- [ ] Infinite scroll for history

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

## 🚀 v2.1 Roadmap

### Planned Features
- [ ] Password reset flow
- [ ] Email verification UI
- [ ] User profile page
- [ ] Account settings
- [ ] Remember me checkbox
- [ ] Session timeout warning
- [ ] Dark mode toggle
- [ ] Keyboard shortcuts
- [ ] Toast notifications
- [ ] Loading skeletons
- [ ] Error boundaries
- [ ] Accessibility improvements

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

### Future Dependencies
- `react-router-dom`: Client-side routing
- `zustand` or `redux`: Advanced state management
- `recharts`: Enhanced charts and graphs
- `date-fns`: Date manipulation
- `react-hot-toast`: Notifications
- `react-query`: API state management

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

### Customizing Token Expiration
Configure on backend (affects all clients):
```bash
JWT_VALIDITY_MS=7200000  # 2 hours
```

---

**Frontend Status**: v2.0 Complete ✅  
**Features**: Authentication + History + Statistics 🚀  
**Ready for**: v2.1 Enhanced UX 📋