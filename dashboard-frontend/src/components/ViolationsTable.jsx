import { useState } from "react";

export default function ViolationsTable({ violations }) {
  const [sortBy, setSortBy] = useState('severity');
  const [sortOrder, setSortOrder] = useState('desc');
  const [filterSeverity, setFilterSeverity] = useState('all');

  if (!violations || violations.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-icon">✅</div>
        <p className="empty-text">No violations detected</p>
        <p className="empty-subtext">All vehicles were within the speed limit</p>
      </div>
    );
  }

  const getSeverityIcon = (severity) => {
    const icons = {
      low: '🟢',
      medium: '🟡',
      high: '🟠',
      critical: '🔴'
    };
    return icons[severity] || '⚪';
  };

  const getSeverityClass = (severity) => {
    return `severity-${severity}`;
  };

  const handleSort = (column) => {
    if (sortBy === column) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(column);
      setSortOrder('desc');
    }
  };

  const severityOrder = { critical: 4, high: 3, medium: 2, low: 1 };

  const sortedViolations = [...violations].sort((a, b) => {
    let aVal, bVal;

    switch(sortBy) {
      case 'severity':
        aVal = severityOrder[a.severity] || 0;
        bVal = severityOrder[b.severity] || 0;
        break;
      case 'speed':
        aVal = a.speed_kmh;
        bVal = b.speed_kmh;
        break;
      case 'overspeed':
        aVal = a.overspeed_kmh;
        bVal = b.overspeed_kmh;
        break;
      case 'confidence':
        aVal = a.plate_confidence;
        bVal = b.plate_confidence;
        break;
      case 'timestamp':
        aVal = a.timestamp_seconds;
        bVal = b.timestamp_seconds;
        break;
      default:
        return 0;
    }

    return sortOrder === 'asc' ? aVal - bVal : bVal - aVal;
  });

  const filteredViolations = filterSeverity === 'all'
    ? sortedViolations
    : sortedViolations.filter(v => v.severity === filterSeverity);

  const exportToCSV = () => {
    const headers = ['Violation ID', 'Plate Number', 'Speed (km/h)', 'Speed Limit', 'Overspeed', 'Timestamp', 'Severity', 'Confidence', 'Validated'];
    const rows = filteredViolations.map(v => [
      v.violation_id,
      v.plate_number,
      v.speed_kmh.toFixed(2),
      v.speed_limit_kmh,
      v.overspeed_kmh.toFixed(2),
      v.timestamp_seconds.toFixed(2),
      v.severity,
      (v.plate_confidence * 100).toFixed(0) + '%',
      v.plate_validated ? 'Yes' : 'No'
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `violations_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div className="violations-container">
      <div className="violations-controls">
        <div className="filter-group">
          <label>Filter by severity:</label>
          <select 
            value={filterSeverity} 
            onChange={(e) => setFilterSeverity(e.target.value)}
            className="severity-filter"
          >
            <option value="all">All ({violations.length})</option>
            <option value="critical">Critical ({violations.filter(v => v.severity === 'critical').length})</option>
            <option value="high">High ({violations.filter(v => v.severity === 'high').length})</option>
            <option value="medium">Medium ({violations.filter(v => v.severity === 'medium').length})</option>
            <option value="low">Low ({violations.filter(v => v.severity === 'low').length})</option>
          </select>
        </div>

        <button onClick={exportToCSV} className="export-button">
          📥 Export CSV
        </button>
      </div>

      <div className="table-wrapper">
        <table className="violations-table">
          <thead>
            <tr>
              <th onClick={() => handleSort('severity')} className="sortable">
                Severity {sortBy === 'severity' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th>Plate</th>
              <th onClick={() => handleSort('speed')} className="sortable">
                Speed {sortBy === 'speed' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th>Limit</th>
              <th onClick={() => handleSort('overspeed')} className="sortable">
                Over {sortBy === 'overspeed' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('timestamp')} className="sortable">
                Time {sortBy === 'timestamp' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th onClick={() => handleSort('confidence')} className="sortable">
                Confidence {sortBy === 'confidence' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
            </tr>
          </thead>
          <tbody>
            {filteredViolations.map((violation) => (
              <tr 
                key={violation.violation_id} 
                className={getSeverityClass(violation.severity)}
              >
                <td className="severity-cell">
                  <span className="severity-badge">
                    {getSeverityIcon(violation.severity)}
                    <span className="severity-label">{violation.severity}</span>
                  </span>
                </td>
                <td className="plate-cell">
                  <span className="plate-number">{violation.plate_number}</span>
                  {!violation.plate_validated && (
                    <span className="validation-warning" title="Plate not validated">
                      ⚠️
                    </span>
                  )}
                </td>
                <td className="speed-cell">
                  <strong>{violation.speed_kmh.toFixed(1)}</strong> km/h
                </td>
                <td className="limit-cell">
                  {violation.speed_limit_kmh} km/h
                </td>
                <td className="overspeed-cell">
                  <span className="overspeed-value">
                    +{violation.overspeed_kmh.toFixed(1)}
                  </span>
                </td>
                <td className="timestamp-cell">
                  {violation.timestamp_seconds.toFixed(1)}s
                </td>
                <td className="confidence-cell">
                  <div className="confidence-bar-container">
                    <div 
                      className={`confidence-bar ${violation.plate_confidence < 0.7 ? 'low' : violation.plate_confidence < 0.85 ? 'medium' : 'high'}`}
                      style={{ width: `${violation.plate_confidence * 100}%` }}
                    />
                  </div>
                  <span className="confidence-text">
                    {(violation.plate_confidence * 100).toFixed(0)}%
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {filteredViolations.length === 0 && filterSeverity !== 'all' && (
        <div className="no-results">
          <p>No {filterSeverity} severity violations found</p>
        </div>
      )}
    </div>
  );
}