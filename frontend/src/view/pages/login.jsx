import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios'; // 1. Import axios
import './pages-style/AuthForm.css';

function Login() {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState(''); // State để lưu thông báo lỗi
  const [isSubmitting, setIsSubmitting] = useState(false); // State để quản lý trạng thái gửi
  const navigate = useNavigate(); // Hook để điều hướng

  // 2. Hàm xử lý khi người dùng nhập liệu
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  // 3. Hàm xử lý khi submit form
  const handleSubmit = async (e) => {
    e.preventDefault(); // Ngăn form reload trang
    setError(''); // Reset lỗi
    setIsSubmitting(true);

    try {
      // Gửi yêu cầu POST đến backend
      const response = await axios.post('http://localhost:5000/api/auth/login', formData);

      // Nếu backend trả về token
      if (response.data.token) {
        // 4. Lưu token vào localStorage để sử dụng cho các yêu cầu sau
        localStorage.setItem('token', response.data.token);
        
        // Tùy chọn: lưu thông tin người dùng nếu có
        if (response.data.user) {
          localStorage.setItem('user', JSON.stringify(response.data.user));
        }

        // 5. Chuyển hướng đến trang jobs và reload lại trang
        window.location.href = '/jobs';
      }
    } catch (err) {
      // Hiển thị lỗi trả về từ backend
      const message = err.response?.data?.message || 'Login failed. Please check your credentials.';
      setError(message);
    } finally {
      setIsSubmitting(false); // Kích hoạt lại nút submit
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-form-wrapper">
        {/* 6. Gắn hàm handleSubmit vào sự kiện onSubmit của form */}
        <form className="auth-form" onSubmit={handleSubmit}>
          <h2>Welcome Back!</h2>
          <p>Please enter your details to sign in.</p>

          {/* Hiển thị lỗi nếu có */}
          {error && <p className="error-message">{error}</p>}

          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input 
              type="email" 
              id="email" 
              name="email" 
              required 
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input 
              type="password" 
              id="password" 
              name="password" 
              required 
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          <div className="auth-options">
            <label className="remember">
              <input type="checkbox" id="remember" name="remember" />
              <span>Remember me</span>
            </label>
            <Link to="/forgot-password" className="forgot-link">
              Forgot password?
            </Link>
          </div>

          <button type="submit" className="auth-button" disabled={isSubmitting}>
            {isSubmitting ? 'Signing In...' : 'Sign In'}
          </button>

          <p className="auth-switch">
            Don't have an account? <Link to="/register">Sign Up</Link>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Login;