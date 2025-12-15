module.exports = {
    validateRegistration: (data) => {
        const errors = {};
        if (!data.username || data.username.trim() === '') {
            errors.username = 'Username is required';
        }
        if (!data.password || data.password.length < 6) {
            errors.password = 'Password must be at least 6 characters long';
        }
        if (data.password !== data.confirmPassword) {
            errors.confirmPassword = 'Passwords do not match';
        }
        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    },

    validateLogin: (data) => {
        const errors = {};
        if (!data.username || data.username.trim() === '') {
            errors.username = 'Username is required';
        }
        if (!data.password) {
            errors.password = 'Password is required';
        }
        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    }
};