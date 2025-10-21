import React from 'react';
import './pages-style/home.css';
import homepagevideo from '/src/assets/video/home-pages.mp4';
import { Link } from 'react-router-dom';
function Home({style}) {
  return (
    <div className="home-container" style={style}>
        <video className="background-video" autoPlay loop muted>
            <source src={homepagevideo} type="video/mp4" />
        </video>
        <div className="home-content">
            <h1>Welcome to aPieceOfJob</h1>
            <p>We got job for you, let's get started</p>
            <Link to="/register" className="cta-button-link">
                <button className="cta-button">Get Started</button>
            </Link>
        </div>
    </div>
);
}

export default Home;