const API_BASE_URL = '/api/v1';

export const getAuthToken = () => sessionStorage.getItem('token');

export const isAuthenticated = () => {
    const token = getAuthToken();
    return !!token;
};

export const isAdmin = () => {
    const role = sessionStorage.getItem('role');
    return role === 'ADMIN';
};

export const isUser = () => {
    const role = sessionStorage.getItem('role');
    return role === 'USER';
};

export const logout = () => {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('role');
    window.location.href = '/login';
};

export const apiFetch = async (url, options = {}) => {
    const token = getAuthToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` }),
        ...options.headers,
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            ...options,
            headers,
        });

        if (response.status === 401) {
            logout();
            throw new Error('Unauthorized - Please login again');
        }

        return response;
    } catch (error) {
        if (error.message.includes('Unauthorized')) {
            logout();
        }
        throw error;
    }
};

export const apiRequest = async (url, options = {}) => {
    const response = await apiFetch(url, options);

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        if (response.status === 400 && errorData.violations) {
            throw {
                status: response.status,
                violations: errorData.violations,
                message: errorData.error || 'Validation failed'
            };
        }
        throw {
            status: response.status,
            message: errorData.message || errorData.error || 'Request failed'
        };
    }

    if (response.status === 204) return null;
    return response.json();
};

export const apiGet = (url) => apiRequest(url);
export const apiPost = (url, data) => apiRequest(url, {
    method: 'POST',
    body: JSON.stringify(data),
});
export const apiPut = (url, data) => apiRequest(url, {
    method: 'PUT',
    body: JSON.stringify(data),
});
export const apiDelete = (url) => apiRequest(url, {
    method: 'DELETE',
});