import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './pages-style/AuthForm.css';

const VerifyOTP = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [otp, setOTP] = useState('');
  const [message, setMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [countdown, setCountdown] = useState(0);

  useEffect(() => {
    if (location.state?.email) {
      setEmail(location.state.email);
    } else {
      navigate('/register');
    }
  }, [location.state, navigate]);

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  const handleVerify = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setMessage("");
    try {
      const res = await axios.post('http://localhost:5000/api/auth/verify-otp', { email, otp });

      if (res.data.success) {
        alert(res.data.message);
        navigate('/login', { state: { email: email } });
      }

    } catch (err) {
      setMessage(err.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleResendOTP = async () => {
    if (countdown > 0) return;
    try {
      const res = await axios.post('http://localhost:5000/api/auth/resend-otp', { email });
      if (res.data.success) {
        setMessage('OTP đã được gửi lại. Vui lòng kiểm tra email của bạn.');
        setCountdown(60);
      }
    } catch (err) {
      setMessage(err.response?.data?.message || 'Có lỗi xảy ra khi gửi lại OTP');
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-form-wrapper">
        <div className="auth-form">
          <h2>Xác thực tài khoản</h2>
          <p>
            Mã OTP đã được gửi tới: <strong>{email}</strong>
          </p>

          <form onSubmit={handleVerify}>
            <div className="input-group">
              <label>Nhập mã OTP</label>
              <input
                type="text"
                value={otp}
                onChange={e => setOTP(e.target.value)}
                required
                placeholder="Nhập 6 số"
              />
            </div>

            {/* Thông báo lỗi/thành công */}
            {message && (
              <p style={{
                color: message.includes('thành công') || message.includes('gửi lại') ? '#4caf50' : '#ff4d4d',
                fontSize: '0.9rem',
                marginBottom: 10
              }}>
                {message}
              </p>
            )}

            <button
              className="auth-button"
              type="submit"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Đang kiểm tra...' : 'Xác thực'}
            </button>
          </form>

          {/* Khu vực gửi lại OTP */}
          <div className="auth-switch">
            <p>Bạn chưa nhận được mã?</p>
            <button
              type="button"
              onClick={handleResendOTP}
              disabled={countdown > 0}
              style={{
                background: 'none',
                border: 'none',
                color: countdown > 0 ? '#999' : '#e85d41',
                textDecoration: 'underline',
                cursor: countdown > 0 ? 'default' : 'pointer',
                fontWeight: 'bold',
                padding: 0,
                fontSize: 'inherit'
              }}
            >
              {countdown > 0 ? `Gửi lại sau ${countdown}s` : 'Gửi lại mã'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VerifyOTP;