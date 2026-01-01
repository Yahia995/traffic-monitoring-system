const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';

/**
 * Upload video for full analysis
 * @param {File} file - Video file to upload
 * @param {string} token - Optional JWT token for authenticated uploads (V2.0)
 * @returns {Promise} AI response with full analysis
 */
export async function uploadVideo(file, token = null) {
    const formData = new FormData();
    formData.append("video", file);

    const headers = {};
    
    // Add authentication header if token is provided (V2.0)
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const res = await fetch(`${API_BASE}/api/upload-video`, {
            method: "POST",
            headers: headers,
            body: formData
        });

        if (!res.ok) {
            // Try to parse error response
            let errorMessage = `Upload failed: ${res.status} ${res.statusText}`;
            
            try {
                const errorData = await res.json();
                errorMessage = errorData.message || errorData.code || errorMessage;
            } catch (e) {
                // If JSON parsing fails, try to get text
                try {
                    const errorText = await res.text();
                    if (errorText) {
                        errorMessage = errorText;
                    }
                } catch (e2) {
                    // Use default error message
                }
            }

            throw new Error(errorMessage);
        }

        return await res.json();
    } catch (error) {
        if (error.message) {
            throw error;
        }
        throw new Error('Network error. Please check your connection and try again.');
    }
}

/**
 * Upload video for summary only (faster response)
 * @param {File} file - Video file to upload
 * @param {string} token - Optional JWT token for authenticated uploads (V2.0)
 * @returns {Promise} Summary response
 */
export async function uploadVideoSummary(file, token = null) {
    const formData = new FormData();
    formData.append("video", file);

    const headers = {};
    
    // Add authentication header if token is provided (V2.0)
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const res = await fetch(`${API_BASE}/api/upload-video/summary`, {
            method: "POST",
            headers: headers,
            body: formData
        });

        if (!res.ok) {
            let errorMessage = `Upload failed: ${res.status} ${res.statusText}`;
            
            try {
                const errorData = await res.json();
                errorMessage = errorData.message || errorData.code || errorMessage;
            } catch (e) {
                // Use default error message
            }

            throw new Error(errorMessage);
        }

        return await res.json();
    } catch (error) {
        if (error.message) {
            throw error;
        }
        throw new Error('Network error. Please check your connection and try again.');
    }
}

/**
 * Check backend health
 * @returns {Promise} Health status
 */
export async function checkHealth() {
    try {
        const res = await fetch(`${API_BASE}/health`);
        return await res.json();
    } catch (error) {
        throw new Error('Cannot reach backend service');
    }
}

/**
 * Check detailed health (including AI service)
 * @returns {Promise} Detailed health status
 */
export async function checkDetailedHealth() {
    try {
        const res = await fetch(`${API_BASE}/health/detailed`);
        return await res.json();
    } catch (error) {
        throw new Error('Cannot reach backend service');
    }
}