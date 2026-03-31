import { useState, useEffect } from "react";
import { getVideos, deleteVideo } from "../../api/v2";

const VideoIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="2" y="3" width="20" height="14" rx="2"/>
    <path d="M8 21h8M12 17v4"/>
  </svg>
);

const TrashIcon = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="3 6 5 6 21 6"/>
    <path d="M19 6l-1 14H6L5 6"/>
    <path d="M10 11v6M14 11v6"/>
    <path d="M9 6V4h6v2"/>
  </svg>
);

export default function History({ token }) {
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState(null);

  const loadVideos = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getVideos(token, page, 10);
      setVideos(result.data);
      setTotalPages(result.totalPages);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadVideos(); }, [page, token]);

  const handleDelete = async (id) => {
    if (!confirm('Delete this video and all associated data?')) return;
    try {
      await deleteVideo(token, id);
      loadVideos();
    } catch (err) {
      alert('Failed to delete: ' + err.message);
    }
  };

  const formatDate = (dateStr) => new Date(dateStr).toLocaleString();

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading && videos.length === 0) {
    return (
      <div className="history-loading">
        <div className="spinner-large"></div>
        <p>Loading history…</p>
      </div>
    );
  }

  return (
    <div className="history-container">
      <div className="history-header">
        <h2>Video History</h2>
        <p>View and manage previously analyzed videos</p>
      </div>

      {error && (
        <div className="error-message">
          <span className="error-icon">&#9888;</span>
          <span>{error}</span>
        </div>
      )}

      {videos.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon"><VideoIcon /></div>
          <h3>No videos yet</h3>
          <p>Upload a video to get started</p>
        </div>
      ) : (
        <>
          <div className="video-grid">
            {videos.map((video) => (
              <div key={video.id} className="video-card">
                <div className="video-card-header">
                  <div className="video-icon"><VideoIcon /></div>
                  <div className="video-info">
                    <h3 className="video-title">{video.filename}</h3>
                    <p className="video-date">{formatDate(video.uploadedAt)}</p>
                  </div>
                  <button
                    className="delete-btn"
                    onClick={() => handleDelete(video.id)}
                    title="Delete video"
                  >
                    <TrashIcon />
                  </button>
                </div>

                <div className="video-stats">
                  <div className="video-stat">
                    <span className="stat-label">Duration</span>
                    <span className="stat-value">{formatDuration(video.durationSeconds)}</span>
                  </div>
                  <div className="video-stat">
                    <span className="stat-label">FPS</span>
                    <span className="stat-value">{video.fps.toFixed(1)}</span>
                  </div>
                  <div className="video-stat">
                    <span className="stat-label">Vehicles</span>
                    <span className="stat-value">{video.summary.totalVehicles}</span>
                  </div>
                  <div className="video-stat">
                    <span className="stat-label">Violations</span>
                    <span className={`stat-value ${video.summary.violations > 0 ? 'violations-found' : ''}`}>
                      {video.summary.violations}
                    </span>
                  </div>
                </div>

                <div className="video-summary">
                  <div className="summary-item">
                    <span className="summary-icon">&#x2015;</span>
                    <span>{video.summary.vehiclesWithPlates} plates detected</span>
                  </div>
                  <div className="summary-item">
                    <span className="summary-icon">&#x2015;</span>
                    <span>Avg speed: {video.summary.averageSpeed.toFixed(1)} km/h</span>
                  </div>
                </div>

                <div className="video-status">
                  <span className={`status-badge status-${video.status.toLowerCase()}`}>
                    {video.status}
                  </span>
                  <span className={`status-badge status-${video.aiStatus.toLowerCase()}`}>
                    AI: {video.aiStatus}
                  </span>
                </div>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="pagination-btn"
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0 || loading}
              >
                &larr; Previous
              </button>
              <span className="pagination-info">
                Page {page + 1} of {totalPages}
              </span>
              <button
                className="pagination-btn"
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1 || loading}
              >
                Next &rarr;
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
