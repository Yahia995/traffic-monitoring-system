const API_BASE = import.meta.env.VITE_API_BASE;

export async function uploadVideo(file) {
    const formData = new FormData();
    formData.append("video", file);

    const res = await fetch(`${API_BASE}/api/upload-video`, {
        method: "POST",
        body: formData
    });

    if(!res.ok)
        throw new Error(await res.text());

    return res.json()
}