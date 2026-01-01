import { useState } from "react";
import { uploadVideo } from "../api/backend";

/**
 * UploadForm Component
 * 
 * Handles video file selection and upload with optional authentication (V2.0)
 * 
 * Props:
 * - onResult: Callback function when upload succeeds
 * - token: Optional JWT token for authenticated uploads (V2.0)
 */
export default function UploadForm({ onResult, token = null }) {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [progress, setProgress] = useState(0);
    const [isDragging, setIsDragging] = useState(false);

    const allowedFormats = ['.mp4', '.avi', '.mov', '.mkv'];
    const maxSizeMB = 200;

    const validateFile = (file) => {
        if (!file) {
            return "Please select a file";
        }

        const extension = '.' + file.name.split('.').pop().toLowerCase();
        if (!allowedFormats.includes(extension)) {
            return `Invalid format. Allowed: ${allowedFormats.join(', ')}`;
        }

        const sizeMB = file.size / (1024 * 1024);
        if (sizeMB > maxSizeMB) {
            return `File too large: ${sizeMB.toFixed(1)} MB (max: ${maxSizeMB} MB)`;
        }

        return null;
    };

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            const validationError = validateFile(selectedFile);
            if (validationError) {
                setError(validationError);
                setFile(null);
            } else {
                setFile(selectedFile);
                setError(null);
            }
        }
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setIsDragging(false);

        const droppedFile = e.dataTransfer.files[0];
        if (droppedFile) {
            const validationError = validateFile(droppedFile);
            if (validationError) {
                setError(validationError);
                setFile(null);
            } else {
                setFile(droppedFile);
                setError(null);
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        const validationError = validateFile(file);
        if (validationError) {
            setError(validationError);
            return;
        }

        setLoading(true);
        setError(null);
        setProgress(0);

        // Simulate progress (since we don't have real upload progress)
        const progressInterval = setInterval(() => {
            setProgress(prev => {
                if (prev >= 90) {
                    clearInterval(progressInterval);
                    return 90;
                }
                return prev + 10;
            });
        }, 1000);

        try {
            // Pass token for authenticated upload (V2.0)
            const result = await uploadVideo(file, token);
            setProgress(100);
            clearInterval(progressInterval);
            
            // Small delay to show 100% progress
            setTimeout(() => {
                onResult(result);
                setFile(null);
                setProgress(0);
            }, 500);
            
        } catch(err) {
            clearInterval(progressInterval);
            setError(err.message || "Upload failed. Please try again.");
            setProgress(0);
        } finally {
            setLoading(false);
        }
    };

    const formatFileSize = (bytes) => {
        const mb = bytes / (1024 * 1024);
        return mb.toFixed(2) + ' MB';
    };

    return (
        <div className="upload-form-container">
            <form onSubmit={handleSubmit}>
                <div 
                    className={`drop-zone ${isDragging ? 'dragging' : ''} ${file ? 'has-file' : ''}`}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                >
                    {!file ? (
                        <>
                            <div className="upload-icon">📹</div>
                            <p className="drop-text">
                                Drag & drop video here, or
                            </p>
                            <label className="file-input-label">
                                <input 
                                    type="file"
                                    accept="video/*"
                                    onChange={handleFileChange}
                                    disabled={loading}
                                    className="file-input-hidden"
                                />
                                <span className="browse-button">Browse Files</span>
                            </label>
                            <p className="format-hint">
                                Supported: {allowedFormats.join(', ')} • Max: {maxSizeMB} MB
                            </p>
                        </>
                    ) : (
                        <div className="file-preview">
                            <div className="file-icon">🎬</div>
                            <div className="file-info">
                                <p className="file-name">{file.name}</p>
                                <p className="file-size">{formatFileSize(file.size)}</p>
                            </div>
                            {!loading && (
                                <button 
                                    type="button"
                                    className="remove-file-btn"
                                    onClick={() => setFile(null)}
                                >
                                    ✕
                                </button>
                            )}
                        </div>
                    )}
                </div>

                {loading && (
                    <div className="progress-container">
                        <div className="progress-bar">
                            <div 
                                className="progress-fill" 
                                style={{ width: `${progress}%` }}
                            />
                        </div>
                        <p className="progress-text">
                            {progress < 90 ? 'Uploading...' : 'Processing video...'}
                            <span className="progress-percentage">{progress}%</span>
                        </p>
                    </div>
                )}

                <button 
                    type="submit"
                    className="upload-button"
                    disabled={!file || loading}
                >
                    {loading ? (
                        <>
                            <span className="spinner"></span>
                            Processing...
                        </>
                    ) : (
                        <>
                            <span>🚀</span>
                            Analyze Video
                        </>
                    )}
                </button>

                {error && (
                    <div className="error-message">
                        <span className="error-icon">⚠️</span>
                        <span>{error}</span>
                    </div>
                )}
            </form>
        </div>
    );
}