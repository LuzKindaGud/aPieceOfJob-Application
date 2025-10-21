// Cấu hình base URL
const API_BASE_URL = 'http://localhost:5000/api';

// Helper function để gọi API
const apiCall = async (endpoint, options = {}) => {
    const token = localStorage.getItem('token');
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    // Thêm token nếu có
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Something went wrong');
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
};

// ============ AUTH APIs ============

export const register = async (userData) => {
    return apiCall('/auth/register', {
        method: 'POST',
        body: JSON.stringify(userData)
    });
};

export const login = async (credentials) => {
    return apiCall('/auth/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
    });
};

export const getProfile = async () => {
    return apiCall('/auth/profile', {
        method: 'GET'
    });
};

// ============ JOB APIs ============

export const getJobList = async () => {
    return apiCall('/jobs', {
        method: 'GET'
    });
};

export const getJobDetails = async (jobId) => {
    return apiCall(`/jobs/${jobId}`, {
        method: 'GET'
    });
};

export const createJob = async (jobData) => {
    return apiCall('/jobs', {
        method: 'POST',
        body: JSON.stringify(jobData)
    });
};

export const updateJob = async (jobId, jobData) => {
    return apiCall(`/jobs/${jobId}`, {
        method: 'PUT',
        body: JSON.stringify(jobData)
    });
};

export const deleteJob = async (jobId) => {
    return apiCall(`/jobs/${jobId}`, {
        method: 'DELETE'
    });
};

// ============ AUTH HELPERS ============

export const saveToken = (token) => {
    localStorage.setItem('token', token);
};

export const getToken = () => {
    return localStorage.getItem('token');
};

export const removeToken = () => {
    localStorage.removeItem('token');
};

export const isAuthenticated = () => {
    return !!getToken();
};