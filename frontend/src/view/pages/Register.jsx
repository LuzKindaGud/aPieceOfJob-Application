import React from 'react';
import { Link } from 'react-router-dom';
import './pages-style/AuthForm.css';

function Register() {
  return (
    <div className="auth-container">
      <div className="auth-form-wrapper">
        <form className="auth-form">
          <h2>Create Account</h2>
          <p>Join us by creating a new account.</p>
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