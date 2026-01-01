const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';

// Auth
export async function register(email, password) {
  const res = await fetch(`${API_BASE}/api/v2/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password, role: 'USER' })
  });

  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: 'Registration failed' }));
    throw new Error(error.message || 'Registration failed');
  }

  return res.json();
}

export async function login(email, password) {
  const res = await fetch(`${API_BASE}/api/v2/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });

  if (!res.ok) {
    const error = await res.json().catch(() => ({ error: 'Login failed' }));
    throw new Error(error.error || 'Login failed');
  }

  return res.json();
}

// Videos
export async function getVideos(token, page = 0, size = 20) {
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}/api/v2/videos?page=${page}&size=${size}`, { headers });

  if (!res.ok) throw new Error('Failed to fetch videos');
  return res.json();
}

export async function getVideo(token, id) {
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}/api/v2/videos/${id}`, { headers });

  if (!res.ok) throw new Error('Failed to fetch video');
  return res.json();
}

export async function deleteVideo(token, id) {
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}/api/v2/videos/${id}`, {
    method: 'DELETE',
    headers
  });

  if (!res.ok) throw new Error('Failed to delete video');
}

// Violations
export async function filterViolations(token, filter) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const body = {
    severity: filter.severity || null,
    plateNumber: filter.plateNumber || null,
    startDate: filter.startDate || null,
    endDate: filter.endDate || null,
    validated: filter.validated === '' ? null : filter.validated === 'true',
    page: 0,
    size: 100
  };

  const res = await fetch(`${API_BASE}/api/v2/violations/filter`, {
    method: 'POST',
    headers,
    body: JSON.stringify(body)
  });

  if (!res.ok) throw new Error('Failed to filter violations');
  return res.json();
}

export async function getViolation(token, id) {
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}/api/v2/violations/${id}`, { headers });

  if (!res.ok) throw new Error('Failed to fetch violation');
  return res.json();
}

// Stats
export async function getStats(token) {
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}/api/v2/stats`, { headers });

  if (!res.ok) throw new Error('Failed to fetch stats');
  return res.json();
}

// Export
export async function exportViolationsCSV(token) {
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}/api/v2/violations/export/csv`, { headers });

  if (!res.ok) throw new Error('Failed to export');

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `violations_${Date.now()}.csv`;
  a.click();
  window.URL.revokeObjectURL(url);
}