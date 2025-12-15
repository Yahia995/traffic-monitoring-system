const API_BASE = "http://localhost:8080";

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