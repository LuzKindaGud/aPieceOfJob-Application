import React from 'react';
import { Link } from 'react-router-dom';
import './pages-style/AuthForm.css';

function ForgotPassword() {
  return (
    <div className="auth-container">
      <div className="auth-form-wrapper">
        <form className="auth-form">
          <h2>Forgot Password?</h2>
          <p>No worries, we'll send you reset instructions.</p>
          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="Enter your email"
              required
            />
          </div>
          <button type="submit" className="auth-button">
            Reset Password
          </button>
          <p className="auth-switch">
            <Link to="/login" className="back-to-login">
              &larr; Back to Sign In
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}

export default ForgotPassword;