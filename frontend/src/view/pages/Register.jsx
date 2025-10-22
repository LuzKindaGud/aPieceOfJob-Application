import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios'; // 1. Import axios
import './pages-style/AuthForm.css';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faBriefcase } from '@fortawesome/free-solid-svg-icons';

function Register() {
  // 2. State để lưu dữ liệu form
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
  });
  // State để lưu role, giá trị 'user' và 'employer' khớp với backend
  const [selectedRole, setSelectedRole] = useState('user'); 
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  // 3. Hàm xử lý thay đổi input
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  // 4. Hàm xử lý khi submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsSubmitting(true);

    // Dữ liệu để gửi đi, bao gồm cả role
    const registrationData = {
      ...formData,
      role: selectedRole,
    };

    try {
      const response = await axios.post('http://localhost:5000/api/auth/register', registrationData);

      if (response.data.success) {
        alert('Registration successful! Please log in.');
        navigate('/login'); // Chuyển hướng đến trang đăng nhập
      }
    } catch (err) {
      const message = err.response?.data?.message || 'Registration failed. Please try again.';
      setError(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-form-wrapper register-form">
        {/* 5. Gắn hàm handleSubmit */}
        <form className="auth-form" onSubmit={handleSubmit}>
          <h2>Create Account</h2>
          <p>Join us by creating a new account.</p>

          {/* Hiển thị lỗi nếu có */}
          {error && <p className="error-message">{error}</p>}
          
          {/* Role Selection */}
          <div className="input-group">
            <label>Choose Your Role</label>
            <div className="role-selection">
              {/* 1. Thêm id="role-user" và htmlFor="role-user" */}
              <label htmlFor="role-user" className={`role-option ${selectedRole === 'user' ? 'active' : ''}`}>
                <input
                  type="radio"
                  id="role-user"
                  name="role"
                  value="user"
                  checked={selectedRole === 'user'}
                  onChange={(e) => setSelectedRole(e.target.value)}
                />
                <div className="role-content">
                  <span className="role-icon"><FontAwesomeIcon icon={faUser} /></span>
                  <span className="role-title">Job Seeker</span>
                  <span className="role-description">Search and apply for jobs</span>
                </div>
              </label>

              {/* 2. Thêm id="role-employer" và htmlFor="role-employer" */}
              <label htmlFor="role-employer" className={`role-option ${selectedRole === 'employer' ? 'active' : ''}`}>
                <input
                  type="radio"
                  id="role-employer"
                  name="role"
                  value="employer"
                  checked={selectedRole === 'employer'}
                  onChange={(e) => setSelectedRole(e.target.value)}
                />
                <div className="role-content">
                  <span className="role-icon"><FontAwesomeIcon icon={faBriefcase} /></span>
                  <span className="role-title">Employer</span>
                  <span className="role-description">Post jobs and hire talent</span>
                </div>
              </label>
            </div>
          </div>

          {/* 6. Gắn value và onChange cho các input */}
          <div className="input-group">
            <label htmlFor="username">Username</label>
            <input 
              type="text" 
              id="username" 
              name="username" 
              required 
              value={formData.username}
              onChange={handleChange}
            />
          </div>
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
          <button type="submit" className="auth-button" disabled={isSubmitting}>
            {isSubmitting ? 'Signing Up...' : 'Sign Up'}
          </button>
          <p className="auth-switch">
            Already have an account? <Link to="/login">Sign In</Link>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Register;