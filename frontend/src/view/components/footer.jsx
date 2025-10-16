
import './style/footer.css';

function Footer({style}) {
  return (
    <footer className="footer-container" style={style}>
      <div className="footer-content">
        <div className="footer-section about">
          <h2 className="footer-logo">aPieceOfJob</h2>
          <p>
            We connect creative talents with job opportunities. Find your next piece of job with us.
          </p>
        </div>
        <div className="footer-section links">
          <h3>Quick Links</h3>
          <ul>
            <li><a href="#about">About Us</a></li>
            <li><a href="#services">Services</a></li>
            <li><a href="#contact">Contact</a></li>
            <li><a href="#">Terms & Conditions</a></li>
          </ul>
        </div>
        <div className="footer-section social">
          <h3>Follow Us</h3>
          <div className="social-links">
            <a href="#">Facebook</a>
            <a href="#">Twitter</a>
            <a href="#">Instagram</a>
            <a href="#">LinkedIn</a>
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