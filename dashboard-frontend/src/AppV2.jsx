import { useState, useEffect } from "react";
import UploadForm from "./components/UploadForm";
import ViolationsTable from "./components/ViolationsTable";
import SummaryStats from "./components/SummaryStats";
import VehicleDetails from "./components/VehicleDetails";
import Login from "./components/v2/Login";
import History from "./components/v2/History";
import Stats from "./components/v2/Stats";
import "./styles/app.css";
import "./styles/v2.css";

export default function AppV2() {
  const [data, setData] = useState(null);
  const [activeTab, setActiveTab] = useState('violations');
  const [currentView, setCurrentView] = useState('upload'); // upload, history, stats
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  const enableV2 = 'true' // import.meta.env.VITE_ENABLE_V2 === 'true';
  const enableAuth = 'true' // import.meta.env.VITE_ENABLE_AUTH === 'true';

  useEffect(() => {
    if (enableAuth) {
      const savedToken = localStorage.getItem('token');
      const savedUser = localStorage.getItem('user');
      if (savedToken && savedUser) {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      }
    }
  }, [enableAuth]);

  const handleLogin = (authData) => {
    setToken(authData.token);
    setUser(authData.user);
    localStorage.setItem('token', authData.token);
    localStorage.setItem('user', JSON.stringify(authData.user));
  };

  const handleLogout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setCurrentView('upload');
    setData(null);
  };

  const handleReset = () => {
    if (window.confirm('Clear current results and upload a new video?')) {
      setData(null);
      setActiveTab('violations');
      setCurrentView('upload');
    }
  };

  if (enableAuth && !token) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <h1 className="app-title">
            <span className="title-icon">🚦</span>
            Traffic Monitoring System
          </h1>
          <div className="version-badge">v2.0</div>
        </div>
        <p className="app-subtitle">
          AI-Powered Traffic Violation Detection
        </p>
        {enableAuth && user && (
          <div className="user-info">
            <span>👤 {user.email}</span>
            <button onClick={handleLogout} className="logout-btn">Logout</button>
          </div>
        )}
      </header>

      <nav className="main-nav">
        <button
          className={`nav-btn ${currentView === 'upload' ? 'active' : ''}`}
          onClick={() => setCurrentView('upload')}
        >
          📤 Upload
        </button>
        {enableV2 && (
          <>
            <button
              className={`nav-btn ${currentView === 'history' ? 'active' : ''}`}
              onClick={() => setCurrentView('history')}
            >
              📜 History
            </button>
            <button
              className={`nav-btn ${currentView === 'stats' ? 'active' : ''}`}
              onClick={() => setCurrentView('stats')}
            >
              📊 Statistics
            </button>
          </>
        )}
      </nav>

      <main className="app-main">
        {currentView === 'upload' && (
          !data ? (
            <div className="upload-section">
              <div className="welcome-message">
                <h2>Welcome to Traffic Monitoring</h2>
                <p>Upload a traffic video to detect vehicles, recognize license plates, and identify speed violations.</p>
                <div className="features-list">
                  <div className="feature-item">
                    <span className="feature-icon">🎯</span>
                    <span>Vehicle Detection</span>
                  </div>
                  <div className="feature-item">
                    <span className="feature-icon">🔍</span>
                    <span>License Plate OCR</span>
                  </div>
                  <div className="feature-item">
                    <span className="feature-icon">📊</span>
                    <span>Speed Analysis</span>
                  </div>
                  <div className="feature-item">
                    <span className="feature-icon">⚠️</span>
                    <span>Violation Detection</span>
                  </div>
                </div>
              </div>
              <UploadForm onResult={setData} token={token} />
            </div>
          ) : (
            <div className="results-section">
              <div className="results-header">
                <div className="results-title">
                  <h2>Analysis Results</h2>
                  <span className="video-name">{data.video_info.filename}</span>
                </div>
                <button onClick={handleReset} className="reset-button">
                  🔄 New Analysis
                </button>
              </div>

              <SummaryStats data={data} />

              <div className="tabs-container">
                <div className="tabs">
                  <button
                    className={`tab ${activeTab === 'violations' ? 'active' : ''}`}
                    onClick={() => setActiveTab('violations')}
                  >
                    <span className="tab-icon">⚠️</span>
                    <span>Violations</span>
                    {data.summary.violations_detected > 0 && (
                      <span className="tab-badge">{data.summary.violations_detected}</span>
                    )}
                  </button>
                  <button
                    className={`tab ${activeTab === 'vehicles' ? 'active' : ''}`}
                    onClick={() => setActiveTab('vehicles')}
                  >
                    <span className="tab-icon">🚗</span>
                    <span>All Vehicles</span>
                    <span className="tab-badge">{data.summary.total_vehicles_tracked}</span>
                  </button>
                  <button
                    className={`tab ${activeTab === 'raw' ? 'active' : ''}`}
                    onClick={() => setActiveTab('raw')}
                  >
                    <span className="tab-icon">📝</span>
                    <span>Raw Data</span>
                  </button>
                </div>

                <div className="tab-content">
                  {activeTab === 'violations' && (
                    <div className="tab-panel">
                      {data.summary.violations_detected > 0 ? (
                        <>
                          <div className="panel-header">
                            <h3>Detected Violations</h3>
                            <p className="panel-description">
                              {data.summary.violations_detected} vehicle{data.summary.violations_detected !== 1 ? 's' : ''} exceeded the speed limit of {data.configuration.speed_limit_kmh} km/h
                            </p>
                          </div>
                          <ViolationsTable violations={data.violations} />
                        </>
                      ) : (
                        <div className="empty-state success">
                          <div className="empty-icon">✅</div>
                          <h3>No Violations Detected</h3>
                          <p>All vehicles were within the speed limit</p>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTab === 'vehicles' && (
                    <div className="tab-panel">
                      <div className="panel-header">
                        <h3>All Tracked Vehicles</h3>
                        <p className="panel-description">
                          Complete tracking data for all {data.summary.total_vehicles_tracked} detected vehicles
                        </p>
                      </div>
                      <VehicleDetails vehicles={data.tracked_vehicles} />
                    </div>
                  )}

                  {activeTab === 'raw' && (
                    <div className="tab-panel">
                      <div className="panel-header">
                        <h3>Raw JSON Response</h3>
                        <button
                          onClick={() => {
                            navigator.clipboard.writeText(JSON.stringify(data, null, 2));
                            alert('Copied to clipboard!');
                          }}
                          className="copy-button"
                        >
                          📋 Copy to Clipboard
                        </button>
                      </div>
                      <pre className="json-display">
                        {JSON.stringify(data, null, 2)}
                      </pre>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )
        )}

        {currentView === 'history' && enableV2 && (
          <History token={token} />
        )}

        {currentView === 'stats' && enableV2 && (
          <Stats token={token} />
        )}
      </main>

      <footer className="app-footer">
        <p>Traffic Monitoring System v2.0 • Database & Authentication</p>
      </footer>
    </div>
  );
}