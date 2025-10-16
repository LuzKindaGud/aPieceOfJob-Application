import React from 'react';
import './pages-style/home.css';
function Home() {
  return (
    <div className="home-container">
        <div className="home-content">
            <h1>Welcome to aPieceOfJob</h1>
            <p>We got job for you, let's get started</p>
            <button className="cta-button">Get Started</button>
        </div>
    </div>
);
}

export default Home;