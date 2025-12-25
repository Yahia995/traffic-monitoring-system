import { useState } from "react";
import UploadForm from "./components/UploadForm";
import ViolationsTable from "./components/ViolationsTable";
import SummaryStats from "./components/SummaryStats";
import VehicleDetails from "./components/VehicleDetails";
import "./styles/app.css";

export default function App() {
  const [data, setData] = useState(null);
  const [activeTab, setActiveTab] = useState('violations');
  const [showRawJson, setShowRawJson] = useState(false);

  const handleReset = () => {
    if (window.confirm('Clear current results and upload a new video?')) {
      setData(null);
      setActiveTab('violations');
      setShowRawJson(false);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <h1 className="app-title">
            <span className="title-icon">🚦</span>
            Traffic Monitoring System
          </h1>
          <div className="version-badge">v1.5</div>
        </div>
        <p className="app-subtitle">
          AI-Powered Traffic Violation Detection
        </p>
      </header>

      <main className="app-main">
        {!data ? (
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
            <UploadForm onResult={setData} />
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
                        <div className="success-stats">
                          <div className="success-stat">
                            <strong>{data.summary.total_vehicles_tracked}</strong>
                            <span>Vehicles Tracked</span>
                          </div>
                          <div className="success-stat">
                            <strong>{data.summary.average_speed_kmh.toFixed(1)} km/h</strong>
                            <span>Average Speed</span>
                          </div>
                        </div>
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
        )}
      </main>

      <footer className="app-footer">
        <p>Traffic Monitoring System v1.5 • Enhanced with AI-powered OCR and validation</p>
      </footer>
    </div>
  );
}