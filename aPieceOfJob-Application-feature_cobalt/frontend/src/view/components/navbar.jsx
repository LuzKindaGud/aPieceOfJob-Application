import React, { useState, useEffect, useRef } from "react";
import pswhitelogo from "/src/assets/image/Remove.png";
import pswhitenoname from "/src/assets/image/white-noname.png";
import "./style/navbar.css";

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser, faBars, faTimes, faRightFromBracket, faFlag, faBriefcase } from '@fortawesome/free-solid-svg-icons';

import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  
  // State mới để quản lý đăng nhập và dialog
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  
  const navigate = useNavigate();
  const profileRef = useRef(null); // Ref để xử lý click bên ngoài

  // Effect để xử lý scroll và kiểm tra đăng nhập
  useEffect(() => {
    // Kiểm tra token và user data khi component tải
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setIsLoggedIn(true);
      setUser(JSON.parse(userData));
    }

    const handleScroll = () => setScrolled(window.scrollY > 10);
    
    // Xử lý click bên ngoài để đóng dialog
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setIsProfileOpen(false);
      }
    };

    window.addEventListener('scroll', handleScroll);
    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const toggleMobileMenu = () => setMobileMenuOpen(!mobileMenuOpen);
  const closeMobileMenu = () => setMobileMenuOpen(false);

  // Hàm xử lý đăng xuất
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setIsLoggedIn(false);
    setUser(null);
    setIsProfileOpen(false);
    closeMobileMenu();
    navigate('/login');
  };

  // Hàm bật/tắt dialog profile
  const toggleProfileDialog = () => {
    setIsProfileOpen(!isProfileOpen);
  };

  return (
    <nav className={scrolled ? 'nav scrolled' : 'nav'}>
      <div className="nav-content">
        {/* Logo section */}
        <div className="nav-logo" onClick={() => navigate('/')}>
          <div className="logo-wrapper">
            <img src={pswhitelogo} alt="logo" className="logo-base"/>
            <img src={pswhitenoname} alt="logo hover" className="logo-overlay"/>
          </div>
          <h2>aPieceOfJob</h2>
        </div>

        {/* Desktop nav links */}
        <div className="nav-links desktop-nav">
          <Link to="/" ><span>Home</span></Link>
          <Link to="/jobs"><span>Jobs</span></Link>
          <Link to="/about"><span>About</span></Link>
          <Link to="/contact"><span>Contact</span></Link>
          
          {/* Thay đổi icon user */}
          <div className="nav-user-section" ref={profileRef}>
            {isLoggedIn ? (
              <a onClick={toggleProfileDialog} style={{cursor: 'pointer'}}>
                <FontAwesomeIcon icon={faUser} className="nav-user-icon logged-in"/>
              </a>
            ) : (
              <Link to="/login">
                <FontAwesomeIcon icon={faFlag} className="nav-user-icon" />
              </Link>
            )}

            {/* Profile Dialog */}
            {isLoggedIn && isProfileOpen && (
              <div className="profile-dialog">
                <div className="profile-info">
                  <p className="profile-name">{user?.username || 'User'}</p>
                  <p className="profile-email">{user?.email}</p>
                  <p className="profile-role">{user?.role}</p>
                </div>
                {(user?.role === 'employer' || user?.role === 'admin') && (
                  <Link to="/employer-admin" className="profile-link" onClick={() => setIsProfileOpen(false)} style={{display: 'block', padding: '10px 15px', color: '#333', textDecoration: 'none', borderBottom: '1px solid #eee'}}>
                    <span style={{fontSize: '0.85rem', border: '1px solid #ccc', padding: '4px 8px', borderRadius: '4px'}}><FontAwesomeIcon icon={faBriefcase} style={{marginRight: '8px'}}/>Employer Dashboard</span>
                  </Link>
                )}
                <button onClick={handleLogout} className="profile-logout-btn">
                  <FontAwesomeIcon icon={faRightFromBracket} />
                  <span>Logout</span>
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Mobile menu button */}
        <button className="mobile-menu-btn" onClick={toggleMobileMenu}>
          <FontAwesomeIcon icon={mobileMenuOpen ? faTimes : faBars} />
        </button>
      </div>

      {/* Mobile menu panel */}
      <div className={`mobile-menu-panel ${mobileMenuOpen ? 'open' : ''}`}>
        <div className="mobile-menu-links">
          <Link to="/" onClick={closeMobileMenu}><span>Home</span></Link>
          <Link to="/jobs" onClick={closeMobileMenu}><span>Jobs</span></Link>
          <Link to="/about" onClick={closeMobileMenu}><span>About</span></Link>
          <Link to="/contact" onClick={closeMobileMenu}><span>Contact</span></Link>
          
          {isLoggedIn ? (
            <>
              <Link to="/profile" onClick={closeMobileMenu}>
                <FontAwesomeIcon icon={faUser} />
                <span>Profile</span>
              </Link>
              {(user?.role === 'employer' || user?.role === 'admin') && (
                <Link to="/employer-admin" onClick={closeMobileMenu}>
                  <span style={{fontSize: '0.85rem', border: '1px solid #ccc', borderRadius: '4px', padding: '4px 8px', backgroundColor: '#f8f9fa', color: '#d9534f'}}><FontAwesomeIcon icon={faBriefcase} style={{marginRight: '8px'}}/>Employer Dashboard</span>
                </Link>
              )}
              <button onClick={handleLogout} className="mobile-logout-btn">
                <FontAwesomeIcon icon={faRightFromBracket} />
                <span>Logout</span>
              </button>
            </>
          ) : (
            <Link to="/login" onClick={closeMobileMenu}>
              <FontAwesomeIcon icon={faUser} />
              <span>Login</span>
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;