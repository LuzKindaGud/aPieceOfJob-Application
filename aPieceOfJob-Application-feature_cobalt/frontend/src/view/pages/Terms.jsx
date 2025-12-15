import React from 'react';
import './pages-style/Terms.css';
import { Link } from 'react-router-dom';

function Terms() {
  return (
    <div className="terms-container">
      <div className="terms-header">
        <h1>Terms and Conditions</h1>
        <p>Last updated: October 18, 2025</p>
      </div>
      <div className="terms-content">
        <h2>1. Introduction</h2>
        <p>Welcome to aPieceOfJob! These Terms and Conditions set out the rules and regulations for using our website and services.</p>
        
        <h2>2. Acceptance of Terms</h2>
        <p>By accessing this website you accept these Terms and Conditions. Do not continue to use aPieceOfJob if you do not agree to all of the terms stated on this page.</p>
        
        <h2>3. User Accounts</h2>
        <p>When you create an account with us, you must provide accurate, complete and up-to-date information at all times. Failure to do so constitutes a breach of the Terms, which may result in immediate termination of your account on our Service.</p>
        
        <h2>4. Intellectual Property</h2>
        <p>The Service and its original content, features and functionality are and will remain the exclusive property of aPieceOfJob and its licensors. Our trademarks and trade dress may not be used in connection with any product or service without prior written consent.</p>
        
        <h2>5. Termination</h2>
        <p>We may terminate or suspend your account immediately, without prior notice or liability, for any reason, including without limitation if you breach the Terms.</p>
        
        <h2>6. Limitation of Liability</h2>
        <p>In no event shall aPieceOfJob, nor its directors, employees, partners, agents, suppliers, or affiliates, be liable for any indirect, incidental, special, consequential or punitive damages, including without limitation, loss of profits, data, use, goodwill, or other intangible losses, resulting from your access to or use of or inability to access or use the Service.</p>
        
        <h2>7. Disclaimer</h2>
        <p>Your use of the Service is at your sole risk. The Service is provided on an "AS IS" and "AS AVAILABLE" basis. The Service is provided without warranties of any kind, whether express or implied.</p>
        <p><strong>This is a sample document and does not constitute legal advice. Consult a legal professional to create terms tailored to your specific needs.</strong></p>

        <h2>8. Governing Law</h2>
        <p>These Terms shall be governed and construed in accordance with the laws of Vietnam, without regard to its conflict of law provisions.</p>
        
        <h2>9. Changes to Terms</h2>
        <p>We reserve the right, at our sole discretion, to modify or replace these Terms at any time. We will provide at least 30 days' notice prior to any new term taking effect. What constitutes a material change will be determined at our sole discretion.</p>
        
        <h2>10. Contact Us</h2>
        <p>If you have any questions about these Terms, please <Link to="/contact">contact us</Link>.</p>
      </div>
    </div>
  );
}

export default Terms;