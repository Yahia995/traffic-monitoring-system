import { useState } from "react";

export default function VehicleDetails({ vehicles }) {
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [showValidatedOnly, setShowValidatedOnly] = useState(false);

  if (!vehicles || vehicles.length === 0) {
    return null;
  }

  const filteredVehicles = showValidatedOnly
    ? vehicles.filter(v => v.plate_info.validated)
    : vehicles;

  const getStatusIcon = (vehicle) => {
    if (vehicle.speed_info.is_violation) return '🚨';
    if (vehicle.plate_info.plate_number) return '✅';
    return '❓';
  };

  const formatTime = (seconds) => {
    return `${seconds.toFixed(1)}s`;
  };

  return (
    <div className="vehicle-details-section">
      <div className="section-header">
        <h3>Vehicle Tracking Details</h3>
        <label className="toggle-label">
          <input
            type="checkbox"
            checked={showValidatedOnly}
            onChange={(e) => setShowValidatedOnly(e.target.checked)}
          />
          <span>Show only validated plates</span>
        </label>
      </div>

      <div className="vehicles-grid">
        {filteredVehicles.map((vehicle, index) => (
          <div 
            key={vehicle.vehicle_id}
            className={`vehicle-card ${selectedVehicle === vehicle.vehicle_id ? 'selected' : ''} ${vehicle.speed_info.is_violation ? 'violation' : ''}`}
            onClick={() => setSelectedVehicle(selectedVehicle === vehicle.vehicle_id ? null : vehicle.vehicle_id)}
          >
            <div className="vehicle-header">
              <div className="vehicle-id">
                <span className="status-icon">{getStatusIcon(vehicle)}</span>
                <span className="id-text">{vehicle.vehicle_id}</span>
              </div>
              {vehicle.speed_info.is_violation && (
                <span className="violation-badge">VIOLATION</span>
              )}
            </div>

            <div className="vehicle-plate">
              {vehicle.plate_info.plate_number ? (
                <>
                  <div className="plate-display">
                    {vehicle.plate_info.plate_number}
                  </div>
                  <div className="plate-meta">
                    <span className={`confidence ${vehicle.plate_info.confidence < 0.7 ? 'low' : vehicle.plate_info.confidence < 0.85 ? 'medium' : 'high'}`}>
                      {(vehicle.plate_info.confidence * 100).toFixed(0)}% confident
                    </span>
                    {vehicle.plate_info.validated ? (
                      <span className="validated">✓ Validated</span>
                    ) : (
                      <span className="unvalidated">⚠ Unvalidated</span>
                    )}
                  </div>
                </>
              ) : (
                <div className="no-plate">
                  <span>No plate detected</span>
                  {vehicle.plate_info.validation_errors && (
                    <span className="error-hint">
                      {vehicle.plate_info.validation_errors.join(', ')}
                    </span>
                  )}
                </div>
              )}
            </div>

            <div className="vehicle-stats">
              <div className="stat-row">
                <span className="stat-label">Speed:</span>
                <span className={`stat-value ${vehicle.speed_info.is_violation ? 'violation-speed' : ''}`}>
                  {vehicle.speed_info.speed_kmh.toFixed(1)} km/h
                </span>
              </div>
              <div className="stat-row">
                <span className="stat-label">Tracked:</span>
                <span className="stat-value">
                  {vehicle.tracking_info.frames_tracked} frames
                </span>
              </div>
            </div>

            {selectedVehicle === vehicle.vehicle_id && (
              <div className="vehicle-expanded">
                <div className="divider"></div>
                
                <div className="detail-section">
                  <h4>Tracking Information</h4>
                  <div className="detail-grid">
                    <div className="detail-item">
                      <span className="detail-label">First Frame:</span>
                      <span className="detail-value">{vehicle.tracking_info.first_frame}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Last Frame:</span>
                      <span className="detail-value">{vehicle.tracking_info.last_frame}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Trajectory:</span>
                      <span className="detail-value">
                        {vehicle.tracking_info.trajectory_length_pixels.toFixed(1)} px
                      </span>
                    </div>
                  </div>
                </div>

                {vehicle.plate_info.plate_number && (
                  <div className="detail-section">
                    <h4>Plate Information</h4>
                    <div className="detail-grid">
                      <div className="detail-item">
                        <span className="detail-label">Raw OCR:</span>
                        <span className="detail-value">{vehicle.plate_info.raw_ocr_text}</span>
                      </div>
                      {vehicle.plate_info.corrections_applied && vehicle.plate_info.corrections_applied.length > 0 && (
                        <div className="detail-item">
                          <span className="detail-label">Corrections:</span>
                          <span className="detail-value corrections">
                            {vehicle.plate_info.corrections_applied.join(', ')}
                          </span>
                        </div>
                      )}
                      {vehicle.plate_info.detection_frame && (
                        <div className="detail-item">
                          <span className="detail-label">Detected at:</span>
                          <span className="detail-value">Frame {vehicle.plate_info.detection_frame}</span>
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {vehicle.positions && vehicle.positions.length > 0 && (
                  <div className="detail-section">
                    <h4>Trajectory Points ({vehicle.positions.length})</h4>
                    <div className="trajectory-preview">
                      {vehicle.positions.slice(0, 5).map((pos, idx) => (
                        <div key={idx} className="trajectory-point">
                          Frame {pos.frame}: ({pos.x}, {pos.y})
                        </div>
                      ))}
                      {vehicle.positions.length > 5 && (
                        <div className="trajectory-more">
                          ... and {vehicle.positions.length - 5} more points
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
      </div>

      {filteredVehicles.length === 0 && (
        <div className="no-vehicles">
          <p>No validated vehicles found</p>
        </div>
      )}
    </div>
  );
}