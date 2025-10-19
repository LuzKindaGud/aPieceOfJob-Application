import { useState, useEffect } from 'react';
import Lenis from '@studio-freight/lenis';

// Components
import Navbar from './components/navbar.jsx';
import Intro from './components/Intro.jsx';
import Footer from './components/footer.jsx';
import Infbar from './components/infbar.jsx';

// Pages
import Home from './pages/home.jsx';
import Commerce from './pages/commerce.jsx'; 
import About from './pages/benefits.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import Jobs from './pages/Jobs.jsx';
import JobApplication from './pages/JobApplication.jsx';
import ForgotPassword from './pages/ForgotPassword.jsx';
import CTA from './pages/cta.jsx';
import Contact from './pages/contact.jsx';
import Terms from './pages/Terms.jsx';
import AlterJobs from './pages/Alter-jobs.jsx';

import { Route, Routes, useLocation } from "react-router-dom";

function App() {
  const [showIntro, setShowIntro] = useState(true);
  const location = useLocation();

  const handleIntroComplete = () => {
    document.body.classList.add('intro-finished');
    setTimeout(() => {
      setShowIntro(false);
    }, 0);
  };

  useEffect(() => {
    // Cuộn cửa sổ về vị trí (0, 0) mỗi khi `location.pathname` thay đổi
    window.scrollTo(0, 0);
  }, [location.pathname]);
  
  useEffect(() => {
    // Khởi tạo Lenis để làm scroll mượt
    const lenis = new Lenis({
      duration: 1.5, 
      // Thời gian tham chiếu cho "smoothing" (giây).
      // Giá trị nhỏ hơn -> cảm giác cuộn nhanh hơn; lớn hơn -> chậm/mượt hơn.

      easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
      /* Hàm easing:
         - Nhận t ∈ [0,1] (progress của animation).
         - Math.pow(2, -10 * t) = 2^(−10t) là exponential decay:
           ở t=0 => 2^0 = 1; khi t → 1 thì 2^(−10) → ~0.00098.
         - 1.001 - 2^(−10t) dịch cả hàm lên một chút (1.001) để bù lỗi số học nhỏ.
         - Math.min(1, ...) clamp kết quả về tối đa 1 (đảm bảo không vượt quá 1).
         - Kết quả: tăng nhanh ở đầu (fast start) rồi chậm dần về cuối (smooth ease‑out),
           cho cảm giác "momentum" tự nhiên khi cuộn.
      */

      smooth: true,
      // Bật smoothing nội bộ của Lenis (cho phép Lenis xử lý vị trí cuộn).

      direction: 'vertical',
      // Hướng cuộn chính (có thể 'horizontal' nếu cần).

      gestureDirection: 'vertical'
      // Hướng nhận gesture từ touchpad / touch; giữ cùng hướng với `direction`.
    });

    // Vòng lặp RAF để cập nhật Lenis mỗi frame.
    // Lưu ý: cần hủy requestAnimationFrame khi component unmount để tránh leak.
    let rafId;
    function raf(time) {
      // lenis.raf chạy cập nhật nội bộ (vị trí, easing, velocity...)
      lenis.raf(time);
      // Lặp tiếp
      rafId = requestAnimationFrame(raf);
    }
    // Bắt đầu vòng lặp
    rafId = requestAnimationFrame(raf);

    // Cleanup: hủy RAF và dọn Lenis
    return () => {
      // Hủy vòng lặp RAF nếu còn
      if (rafId) cancelAnimationFrame(rafId);
      // Dọn nội bộ lenis (sự kiện, listeners...)
      lenis.destroy();
    };
  }, []);

  return (
    <div className="app-wrapper">
      <div className="page-transition-overlay" key={location.pathname}></div>
      {showIntro && <Intro onComplete={handleIntroComplete} />}
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route
            path="/"
            element={
              <>
                <Home />
                <Infbar />
                <About />
                <Commerce />
                <AlterJobs />
                <CTA />
              </>
            }
          />
          <Route path="/about" element={<About />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/jobs" element={<Jobs />} />
          <Route path="/job-application" element={<JobApplication />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/terms" element={<Terms />} />
        </Routes>
        <Footer style={{ zIndex: 0}}/>
      </main>
    </div>
  );
}

export default App;