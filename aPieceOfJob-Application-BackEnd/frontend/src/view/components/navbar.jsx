import React, { useState, useEffect } from "react";
import pswhitelogo from "/src/assets/image/Remove.png";
import pswhitenoname from "/src/assets/image/white-noname.png";
import "./style/navbar.css";

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser, faBars, faTimes } from '@fortawesome/free-solid-svg-icons';

import { Link } from "react-router-dom";

function Navbar() {
  // State để theo dõi khi scroll
  const [scrolled, setScrolled] = useState(false);
  // State để mở/đóng mobile menu
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  // Effect để xử lý scroll event
  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Hàm toggle mobile menu
  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  // Hàm đóng mobile menu khi click vào link
  const closeMobileMenu = () => {
    setMobileMenuOpen(false);
  };

  return (
    <nav className={scrolled ? 'nav scrolled' : 'nav'}>
      <div className="nav-content">
        {/* Logo section */}
        <div className="nav-logo" onClick={() => window.location.reload()}>
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
          <Link to="/login"> <FontAwesomeIcon icon={faUser} className="nav-user-icon"/> </Link>
        </div>

        {/* Mobile menu button */}
        <button className="mobile-menu-btn" onClick={toggleMobileMenu}>
          <FontAwesomeIcon icon={mobileMenuOpen ? faTimes : faBars} />
        </button>
      </div>

      {/* Mobile menu panel */}
      <div className={`mobile-menu-panel ${mobileMenuOpen ? 'open' : ''}`}>
        <div className="mobile-menu-links">
          <Link to="/" onClick={closeMobileMenu}>
            <span>Home</span>
          </Link>
          <Link to="/jobs" onClick={closeMobileMenu}>
            <span>Jobs</span>
          </Link>
          <Link to="/about" onClick={closeMobileMenu}>
            <span>About</span>
          </Link>
          <Link to="/contact" onClick={closeMobileMenu}>
            <span>Contact</span>
          </Link>
          <Link to="/login" onClick={closeMobileMenu}>
            <FontAwesomeIcon icon={faUser} className="nav-user-icon"/>
            <span>Login</span>
          </Link>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;