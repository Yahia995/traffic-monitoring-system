import { useState, useEffect } from "react";
import { getStats, filterViolations, exportViolationsCSV } from "../../api/v2";

export default function Stats({ token }) {
  const [stats, setStats] = useState(null);
  const [violations, setViolations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState({
    severity: '',
    plateNumber: '',
    startDate: '',
    endDate: '',
    validated: ''
  });

  useEffect(() => {
    loadStats();
    loadViolations();
  }, [token]);

  const loadStats = async () => {
    try {
      const data = await getStats(token);
      setStats(data);
    } catch (err) {
      console.error('Failed to load stats:', err);
    }
  };

  const loadViolations = async () => {
    setLoading(true);
    try {
      const data = await filterViolations(token, filter);
      setViolations(data.data);
    } catch (err) {
      console.error('Failed to load violations:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    setFilter(prev => ({ ...prev, [key]: value }));
  };

  const handleSearch = () => {
    loadViolations();
  };

  const handleExport = async () => {
    try {
      await exportViolationsCSV(token);
    } catch (err) {
      alert('Export failed: ' + err.message);
    }
  };

  if (!stats) {
    return (
      <div className="stats-loading">
        <div className="spinner-large"></div>
        <p>Loading statistics...</p>
      </div>
    );
  }

  const severityColors = {
    LOW: '#10b981',
    MEDIUM: '#f59e0b',
    HIGH: '#ef4444',
    CRITICAL: '#dc2626'
  };

  const totalBySeverity = stats.bySeverity || {};
  const maxSeverity = Math.max(...Object.values(totalBySeverity), 1);

  return (
    <div className="stats-container">
      <div className="stats-header">
        <h2>Statistics & Analytics</h2>
        <p>Comprehensive violation analysis and trends</p>
      </div>

      {/* Overview Cards */}
      <div className="stats-overview">
        <div className="stats-card">
          <div className="stats-card-icon">⚠️</div>
          <div className="stats-card-content">
            <h3>{stats.total || 0}</h3>
            <p>Total Violations</p>
          </div>
        </div>

        <div className="stats-card">
          <div className="stats-card-icon">⚡</div>
          <div className="stats-card-content">
            <h3>{stats.averageSpeed?.toFixed(1) || '0.0'} km/h</h3>
            <p>Average Speed</p>
          </div>
        </div>

        <div className="stats-card">
          <div className="stats-card-icon">🔴</div>
          <div className="stats-card-content">
            <h3>{totalBySeverity.CRITICAL || 0}</h3>
            <p>Critical Violations</p>
          </div>
        </div>

        <div className="stats-card">
          <div className="stats-card-icon">🟠</div>
          <div className="stats-card-content">
            <h3>{totalBySeverity.HIGH || 0}</h3>
            <p>High Severity</p>
          </div>
        </div>
      </div>

      {/* Severity Chart */}
      <div className="chart-section">
        <h3>Violations by Severity</h3>
        <div className="bar-chart">
          {Object.entries(totalBySeverity).map(([severity, count]) => (
            <div key={severity} className="bar-item">
              <div className="bar-label">{severity}</div>
              <div className="bar-wrapper">
                <div
                  className="bar-fill"
                  style={{
                    width: `${(count / maxSeverity) * 100}%`,
                    backgroundColor: severityColors[severity]
                  }}
                >
                  <span className="bar-value">{count}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Filters */}
      <div className="filter-section">
        <h3>Filter Violations</h3>
        <div className="filter-grid">
          <div className="filter-item">
            <label>Severity</label>
            <select
              value={filter.severity}
              onChange={(e) => handleFilterChange('severity', e.target.value)}
            >
              <option value="">All</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          <div className="filter-item">
            <label>Plate Number</label>
            <input
              type="text"
              value={filter.plateNumber}
              onChange={(e) => handleFilterChange('plateNumber', e.target.value)}
              placeholder="Search plate..."
            />
          </div>

          <div className="filter-item">
            <label>Start Date</label>
            <input
              type="datetime-local"
              value={filter.startDate}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
            />
          </div>

          <div className="filter-item">
            <label>End Date</label>
            <input
              type="datetime-local"
              value={filter.endDate}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
            />
          </div>

          <div className="filter-item">
            <label>Validated</label>
            <select
              value={filter.validated}
              onChange={(e) => handleFilterChange('validated', e.target.value)}
            >
              <option value="">All</option>
              <option value="true">Validated</option>
              <option value="false">Not Validated</option>
            </select>
          </div>
        </div>

        <div className="filter-actions">
          <button onClick={handleSearch} className="filter-btn primary">
            🔍 Search
          </button>
          <button onClick={handleExport} className="filter-btn">
            📥 Export CSV
          </button>
        </div>
      </div>

      {/* Violations List */}
      <div className="violations-list">
        <h3>Recent Violations ({violations.length})</h3>
        {loading ? (
          <div className="loading-spinner">
            <div className="spinner"></div>
            <p>Loading violations...</p>
          </div>
        ) : violations.length === 0 ? (
          <div className="empty-state">
            <p>No violations match your filters</p>
          </div>
        ) : (
          <div className="violations-table-wrapper">
            <table className="violations-table-compact">
              <thead>
                <tr>
                  <th>Plate</th>
                  <th>Speed</th>
                  <th>Over</th>
                  <th>Severity</th>
                  <th>Date</th>
                  <th>Validated</th>
                </tr>
              </thead>
              <tbody>
                {violations.map((v) => (
                  <tr key={v.id}>
                    <td className="plate-cell">
                      <span className="plate-number-compact">{v.plateNumber}</span>
                    </td>
                    <td>{v.speedKmh.toFixed(1)} km/h</td>
                    <td className="overspeed-cell">+{v.overspeedKmh.toFixed(1)}</td>
                    <td>
                      <span
                        className="severity-badge-compact"
                        style={{ backgroundColor: severityColors[v.severity] }}
                      >
                        {v.severity}
                      </span>
                    </td>
                    <td className="date-cell">
                      {new Date(v.detectedAt).toLocaleDateString()}
                    </td>
                    <td className="validated-cell">
                      {v.plateValidated ? '✓' : '⚠️'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}