import React, { useState, useEffect } from "react";
import pswhitelogo from "/src/assets/image/Remove.png";
import pswhitenoname from "/src/assets/image/white-noname.png";
import "./style/navbar.css";

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser } from '@fortawesome/free-solid-svg-icons';

function Navbar() {
  const [scrolled, setScrolled] = useState(false);

  //
  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav className={scrolled ? 'nav scrolled' : 'nav'}>
      <div className="nav-content">
        <div className="nav-logo" onClick={() => window.location.reload()}>
          <div className="logo-wrapper">
            <img src={pswhitelogo} alt="logo" className="logo-base"/>
            <img src={pswhitenoname} alt="logo hover" className="logo-overlay"/>
          </div>
          <h2>aPieceOfJob</h2>
        </div>
        <div className="nav-links">
          <a href="#home"><span>Home</span></a>
          <a href="#about"><span>About</span></a>
          <a href="#services"><span>Services</span></a>
          <a href="#contact"><span>Contact</span></a>
          <a href="#profile"> <FontAwesomeIcon icon={faUser} className="nav-user-icon"/> </a>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;