import { useState } from "react";
import { uploadVideo } from "../api/backend";

export default function UploadForm({ onResult }) {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if(!file) return;

        setLoading(true);
        setError(null);

        try {
            const result = await uploadVideo(file);
            onResult(result);
        } catch(err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <input 
                type="file"
                accept="video/*"
                onChange={(e) => setFile(e.target.files[0])} 
            />
            <button disabled={loading}>
                {loading ? "Processing..." : "Upload"}
            </button>

            {error && <p className="error">{error}</p>}
        </form>
    );
}