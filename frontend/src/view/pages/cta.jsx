import React from 'react';
import './pages-style/cta.css';
import { Link } from 'react-router-dom';

function DualCTA() {
  return (
    <div className="cta-container">
      <div className="cta-content">
        <h2>Ready to Get Started?</h2>
        <p className="cta-description">Join thousands of businesses and freelancers making things happen on aPieceOfJob.</p>
        <div className="ctaa-buttons">
          <Link to="/register" className="ctaa-button" id="primary-cta">
            I Want to Hire
          </Link>
          <Link to="/register" className="ctaa-button" id="secondary-cta">
            I Want to Find Work
          </Link>
        </div>
      </div>
    </div>
  );
}

export default DualCTA;