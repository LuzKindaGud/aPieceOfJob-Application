import { Link } from "react-router-dom";
import './style/footer.css';
import pswhitelogo from "/src/assets/image/Remove.png";

function Footer({style}) {
  return (
    <footer className="footer-container" style={style}>
      <div className="footer-content">
        <div className="footer-section about">
          <h2 className="footer-logo"><img src={pswhitelogo} alt="logo" />aPieceOfJob</h2>
          <p>
            We connect creative talents with job opportunities. Find your next piece of job with us.
          </p>
        </div>
        <div className="footer-section links">
          <h3>Quick Links</h3>
          <ul>
            <li><Link to="/about">About Us</Link></li>
            <li><Link to="/services">Services</Link></li>
            <li><Link to="/contact">Contact</Link></li>
            <li><Link to="/terms">Terms &amp; Conditions</Link></li>
          </ul>
        </div>
        <div className="footer-section social">
          <h3>Follow Us</h3>
          <div className="social-links">
            <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ" target="blank">Facebook</a>
            <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ" target="blank">Twitter</a>
            <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ" target="blank">Instagram</a>
            <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ" target="blank">LinkedIn</a>
          </div>
        </div>
      </div>
      <div className="footer-bottom">
        &copy; {new Date().getFullYear()} aPieceOfJob | All Rights Reserved
      </div>
    </footer>
  );
};

export default Footer;