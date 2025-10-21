import React from 'react';
import { Link } from 'react-router-dom';
import './pages-style/AuthForm.css';

function Login() {
  return (
    <div className="auth-container">
      <div className="auth-form-wrapper">
        <form className="auth-form">
          <h2>Welcome Back!</h2>
          <p>Please enter your details to sign in.</p>

          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input type="email" id="email" name="email" required />
          </div>

          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input type="password" id="password" name="password" required />
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

          <button type="submit" className="auth-button">Sign In</button>

          <p className="auth-switch">
            Don't have an account? <Link to="/register">Sign Up</Link>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Login;