// API base URL - relative since frontend is served by the same Spring Boot server
const API_BASE = '/api';

// Auth helpers
const Auth = {
    getToken: () => localStorage.getItem('token'),
    getUser: () => JSON.parse(localStorage.getItem('user') || '{}'),
    getRole: () => localStorage.getItem('role'),
    isLoggedIn: () => !!localStorage.getItem('token'),
    save: (data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('role', data.role);
        localStorage.setItem('user', JSON.stringify({ name: data.name, userId: data.userId }));
    },
    logout: () => {
        localStorage.clear();
        window.location.href = '/index.html';
    }
};

// HTTP helpers
async function apiGet(url) {
    const res = await fetch(API_BASE + url, {
        headers: { 'Authorization': 'Bearer ' + Auth.getToken() }
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

async function apiDelete(url) {
    const res = await fetch(API_BASE + url, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + Auth.getToken() }
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.error || 'Delete failed');
    }
    return res.json();
}

async function apiPost(url, data, auth = true) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth) headers['Authorization'] = 'Bearer ' + Auth.getToken();
    const res = await fetch(API_BASE + url, {
        method: 'POST',
        headers,
        body: JSON.stringify(data)
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.error || 'Request failed');
    }
    return res.json();
}

// Guard: redirect if not logged in or wrong role
function requireAuth(role) {
    if (!Auth.isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }
    if (role && Auth.getRole() !== role) {
        window.location.href = '/index.html';
    }
}

// Show toast notification
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
    toast.style.cssText = 'min-width:280px;';
    toast.innerHTML = `${message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

function createToastContainer() {
    const div = document.createElement('div');
    div.id = 'toast-container';
    div.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;';
    document.body.appendChild(div);
    return div;
}
