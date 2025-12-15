import React from 'react';
import './style/JobCard.css';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLocationDot } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';

// Component hiển thị thẻ công việc (Job Card)
function JobCard({ job }) {
  const navigate = useNavigate();

  // 1. Lấy thông tin người dùng từ localStorage
  const userData = localStorage.getItem('user');
  const user = userData ? JSON.parse(userData) : null;

  // 2. Kiểm tra xem người dùng đã đăng nhập có phải là chủ của công việc này không
  // Thêm điều kiện kiểm tra user.role === 'admin' nếu bạn muốn admin cũng có thể sửa
  const isOwner = user && (user.id === job.employer_id || user.role === 'admin');

  // 3. Hàm format ngày tháng thành dạng relative time (vd: "2 days ago")
  const formatDate = (dateString) => {
    const date = new Date(dateString);  // Chuyển string thành Date object
    const now = new Date(); // Lấy thời gian hiện tại
    const diffTime = Math.abs(now - date);  // Tính khoảng cách thời gian (milliseconds)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));  // Chuyển thành số ngày
    
    // Hiển thị theo format khác nhau dựa trên khoảng thời gian
    if (diffDays === 1) return '1 day ago';  // 1 ngày trước
    if (diffDays < 7) return `${diffDays} days ago`;  // 2-6 ngày trước
    if (diffDays < 30) return `${Math.ceil(diffDays / 7)} weeks ago`;  // 1-4 tuần trước
    return `${Math.ceil(diffDays / 30)} months ago`;  // 1+ tháng trước
  };

  return (
    // Thẻ job card - thêm class 'featured' nếu công việc được đánh dấu nổi bật
    <div className={`job-card ${job.featured ? 'featured' : ''}`}> 
      {/* Badge "Featured" hiển thị nếu công việc là featured */}
      {job.featured ? <div className="featured-badge">Featured</div> : null}
      
      {/* Header của card - bao gồm hình ảnh và thông tin cơ bản */}
      <div className="job-card-header">
        {/* Hình ảnh công ty/công việc */}
        <div className="job-image">
          <img src={job.image} alt={job.title} />
        </div>
        
        {/* Thông tin cơ bản: tên công việc, công ty, metadata */}
        <div className="job-basic-info">
          <h3 className="job-title">{job.title}</h3>
          <p className="job-company">{job.company}</p>
          
          {/* Metadata: địa điểm, loại công việc, mức kinh nghiệm */}
          <div className="job-meta">
            <span className="job-location">
              <FontAwesomeIcon icon={faLocationDot} /> {job.location}
            </span>
            <span className="job-type">{job.type}</span>
            <span className="job-experience">{job.experience}</span>
          </div>
        </div>
      </div>

      {/* Body của card - mô tả, lương, tags */}
      <div className="job-card-body">
        {/* Mô tả ngắn về công việc */}
        <p className="job-description">{job.description}</p>
        
        {/* Thông tin về lương */}
        <div className="job-salary">
          <span className="salary-label">Salary:</span>
          <span className="salary-amount">{job.salary}</span>
        </div>

        {/* Tags: danh mục và thời gian đăng */}
        <div className="job-tags">
          <span className="job-category">{job.category}</span>
          <span className="job-posted">Posted {formatDate(job.created_at)}</span>
        </div>
      </div>

      {/* Footer của card - chứa các nút hành động */}
      <div className="job-card-footer">
        {/* Nút Apply - chuyển đến trang ứng tuyển */}
        <button 
          className="apply-btn"
          onClick={() => navigate('/job-application', { state: { job } })}
        >
          Apply Now
        </button>
        
        {/* Nút Edit - chỉ hiển thị nếu người dùng là chủ công việc hoặc admin */}
        {isOwner && (
          <button 
            className="edit-btn"
            onClick={() => navigate(`/update-job/${job.id}`, { state: { job } })}
          >
            Edit
          </button>
        )}
      </div>
    </div>
  );
}

export default JobCard;
