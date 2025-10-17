import React, { useState, useEffect } from "react";
import pswhitelogo from "/src/assets/image/Remove.png";
import pswhitenoname from "/src/assets/image/white-noname.png";
import "./style/navbar.css";

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser } from '@fortawesome/free-solid-svg-icons';

import { Link } from "react-router-dom";

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
          <Link to="/" ><span>Home</span></Link>
          <Link to="/jobs"><span>Jobs</span></Link>
          <Link to="/about"><span>About</span></Link>
          <Link to="/contact"><span>Contact</span></Link>
          <Link to="/login"> <FontAwesomeIcon icon={faUser} className="nav-user-icon"/> </Link>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;