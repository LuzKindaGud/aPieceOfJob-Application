import React, { useState, useEffect  } from 'react';
import { useLocation } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

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
    }
    else { 
      navigate('/register');
    }
  }, [location.state , navigate]);

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  const handleVerify = async (e) => {
    e.preventDefault();
    setIsSubmitting(true)
    setMessage("");
    try {
      const res = await axios.post('http://localhost:5000/api/auth/verify-otp', { email, otp });

      if (res.data.success) {
        alert(res.data.message);
        navigate('/login', { state: { email: email } });
      }
      
    }
    catch (err) {
      setMessage(err.response?.data?.message || 'Có lỗi xảy ra');
    }
    finally { 
      setIsSubmitting(false)
    }
  };

  const handleResendOTP = async () => {
    if (countdown > 0) return; // Chặn spam countdown khi đang đếm ngược
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
    <div style={{ maxWidth: 400, margin: '40px auto', padding: 24, border: '1px solid #ccc', borderRadius: 8 }}>
      <h2 style={{textAlign: 'center'}}>Xác thực tài khoản</h2>
      <p style={{textAlign: 'center', fontSize: '0.9rem', color: '#666'}}>
        Mã OTP đã được gửi tới: <strong>{email}</strong>
      </p>

      <form onSubmit={handleVerify}>
        <div style={{ marginBottom: 15 }}>
          <label> Thằng nào Gay sẽ nhập mã vào đây:</label>
          <input
            type="text"
            value={otp}
            onChange={e => setOTP(e.target.value)}
            required
            placeholder="Nhập 6 số"
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>

        {/* Thông báo lỗi/thành công */}
        {message && (
            <p style={{ 
                color: message.includes('thành công') || message.includes('gửi lại') ? 'green' : 'red', 
                fontSize: '0.9rem', 
                marginBottom: 10 
            }}>
                {message}
            </p>
        )}

        <button 
            type="submit" 
            disabled={isSubmitting}
            style={{ 
                width: '100%', 
                padding: '10px', 
                backgroundColor: '#007bff', 
                color: 'white', 
                border: 'none', 
                borderRadius: '4px',
                cursor: isSubmitting ? 'not-allowed' : 'pointer'
            }}
        >
            {isSubmitting ? 'Đang kiểm tra...' : 'Xác thực'}
        </button>
      </form>

      {/* Khu vực gửi lại OTP */}
      <div style={{ marginTop: 20, textAlign: 'center' }}>
        <p style={{ fontSize: '0.9rem' }}>Bạn chưa nhận được mã?</p>
        <button 
            type="button"
            onClick={handleResendOTP}
            disabled={countdown > 0}
            style={{ 
                background: 'none', 
                border: 'none', 
                color: countdown > 0 ? '#999' : '#007bff', 
                textDecoration: 'underline', 
                cursor: countdown > 0 ? 'default' : 'pointer' 
            }}
        >
            {countdown > 0 ? `Gửi lại sau ${countdown}s` : 'Thằng nào Gay sẽ gửi lại mã đây'}
        </button>
      </div>
    </div>
  );
};

export default VerifyOTP;