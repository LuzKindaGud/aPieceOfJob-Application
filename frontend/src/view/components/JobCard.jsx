import React from 'react';
import './style/JobCard.css';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationDot } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';

function JobCard({ job }) {
  const navigate = useNavigate();

  const formatDate = (dateString) => {
    const date = new Date(dateString);  // 2024-01-15
    const now = new Date();
    const diffTime = Math.abs(now - date);  // 1000 * 60 * 60 * 24 = 1 ngày
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));  // 1
    
    if (diffDays === 1) return '1 day ago';  // 1 ngày trước
    if (diffDays < 7) return `${diffDays} days ago`;  // 2 ngày trước
    if (diffDays < 30) return `${Math.ceil(diffDays / 7)} weeks ago`;  // 1 tuần trước
    return `${Math.ceil(diffDays / 30)} months ago`;  // 1 tháng trước
  };

  return (
    // job-card: class name của job card
    <div className={`job-card ${job.featured ? 'featured' : ''}`}> 
      {job.featured && <div className="featured-badge">Featured</div>}
      
      <div className="job-card-header">
        <div className="job-image">
          <img src={job.image} alt={job.title} />
        </div>
        <div className="job-basic-info">
          <h3 className="job-title">{job.title}</h3>
          <p className="job-company">{job.company}</p>
          <div className="job-meta">
            <span className="job-location"><FontAwesomeIcon icon={faLocationDot} /> {job.location}</span>
            <span className="job-type">{job.type}</span>
            <span className="job-experience">{job.experience}</span>
          </div>
        </div>
      </div>

      <div className="job-card-body">
        <p className="job-description">{job.description}</p>
        
        <div className="job-salary">
          <span className="salary-label">Salary:</span>
          <span className="salary-amount">{job.salary}</span>
        </div>

        <div className="job-tags">
          <span className="job-category">{job.category}</span>
          <span className="job-posted">Posted {formatDate(job.posted)}</span>
        </div>
      </div>

      <div className="job-card-footer">
        <button 
          className="apply-btn"
          onClick={() => navigate('/job-application', { state: { job } })}
        >
          Apply Now
        </button>
      </div>
    </div>
  );
}

export default JobCard;
