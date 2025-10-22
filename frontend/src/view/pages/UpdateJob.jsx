import React, { useState, useEffect } from 'react';
// 1. Bỏ useParams, thêm useLocation
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './pages-style/AddJob.css'; // Tái sử dụng CSS từ AddJob
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faUpload, faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';

function UpdateJob() {
  const location = useLocation(); // 2. Dùng useLocation để lấy state
  const navigate = useNavigate();

  // Lấy job object từ state được truyền qua. Nếu không có, job sẽ là undefined.
  const { job } = location.state || {};

  // State để lưu trữ dữ liệu form job
  const [formData, setFormData] = useState({
    title: '',
    company: '',
    location: '',
    salary: '',
    type: 'Full-time',
    category: '',
    experience: 'Mid-level',
    description: '',
    image: '',
    featured: false,
  });

  // State cho các trường động
  const [requirements, setRequirements] = useState(['']);
  const [benefits, setBenefits] = useState(['']);

  // State để theo dõi trạng thái
  // 3. Bỏ state 'loading' vì không còn fetch dữ liệu
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // State cho ảnh
  const [imageInputType, setImageInputType] = useState('url');
  const [imageFile, setImageFile] = useState(null);

  // 4. useEffect để điền dữ liệu từ 'job' object vào form
  useEffect(() => {
    // Nếu không có job object (ví dụ: người dùng vào thẳng URL), quay về trang jobs
    if (!job) {
      navigate('/jobs');
      return;
    }

    // Điền dữ liệu vào form
    setFormData({
      title: job.title || '',
      company: job.company || '',
      location: job.location || '',
      salary: job.salary || '',
      type: job.type || 'Full-time',
      category: job.category || '',
      experience: job.experience || 'Mid-level',
      description: job.description || '',
      image: job.image || '',
      featured: job.featured || false,
    });

    // Chuyển chuỗi requirements/benefits từ DB thành mảng
    setRequirements(job.requirements ? job.requirements.split(', ') : ['']);
    setBenefits(job.benefits ? job.benefits.split(', ') : ['']);

  }, [job, navigate]); // Chạy lại khi job hoặc navigate thay đổi

  // Các hàm handleChange, add/remove Requirement/Benefit, validateForm... giữ nguyên
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };
  const handleRequirementChange = (index, value) => {
    const newRequirements = [...requirements];
    newRequirements[index] = value;
    setRequirements(newRequirements);
  };
  const addRequirement = () => setRequirements([...requirements, '']);
  const removeRequirement = (index) => {
    if (requirements.length > 1) setRequirements(requirements.filter((_, i) => i !== index));
  };
  const handleBenefitChange = (index, value) => {
    const newBenefits = [...benefits];
    newBenefits[index] = value;
    setBenefits(newBenefits);
  };
  const addBenefit = () => setBenefits([...benefits, '']);
  const removeBenefit = (index) => {
    if (benefits.length > 1) setBenefits(benefits.filter((_, i) => i !== index));
  };
  const handleImageFileChange = (e) => { /* ...giữ nguyên... */ };
  const handleImageTypeChange = (type) => { /* ...giữ nguyên... */ };
  const validateForm = () => { /* ...giữ nguyên... */ return true; };


  // Hàm xử lý submit form (cập nhật)
  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');

    if (validateForm()) {
      setIsSubmitting(true);

      const filteredRequirements = requirements.filter(req => req.trim() !== '');
      const filteredBenefits = benefits.filter(ben => ben.trim() !== '');
      const requirementsString = filteredRequirements.join(', ');
      const benefitsString = filteredBenefits.join(', ');

      const jobData = {
        ...formData,
        requirements: requirementsString,
        benefits: benefitsString,
      };

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          setApiError('Authentication error. Please log in again.');
          setIsSubmitting(false);
          return;
        }

        // 5. Gửi yêu cầu PUT để cập nhật, dùng job.id từ state
        const response = await axios.put(
          `http://localhost:5000/api/jobs/${job.id}`,
          jobData,
          { headers: { 'Authorization': `Bearer ${token}` } }
        );

        if (response.data.success) {
          alert('Job updated successfully!');
          navigate('/jobs');
        }
      } catch (err) {
        const message = err.response?.data?.message || 'An unexpected error occurred.';
        setApiError(message);
      } finally {
        setIsSubmitting(false);
      }
    }
  };

  // 1. Thêm hàm xử lý xóa job
  const handleDelete = async () => {
    // Hiển thị hộp thoại xác nhận trước khi xóa
    if (window.confirm('Are you sure you want to delete this job? This action cannot be undone.')) {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          setApiError('Authentication error. Please log in again.');
          return;
        }

        // Gửi yêu cầu DELETE đến backend
        const response = await axios.delete(
          `http://localhost:5000/api/jobs/${job.id}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        );

        if (response.data.success) {
          alert('Job deleted successfully!');
          navigate('/jobs'); // Chuyển về trang danh sách jobs
        }
      } catch (err) {
        const message = err.response?.data?.message || 'An error occurred while deleting the job.';
        setApiError(message);
        console.error('Error deleting job:', err);
      }
    }
  };

  // 6. Nếu chưa có job data, không render gì cả (vì useEffect sẽ điều hướng đi)
  if (!job) {
    return null;
  }

  return (
    <div className="add-job-container">
      <div className="add-job-content">
        <button className="back-btn" onClick={() => navigate('/jobs')}>
          <FontAwesomeIcon icon={faArrowLeft} /> Back to Jobs
        </button>

        <div className="add-job-header">
          <h1>Update Job</h1>
          <p className="header-description">Edit the form below to update the job opening</p>
        </div>

        {/* Dán toàn bộ nội dung form từ AddJob.jsx vào đây */}
        <form onSubmit={handleSubmit} className="add-job-form">
          {apiError && (
            <div className="form-section">
              <p className="error-message api-error">{apiError}</p>
            </div>
          )}

          {/* Basic Information Section */}
          <div className="form-section">
            <h2 className="section-title">Basic Information</h2>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="title">Job Title *</label>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  className={errors.title ? 'error' : ''}
                  placeholder="e.g. Senior Frontend Developer"
                />
                {errors.title && <span className="error-message">{errors.title}</span>}
              </div>
              <div className="form-group">
                <label htmlFor="company">Company Name *</label>
                <input
                  type="text"
                  id="company"
                  name="company"
                  value={formData.company}
                  onChange={handleChange}
                  className={errors.company ? 'error' : ''}
                  placeholder="e.g. Google"
                />
                {errors.company && <span className="error-message">{errors.company}</span>}
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="location">Location *</label>
              <input
                type="text"
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                className={errors.location ? 'error' : ''}
                placeholder="e.g. Ho Chi Minh City, Vietnam"
              />
              {errors.location && <span className="error-message">{errors.location}</span>}
            </div>
            <div className="form-group">
              <label htmlFor="salary">Salary Range</label>
              <input
                type="text"
                id="salary"
                name="salary"
                value={formData.salary}
                onChange={handleChange}
                placeholder="e.g. $1,500 - $2,500"
              />
            </div>
            <div className="form-group featured-checkbox">
                <label className="checkbox-label">
                    <input type="checkbox" id="featured" name="featured" checked={formData.featured} onChange={handleChange} />
                    <span>Mark as Featured Job</span>
                </label>
            </div>
          </div>

          {/* Details Section */}
          <div className="form-section">
            <h2 className="section-title">Job Details</h2>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="type">Job Type</label>
                <select
                  id="type"
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                >
                  <option value="Full-time">Full-time</option>
                  <option value="Part-time">Part-time</option>
                  <option value="Contract">Contract</option>
                  <option value="Internship">Internship</option>
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="experience">Experience Level</label>
                <select
                  id="experience"
                  name="experience"
                  value={formData.experience}
                  onChange={handleChange}
                >
                  <option value="Internship">Internship</option>
                  <option value="Entry-level">Entry-level</option>
                  <option value="Mid-level">Mid-level</option>
                  <option value="Senior">Senior</option>
                  <option value="Lead">Lead</option>
                </select>
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="category">Category</label>
              <input
                type="text"
                id="category"
                name="category"
                value={formData.category}
                onChange={handleChange}
                className={errors.category ? 'error' : ''}
                placeholder="e.g. Software Development"
              />
              {errors.category && <span className="error-message">{errors.category}</span>}
            </div>
            <div className="form-group">
              <label htmlFor="description">Job Description</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="6"
                placeholder="Describe the role, responsibilities, and culture..."
              ></textarea>
            </div>
          </div>

          {/* Requirements Section */}
          <div className="form-section">
            <h2 className="section-title">Requirements</h2>
            <div className="dynamic-fields">
              {requirements.map((req, index) => (
                <div key={index} className="dynamic-field-group">
                  <input
                    type="text"
                    value={req}
                    onChange={(e) => handleRequirementChange(index, e.target.value)}
                    placeholder={`Requirement ${index + 1}`}
                    className="dynamic-input"
                  />
                  {requirements.length > 1 && (
                    <button type="button" onClick={() => removeRequirement(index)} className="remove-btn">
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                  )}
                </div>
              ))}
              <button type="button" onClick={addRequirement} className="add-field-btn">
                <FontAwesomeIcon icon={faPlus} /> Add Requirement
              </button>
            </div>
          </div>

          {/* Benefits Section */}
          <div className="form-section">
            <h2 className="section-title">Benefits</h2>
            <div className="dynamic-fields">
              {benefits.map((ben, index) => (
                <div key={index} className="dynamic-field-group">
                  <input
                    type="text"
                    value={ben}
                    onChange={(e) => handleBenefitChange(index, e.target.value)}
                    placeholder={`Benefit ${index + 1}`}
                    className="dynamic-input"
                  />
                  {benefits.length > 1 && (
                    <button type="button" onClick={() => removeBenefit(index)} className="remove-btn">
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                  )}
                </div>
              ))}
              <button type="button" onClick={addBenefit} className="add-field-btn">
                <FontAwesomeIcon icon={faPlus} /> Add Benefit
              </button>
            </div>
          </div>

          {/* Image and Featured Section */}
          <div className="form-section">
            <h2 className="section-title">Image & Visibility</h2>
            <div className="form-group">
              <label>Company Logo/Image</label>
              <div className="image-input-type-selector">
                <button type="button" className={imageInputType === 'url' ? 'active' : ''} onClick={() => handleImageTypeChange('url')}>Image URL</button>
                <button type="button" className={imageInputType === 'file' ? 'active' : ''} onClick={() => handleImageTypeChange('file')}>Upload Image</button>
              </div>
              {imageInputType === 'url' ? (
                <input type="text" name="image" value={formData.image} onChange={handleChange} placeholder="https://example.com/logo.png" />
              ) : (
                <div className="file-upload-wrapper">
                  <input type="file" id="imageFile" onChange={handleImageFileChange} accept="image/*" />
                  <label htmlFor="imageFile" className="file-upload-label"><FontAwesomeIcon icon={faUpload} /> Choose a file...</label>
                  {imageFile && <span className="file-name">{imageFile.name}</span>}
                </div>
              )}
            </div>
          </div>

          {/* Submit Buttons */}
          <div className="form-actions">
            <button type="button" className="cancellation-btn" onClick={() => navigate('/jobs')}>
              Cancel
            </button>
            {/* 2. Thêm nút Delete Job */}
            <button type="button" className="delete-btn" onClick={handleDelete}>
              Delete Job
            </button>
            <button type="submit" className="submit-btn" disabled={isSubmitting}>
              {isSubmitting ? 'Updating...' : 'Update Job'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default UpdateJob;