import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './pages-style/AuthForm.css';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { faUser } from '@fortawesome/free-solid-svg-icons';
import { faBriefcase } from '@fortawesome/free-solid-svg-icons';

function Register() {
  // State để lưu role được chọn
  const [selectedRole, setSelectedRole] = useState('job-seeker');

  // Hàm xử lý khi submit form
  const handleSubmit = (e) => {
    e.preventDefault();
    // Xử lý logic đăng ký ở đây
    console.log('Selected role:', selectedRole);
  };

  return (
    <div className="auth-container">
      <div className="auth-form-wrapper register-form">
        <form className="auth-form" onSubmit={handleSubmit}>
          <h2>Create Account</h2>
          <p>Join us by creating a new account.</p>
          
          {/* Role Selection */}
          <div className="input-group">
            <label>Choose Your Role</label>
            <div className="role-selection">
              <label className={`role-option ${selectedRole === 'job-seeker' ? 'active' : ''}`}>
                <input
                  type="radio"
                  name="role"
                  value="job-seeker"
                  checked={selectedRole === 'job-seeker'}
                  onChange={(e) => setSelectedRole(e.target.value)}
                />
                <div className="role-content">
                  <span className="role-icon"><FontAwesomeIcon icon={faUser} /></span>
                  <span className="role-title">Job Seeker</span>
                  <span className="role-description">Search and apply for jobs</span>
                </div>
              </label>

              <label className={`role-option ${selectedRole === 'employer' ? 'active' : ''}`}>
                <input
                  type="radio"
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

          <div className="input-group">
            <label htmlFor="username">Username</label>
            <input type="text" id="username" name="username" required />
          </div>
          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input type="email" id="email" name="email" required />
          </div>
          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input type="password" id="password" name="password" required />
          </div>
          <button type="submit" className="auth-button">Sign Up</button>
          <p className="auth-switch">
            Already have an account? <Link to="/login">Sign In</Link>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Register;