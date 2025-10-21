// Import React và hook useState để quản lý state
import React, { useState } from 'react';
// Import hooks từ react-router-dom để điều hướng và lấy thông tin location
import { useLocation, useNavigate } from 'react-router-dom';
// Import CSS cho trang application
import './pages-style/JobApplication.css';
// Import FontAwesome icons
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faUpload, faCheckCircle } from '@fortawesome/free-solid-svg-icons';

// Component chính cho trang Job Application
function JobApplication() {
  // useLocation: lấy thông tin về location hiện tại (bao gồm state được truyền từ trang trước)
  const location = useLocation();
  // useNavigate: hook để điều hướng sang trang khác
  const navigate = useNavigate();
  // Lấy thông tin job từ state được truyền qua navigate (từ JobCard component)
  // job?.state?.job có thể là undefined nếu không có data được truyền
  const job = location.state?.job;

  // State để lưu trữ dữ liệu form
  // useState: hook để tạo state trong functional component
  const [formData, setFormData] = useState({
    fullName: '',       // Tên đầy đủ của ứng viên
    email: '',          // Email của ứng viên
    phone: '',          // Số điện thoại
    address: '',        // Địa chỉ (optional)
    coverLetter: '',    // Thư xin việc
    portfolio: '',      // Link portfolio hoặc LinkedIn (optional)
    experience: '',     // Số năm kinh nghiệm
    resume: null        // File CV (null ban đầu, sẽ chứa File object khi upload)
  });

  // State để theo dõi trạng thái submit của form (đã submit hay chưa)
  const [isSubmitted, setIsSubmitted] = useState(false);
  
  // State để lưu trữ các lỗi validation của form
  // Object với key là tên field và value là message lỗi
  const [errors, setErrors] = useState({});

  // Kiểm tra nếu không có thông tin job (user truy cập trực tiếp URL mà không click từ job card)
  // Redirect về trang /jobs để tránh lỗi
  if (!job) {
    navigate('/jobs');  // Điều hướng về trang jobs
    return null;        // Return null để không render gì cả
  }

  // Hàm xử lý khi user thay đổi giá trị của input fields
  const handleChange = (e) => {
    // Destructure để lấy name và value từ input element
    const { name, value } = e.target;
    
    // Cập nhật formData với giá trị mới
    // prev: giá trị state trước đó
    // ...prev: spread operator để giữ lại tất cả các field cũ
    // [name]: value: cập nhật field có name tương ứng với value mới
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Xóa error message của field này khi user bắt đầu nhập lại
    // Điều này giúp UX tốt hơn - không hiển thị lỗi khi user đang sửa
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  // Hàm xử lý khi user upload file CV
  const handleFileChange = (e) => {
    // e.target.files là một FileList, lấy file đầu tiên [0]
    const file = e.target.files[0];
    
    // Kiểm tra nếu có file được chọn
    if (file) {
      // Cập nhật formData với file object
      setFormData(prev => ({
        ...prev,
        resume: file  // Lưu toàn bộ File object (chứa name, size, type, etc.)
      }));
      
      // Xóa error message của resume field nếu có
      if (errors.resume) {
        setErrors(prev => ({ ...prev, resume: '' }));
      }
    }
  };

  // Hàm validate form trước khi submit
  // Return true nếu form hợp lệ, false nếu có lỗi
  const validateForm = () => {
    // Object để lưu các lỗi
    const newErrors = {};
    
    // Kiểm tra Full Name (required)
    // .trim() loại bỏ khoảng trắng đầu/cuối để tránh user nhập toàn space
    if (!formData.fullName.trim()) newErrors.fullName = 'Full name is required';
    
    // Kiểm tra Email (required + format)
    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      // Regex đơn giản để kiểm tra format email: something@something.something
      newErrors.email = 'Email is invalid';
    }
    
    // Kiểm tra Phone (required)
    if (!formData.phone.trim()) newErrors.phone = 'Phone number is required';
    
    // Kiểm tra Cover Letter (required)
    if (!formData.coverLetter.trim()) newErrors.coverLetter = 'Cover letter is required';
    
    // Kiểm tra Resume file (required)
    if (!formData.resume) newErrors.resume = 'Resume is required';

    // Cập nhật state errors với các lỗi vừa tìm được
    setErrors(newErrors);
    
    // Return true nếu không có lỗi (Object.keys(newErrors).length === 0)
    // Return false nếu có lỗi
    return Object.keys(newErrors).length === 0;
  };

  // Hàm xử lý khi user submit form
  const handleSubmit = (e) => {
    // Ngăn chặn hành vi mặc định của form (reload trang)
    e.preventDefault();
    
    // Validate form trước khi submit
    if (validateForm()) {
      // === Lưu application vào localStorage ===
      // localStorage.getItem: lấy dữ liệu từ localStorage
      // JSON.parse: chuyển string JSON thành object/array
      // || '[]': nếu không có data thì dùng array rỗng
      const applications = JSON.parse(localStorage.getItem('jobApplications') || '[]');
      
      // Thêm application mới vào mảng
      applications.push({
        jobId: job.id,               // ID của công việc
        jobTitle: job.title,         // Tên công việc
        company: job.company,        // Tên công ty
        applicantData: {             // Thông tin ứng viên
          ...formData,               // Spread tất cả các field từ formData
          resume: formData.resume.name  // Chỉ lưu tên file (không thể lưu File object vào localStorage)
        },
        appliedDate: new Date().toISOString()  // Thời gian apply (ISO format)
      });
      
      // Lưu mảng applications đã cập nhật vào localStorage
      // JSON.stringify: chuyển object/array thành string JSON
      localStorage.setItem('jobApplications', JSON.stringify(applications));

      // Đánh dấu form đã được submit thành công
      setIsSubmitted(true);

      // Sau 3 giây (3000ms) tự động chuyển về trang jobs
      setTimeout(() => {
        navigate('/jobs');
      }, 3000);
    }
  };

  // Nếu form đã được submit thành công, hiển thị màn hình success
  if (isSubmitted) {
    return (
      <div className="application-container">
        <div className="success-container">
          {/* Icon success với animation */}
          <div className="success-icon">
            <FontAwesomeIcon icon={faCheckCircle} />
          </div>
          {/* Thông báo thành công */}
          <h1>Application Submitted Successfully!</h1>
          <p>Thank you for applying to {job.title} at {job.company}</p>
          <p className="redirect-message">Redirecting to jobs page...</p>
        </div>
      </div>
    );
  }

  // === Render form application ===
  return (
    <div className="application-container">
      <div className="application-content">
        {/* Nút Back để quay lại trang jobs */}
        <button className="back-btn" onClick={() => navigate('/jobs')}>
          <FontAwesomeIcon icon={faArrowLeft} /> Back to Jobs
        </button>

        {/* Card hiển thị thông tin công việc đang apply */}
        <div className="job-info-card">
          <div className="job-info-header">
            {/* Logo công ty */}
            <img src={job.image} alt={job.company} className="company-logo" />
            <div className="job-info-details">
              {/* Tên công việc */}
              <h2>{job.title}</h2>
              {/* Tên công ty */}
              <p className="company-name">{job.company}</p>
              {/* Thông tin meta: location, type, salary */}
              <div className="job-info-meta">
                <span>{job.location}</span>
                <span>{job.type}</span>
                <span>{job.salary}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Container chứa form application */}
        <div className="application-form-container">
          <h1>Apply for this Position</h1>
          <p className="form-description">Fill out the form below to submit your application</p>

          {/* Form với onSubmit handler */}
          <form onSubmit={handleSubmit} className="application-form">
            {/* Form row: 2 fields nằm ngang (Full Name và Email) */}
            <div className="form-row">
              {/* Form group cho Full Name */}
              <div className="form-group">
                <label htmlFor="fullName">Full Name *</label>
                <input
                  type="text"
                  id="fullName"           // ID để liên kết với label
                  name="fullName"         // Name để identify field trong handleChange
                  value={formData.fullName}  // Controlled input: value từ state
                  onChange={handleChange}    // Handler khi value thay đổi
                  className={errors.fullName ? 'error' : ''}  // Thêm class 'error' nếu có lỗi
                  placeholder="John Doe"
                />
                {/* Hiển thị error message nếu có lỗi */}
                {errors.fullName && <span className="error-message">{errors.fullName}</span>}
              </div>

              {/* Form group cho Email */}
              <div className="form-group">
                <label htmlFor="email">Email Address *</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className={errors.email ? 'error' : ''}
                  placeholder="john.doe@example.com"
                />
                {errors.email && <span className="error-message">{errors.email}</span>}
              </div>
            </div>

            {/* Form row: Phone và Address */}
            <div className="form-row">
              {/* Form group cho Phone Number */}
              <div className="form-group">
                <label htmlFor="phone">Phone Number *</label>
                <input
                  type="tel"  // type="tel" để mobile hiển thị keyboard số
                  id="phone"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  className={errors.phone ? 'error' : ''}
                  placeholder="+1 (555) 123-4567"
                />
                {errors.phone && <span className="error-message">{errors.phone}</span>}
              </div>

              {/* Form group cho Address (optional - không có dấu *) */}
              <div className="form-group">
                <label htmlFor="address">Address</label>
                <input
                  type="text"
                  id="address"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="City, State, Country"
                />
                {/* Không có error validation vì field này optional */}
              </div>
            </div>

            {/* Form group cho Experience (dropdown select - optional) */}
            <div className="form-group">
              <label htmlFor="experience">Years of Experience</label>
              <select
                id="experience"
                name="experience"
                value={formData.experience}
                onChange={handleChange}  // handleChange hoạt động với cả select
              >
                {/* Option rỗng làm placeholder */}
                <option value="">Select experience level</option>
                <option value="0-1">0-1 years</option>
                <option value="1-3">1-3 years</option>
                <option value="3-5">3-5 years</option>
                <option value="5-10">5-10 years</option>
                <option value="10+">10+ years</option>
              </select>
            </div>

            {/* Form group cho Portfolio/LinkedIn URL (optional) */}
            <div className="form-group">
              <label htmlFor="portfolio">Portfolio/LinkedIn URL</label>
              <input
                type="url"  // type="url" để validate URL format
                id="portfolio"
                name="portfolio"
                value={formData.portfolio}
                onChange={handleChange}
                placeholder="https://your-portfolio.com"
              />
            </div>

            {/* Form group cho Cover Letter (textarea - required) */}
            <div className="form-group">
              <label htmlFor="coverLetter">Cover Letter *</label>
              <textarea
                id="coverLetter"
                name="coverLetter"
                value={formData.coverLetter}
                onChange={handleChange}
                className={errors.coverLetter ? 'error' : ''}
                placeholder="Tell us why you're a great fit for this position..."
                rows="6"  // Chiều cao mặc định 6 dòng (có thể resize)
              ></textarea>
              {errors.coverLetter && <span className="error-message">{errors.coverLetter}</span>}
            </div>

            {/* Form group cho Resume Upload (file input - required) */}
            <div className="form-group file-upload-group">
              <label htmlFor="resume">Upload Resume *</label>
              <div className="file-upload-wrapper">
                {/* Input file thật (ẩn đi bằng CSS) */}
                <input
                  type="file"
                  id="resume"
                  name="resume"
                  onChange={handleFileChange}  // Dùng handler riêng cho file
                  accept=".pdf,.doc,.docx"     // Chỉ accept các format này
                  className="file-input"       // CSS để ẩn input
                />
                {/* Label custom để làm button upload đẹp hơn */}
                <label htmlFor="resume" className={`file-upload-btn ${errors.resume ? 'error' : ''}`}>
                  <FontAwesomeIcon icon={faUpload} />
                  {/* Hiển thị tên file nếu đã chọn, nếu không thì "Choose File" */}
                  {formData.resume ? formData.resume.name : 'Choose File'}
                </label>
              </div>
              {errors.resume && <span className="error-message">{errors.resume}</span>}
              {/* Thông tin về format file được chấp nhận */}
              <small>Accepted formats: PDF, DOC, DOCX (Max 5MB)</small>
            </div>

            {/* Submit button */}
            <button type="submit" className="submit-btn">
              Submit Application
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

// Export component để sử dụng ở file khác
export default JobApplication;
