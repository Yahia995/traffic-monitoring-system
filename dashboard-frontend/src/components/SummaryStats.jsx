export default function SummaryStats({ data }) {
  if (!data) return null;

  const { summary, video_info, processing_time_seconds, configuration } = data;

  const formatDuration = (seconds) => {
    if (seconds < 60) return `${seconds.toFixed(1)}s`;
    const minutes = Math.floor(seconds / 60);
    const secs = (seconds % 60).toFixed(0);
    return `${minutes}m ${secs}s`;
  };

  const detectionRate = summary.total_vehicles_tracked > 0
    ? ((summary.vehicles_with_plates / summary.total_vehicles_tracked) * 100).toFixed(1)
    : 0;

  const violationRate = summary.vehicles_with_plates > 0
    ? ((summary.violations_detected / summary.vehicles_with_plates) * 100).toFixed(1)
    : 0;

  const stats = [
    {
      icon: '🚗',
      label: 'Vehicles Tracked',
      value: summary.total_vehicles_tracked,
      color: 'blue'
    },
    {
      icon: '🔢',
      label: 'Plates Detected',
      value: summary.vehicles_with_plates,
      subtitle: `${detectionRate}% detection rate`,
      color: 'green'
    },
    {
      icon: '⚠️',
      label: 'Violations Found',
      value: summary.violations_detected,
      subtitle: `${violationRate}% violation rate`,
      color: summary.violations_detected > 0 ? 'red' : 'green'
    },
    {
      icon: '📊',
      label: 'Average Speed',
      value: `${summary.average_speed_kmh.toFixed(1)} km/h`,
      subtitle: `Limit: ${configuration.speed_limit_kmh} km/h`,
      color: summary.average_speed_kmh > configuration.speed_limit_kmh ? 'orange' : 'green'
    }
  ];

  const processingStats = [
    {
      label: 'Video Duration',
      value: formatDuration(video_info.duration_seconds)
    },
    {
      label: 'Processing Time',
      value: formatDuration(processing_time_seconds)
    },
    {
      label: 'Frame Rate',
      value: `${video_info.fps.toFixed(1)} fps`
    },
    {
      label: 'Frames Processed',
      value: `${video_info.processed_frames} / ${video_info.total_frames}`
    }
  ];

  return (
    <div className="summary-stats">
      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className={`stat-card stat-${stat.color}`}>
            <div className="stat-icon">{stat.icon}</div>
            <div className="stat-content">
              <div className="stat-value">{stat.value}</div>
              <div className="stat-label">{stat.label}</div>
              {stat.subtitle && (
                <div className="stat-subtitle">{stat.subtitle}</div>
              )}
            </div>
          </div>
        ))}
      </div>

      <div className="processing-info">
        <h3>Processing Details</h3>
        <div className="processing-grid">
          {processingStats.map((stat, index) => (
            <div key={index} className="processing-item">
              <span className="processing-label">{stat.label}:</span>
              <span className="processing-value">{stat.value}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}