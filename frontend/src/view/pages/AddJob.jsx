import React, { useState } from 'react';
import './pages-style/AddJob.css';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faUpload, faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';

function AddJob() {
  const navigate = useNavigate();

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

  // State để lưu trữ requirements và benefits (dạng array)
  const [requirements, setRequirements] = useState(['']);
  const [benefits, setBenefits] = useState(['']);

  // State để theo dõi trạng thái submit
  const [errors, setErrors] = useState({});

  // State để track image input type: 'url' hoặc 'file'
  const [imageInputType, setImageInputType] = useState('url');
  
  // State để lưu file ảnh
  const [imageFile, setImageFile] = useState(null);

  // Hàm xử lý khi thay đổi input fields
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));

    // Xóa error khi user bắt đầu nhập lại
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  // Hàm xử lý requirements
  const handleRequirementChange = (index, value) => {
    const newRequirements = [...requirements];
    newRequirements[index] = value;
    setRequirements(newRequirements);
  };

  const addRequirement = () => {
    setRequirements([...requirements, '']);
  };

  const removeRequirement = (index) => {
    if (requirements.length > 1) {
      const newRequirements = requirements.filter((_, i) => i !== index);
      setRequirements(newRequirements);
    }
  };

  // Hàm xử lý benefits
  const handleBenefitChange = (index, value) => {
    const newBenefits = [...benefits];
    newBenefits[index] = value;
    setBenefits(newBenefits);
  };

  const addBenefit = () => {
    setBenefits([...benefits, '']);
  };

  const removeBenefit = (index) => {
    if (benefits.length > 1) {
      const newBenefits = benefits.filter((_, i) => i !== index);
      setBenefits(newBenefits);
    }
  };

  // Hàm xử lý khi upload file ảnh
  const handleImageFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Kiểm tra file type
      if (!file.type.startsWith('image/')) {
        setErrors(prev => ({ ...prev, image: 'Please select a valid image file' }));
        return;
      }
      
      // Kiểm tra file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        setErrors(prev => ({ ...prev, image: 'Image size must be less than 5MB' }));
        return;
      }
      
      setImageFile(file);
      
      // Xóa error nếu có
      if (errors.image) {
        setErrors(prev => ({ ...prev, image: '' }));
      }
    }
  };

  // Hàm xử lý khi thay đổi image input type
  const handleImageTypeChange = (type) => {
    setImageInputType(type);
    // Reset errors và values
    setErrors(prev => ({ ...prev, image: '' }));
    if (type === 'url') {
      setImageFile(null);
    } else {
      setFormData(prev => ({ ...prev, image: '' }));
    }
  };

  // Validate form
  const validateForm = () => {
    const newErrors = {};

    if (!formData.title.trim()) newErrors.title = 'Job title is required';
    if (!formData.company.trim()) newErrors.company = 'Company name is required';
    if (!formData.location.trim()) newErrors.location = 'Location is required';
    if (!formData.salary.trim()) newErrors.salary = 'Salary range is required';
    if (!formData.category.trim()) newErrors.category = 'Category is required';
    if (!formData.description.trim()) newErrors.description = 'Job description is required';
    
    // Validate image based on input type
    if (imageInputType === 'url') {
      if (!formData.image.trim()) newErrors.image = 'Image URL is required';
    } else {
      if (!imageFile) newErrors.image = 'Please upload an image';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Hàm xử lý submit form
  const handleSubmit = (e) => {
    e.preventDefault();

    if (validateForm()) {
      // Lọc bỏ các requirements và benefits rỗng
      const filteredRequirements = requirements.filter(req => req.trim() !== '');
      const filteredBenefits = benefits.filter(ben => ben.trim() !== '');

      // Xử lý image dựa trên type
      let imageData;
      if (imageInputType === 'url') {
        imageData = formData.image;
      } else {
        // Trong thực tế, bạn sẽ upload file lên server và lấy URL
        // Hiện tại chỉ tạo URL tạm từ file
        imageData = imageFile ? URL.createObjectURL(imageFile) : '';
      }

      const jobData = {
        ...formData,
        image: imageData,
        imageFile: imageInputType === 'file' ? imageFile?.name : null, // Lưu tên file nếu là file upload
        requirements: filteredRequirements,
        benefits: filteredBenefits,
        posted: new Date().toISOString().split('T')[0], // Format: YYYY-MM-DD
        id: Date.now(), // Generate simple ID
      };

      console.log('Job Data:', jobData);
      console.log('Image Type:', imageInputType);
      if (imageFile) console.log('Image File:', imageFile);
      
      // Ở đây bạn có thể gọi API để lưu job data
      // Nếu là file, bạn cần dùng FormData để upload
      // const formDataToSend = new FormData();
      // formDataToSend.append('image', imageFile);
      // ... append other fields

      alert('Job posted successfully! (This is UI only)');
      // Có thể redirect về trang jobs
      // navigate('/jobs');
    }
  };

  return (
    <div className="add-job-container">
      <div className="add-job-content">
        {/* Back Button */}
        <button className="back-btn" onClick={() => navigate('/jobs')}>
          <FontAwesomeIcon icon={faArrowLeft} /> Back to Jobs
        </button>

        {/* Header */}
        <div className="add-job-header">
          <h1>Post a New Job</h1>
          <p className="header-description">Fill out the form below to post a new job opening</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="add-job-form">
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
                  placeholder="e.g. TechCorp Solutions"
                />
                {errors.company && <span className="error-message">{errors.company}</span>}
              </div>
            </div>

            <div className="form-row">
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
                <label htmlFor="salary">Salary Range *</label>
                <input
                  type="text"
                  id="salary"
                  name="salary"
                  value={formData.salary}
                  onChange={handleChange}
                  className={errors.salary ? 'error' : ''}
                  placeholder="e.g. $120,000 - $150,000"
                />
                {errors.salary && <span className="error-message">{errors.salary}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="type">Job Type *</label>
                <select
                  id="type"
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                >
                  <option value="Full-time">Full-time</option>
                  <option value="Part-time">Part-time</option>
                  <option value="Contract">Contract</option>
                  <option value="Freelance">Freelance</option>
                  <option value="Internship">Internship</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="experience">Experience Level *</label>
                <select
                  id="experience"
                  name="experience"
                  value={formData.experience}
                  onChange={handleChange}
                >
                  <option value="Entry-level">Entry-level</option>
                  <option value="Mid-level">Mid-level</option>
                  <option value="Senior">Senior</option>
                  <option value="Lead">Lead</option>
                  <option value="Executive">Executive</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="category">Category *</label>
              <input
                type="text"
                id="category"
                name="category"
                value={formData.category}
                onChange={handleChange}
                className={errors.category ? 'error' : ''}
                placeholder="e.g. Development, Design, Marketing"
              />
              {errors.category && <span className="error-message">{errors.category}</span>}
            </div>

            <div className="form-group">
              <label>Job Image *</label>
              
              {/* Radio buttons để chọn giữa URL và File */}
              <div className="image-type-selector">
                <label className="radio-label">
                  <input
                    type="radio"
                    name="imageType"
                    value="url"
                    checked={imageInputType === 'url'}
                    onChange={() => handleImageTypeChange('url')}
                  />
                  <span>Image URL</span>
                </label>
                <label className="radio-label">
                  <input
                    type="radio"
                    name="imageType"
                    value="file"
                    checked={imageInputType === 'file'}
                    onChange={() => handleImageTypeChange('file')}
                  />
                  <span>Upload Image</span>
                </label>
              </div>

              {/* Hiển thị input dựa trên image type */}
              {imageInputType === 'url' ? (
                <input
                  type="url"
                  id="image"
                  name="image"
                  value={formData.image}
                  onChange={handleChange}
                  className={errors.image ? 'error' : ''}
                  placeholder="https://example.com/image.jpg"
                />
              ) : (
                <div className="file-upload-wrapper">
                  <input
                    type="file"
                    id="imageFile"
                    accept="image/*"
                    onChange={handleImageFileChange}
                    className="file-input"
                  />
                  <label 
                    htmlFor="imageFile" 
                    className={`file-upload-btn ${errors.image ? 'error' : ''}`}
                  >
                    <FontAwesomeIcon icon={faUpload} />
                    {imageFile ? imageFile.name : 'Choose Image File'}
                  </label>
                  {imageFile && (
                    <div className="image-preview">
                      <img src={URL.createObjectURL(imageFile)} alt="Preview" />
                    </div>
                  )}
                </div>
              )}
              
              {errors.image && <span className="error-message">{errors.image}</span>}
              <small className="input-hint">
                {imageInputType === 'url' 
                  ? 'Enter a valid image URL' 
                  : 'Upload an image file (Max 5MB, JPG/PNG/GIF)'}
              </small>
            </div>

            <div className="form-group checkbox-group">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  name="featured"
                  checked={formData.featured}
                  onChange={handleChange}
                />
                <span>Mark as Featured Job</span>
              </label>
            </div>
          </div>

          {/* Description Section */}
          <div className="form-section">
            <h2 className="section-title">Job Description</h2>
            
            <div className="form-group">
              <label htmlFor="description">Description *</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                className={errors.description ? 'error' : ''}
                placeholder="Describe the job role, responsibilities, and what you're looking for..."
                rows="6"
              ></textarea>
              {errors.description && <span className="error-message">{errors.description}</span>}
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
                    <button
                      type="button"
                      onClick={() => removeRequirement(index)}
                      className="remove-btn"
                    >
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                  )}
                </div>
              ))}
              <button
                type="button"
                onClick={addRequirement}
                className="add-field-btn"
              >
                <FontAwesomeIcon icon={faPlus} /> Add Requirement
              </button>
            </div>
          </div>

          {/* Benefits Section */}
          <div className="form-section">
            <h2 className="section-title">Benefits</h2>
            
            <div className="dynamic-fields">
              {benefits.map((benefit, index) => (
                <div key={index} className="dynamic-field-group">
                  <input
                    type="text"
                    value={benefit}
                    onChange={(e) => handleBenefitChange(index, e.target.value)}
                    placeholder={`Benefit ${index + 1}`}
                    className="dynamic-input"
                  />
                  {benefits.length > 1 && (
                    <button
                      type="button"
                      onClick={() => removeBenefit(index)}
                      className="remove-btn"
                    >
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                  )}
                </div>
              ))}
              <button
                type="button"
                onClick={addBenefit}
                className="add-field-btn"
              >
                <FontAwesomeIcon icon={faPlus} /> Add Benefit
              </button>
            </div>
          </div>

          {/* Submit Buttons */}
          <div className="form-actions">
            <button type="button" className="cancellation-btn" onClick={() => navigate('/jobs')}>
              Cancel
            </button>
            <button type="submit" className="submit-btn">
              Post Job
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddJob;

