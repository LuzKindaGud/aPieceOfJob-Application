import React from 'react';
import './pages-style/contact.css';
import memberData from './data/member-data.json';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLinkedin, faGithub, faTwitter } from '@fortawesome/free-brands-svg-icons';

function Contact() {
  return (
    <div className="contact-container">
      <div className="contact-header">
        <h1>Our Team</h1>
        <p>Meet the amazing people behind this project.</p>
      </div>
      <div className="member-grid">
        {memberData.map(member => (
          <div key={member.id} className="member-card">
            <div className="member-image">
              <img src={member.imageUrl} alt={member.name} />
            </div>
            <div className="member-info">
              <h3>{member.name}</h3>
              <p>{member.role}</p>
              <div className="social-links">
                <a href={member.socialLinks.linkedin} aria-label="LinkedIn"><FontAwesomeIcon icon={faLinkedin} /></a>
                <a href={member.socialLinks.github} aria-label="GitHub"><FontAwesomeIcon icon={faGithub} /></a>
                <a href={member.socialLinks.twitter} aria-label="Twitter"><FontAwesomeIcon icon={faTwitter} /></a>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Contact;