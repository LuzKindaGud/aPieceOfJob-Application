import React, { useState, useEffect } from 'react';
import axios from 'axios'; // 1. Import axios
import './pages-style/Jobs.css';
// import jobsData from './data/jobs.json'; // 2. Xóa data tĩnh
import JobCard from '../components/JobCard.jsx';

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faPlus } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';

function Jobs() {
  // 3. Khởi tạo state để lưu jobs từ API, trạng thái loading và error
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // 4. State cho chức năng tìm kiếm và lọc
  const [searchTerm, setSearchTerm] = useState(''); // Từ khóa tìm kiếm
  const [selectedCategory, setSelectedCategory] = useState(''); // Danh mục đã chọn
  const [selectedLocation, setSelectedLocation] = useState(''); // Địa điểm đã chọn
  const [selectedType, setSelectedType] = useState(''); // Loại công việc đã chọn
  const [selectedExperience, setSelectedExperience] = useState(''); // Mức kinh nghiệm đã chọn
  const [sortBy, setSortBy] = useState('newest'); // Tiêu chí sắp xếp
  const [showFilters, setShowFilters] = useState(false); // Hiển thị bộ lọc trên mobile

  // 5. Dùng useEffect để gọi API với axios khi component được render
  useEffect(() => {
    const fetchJobs = async () => {
      try {
        // Gọi API bằng axios. Dữ liệu trả về nằm trong response.data
        const response = await axios.get('http://localhost:5000/api/jobs');
        setJobs(response.data.jobs); // Backend trả về { success, count, jobs }
      } catch (err) {
        setError(err.message);
        console.error("Error fetching jobs:", err);
      } finally {
        setLoading(false); // Dừng loading dù thành công hay thất bại
      }
    };

    fetchJobs();
  }, []); // Mảng rỗng `[]` đảm bảo useEffect chỉ chạy 1 lần

  // 6. Lấy giá trị unique cho các filter options từ danh sách jobs
  const categories = [...new Set(jobs.map(job => job.category).filter(Boolean))];
  const locations = [...new Set(jobs.map(job => job.location).filter(Boolean))];
  const types = [...new Set(jobs.map(job => job.type).filter(Boolean))];
  const experiences = [...new Set(jobs.map(job => job.experience).filter(Boolean))];

  // 7. Hàm xử lý khi người dùng chọn/bỏ chọn một filter
  const handleFilterChange = (filterType, value) => {
    // Nếu filter đã được chọn thì bỏ chọn (toggle), ngược lại thì chọn
    switch(filterType) {
      case 'category':
        setSelectedCategory(selectedCategory === value ? '' : value);
        break;
      case 'location':
        setSelectedLocation(selectedLocation === value ? '' : value);
        break;
      case 'type':
        setSelectedType(selectedType === value ? '' : value);
        break;
      case 'experience':
        setSelectedExperience(selectedExperience === value ? '' : value);
        break;
      default:
        break;
    }
  };

  // 8. Hàm xóa tất cả các filter đã chọn
  const clearFilters = () => {
    setSearchTerm('');
    setSelectedCategory('');
    setSelectedLocation('');
    setSelectedType('');
    setSelectedExperience('');
  };

  // 9. Hàm đếm số lượng filter đang được áp dụng
  const getActiveFiltersCount = () => {
    let count = 0;
    if (searchTerm) count++;
    if (selectedCategory) count++;
    if (selectedLocation) count++;
    if (selectedType) count++;
    if (selectedExperience) count++;
    return count;
  };

  // 10. Lọc jobs dựa trên các filter đã chọn
  const filteredJobs = jobs.filter(job => {
    // Kiểm tra tìm kiếm theo từ khóa (tìm trong title, company, description)
    const matchesSearch = !searchTerm || 
      job.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      job.company.toLowerCase().includes(searchTerm.toLowerCase()) ||
      job.description.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Kiểm tra lọc theo danh mục
    const matchesCategory = !selectedCategory || job.category === selectedCategory;
    
    // Kiểm tra lọc theo địa điểm
    const matchesLocation = !selectedLocation || job.location === selectedLocation;
    
    // Kiểm tra lọc theo loại công việc
    const matchesType = !selectedType || job.type === selectedType;
    
    // Kiểm tra lọc theo mức kinh nghiệm
    const matchesExperience = !selectedExperience || job.experience === selectedExperience;
    
    // Chỉ trả về job nếu thỏa mãn TẤT CẢ các điều kiện
    return matchesSearch && matchesCategory && matchesLocation && matchesType && matchesExperience;
  });

  // 11. Sắp xếp jobs đã lọc theo tiêu chí được chọn
  const sortedJobs = [...filteredJobs].sort((a, b) => {
    switch(sortBy) {
      case 'newest':
        // Sắp xếp từ mới nhất đến cũ nhất (so sánh ngày created_at)
        return new Date(b.created_at) - new Date(a.created_at);
      
      case 'oldest':
        // Sắp xếp từ cũ nhất đến mới nhất
        return new Date(a.created_at) - new Date(b.created_at);
      
      case 'salary-high':
        // Sắp xếp lương từ cao xuống thấp
        // Parse số từ chuỗi salary (VD: "$120,000 - $150,000" -> 150000)
        const getMaxSalary = (salaryStr) => {
          const numbers = salaryStr.match(/\d+/g);
          return numbers ? Math.max(...numbers.map(Number)) : 0;
        };
        return getMaxSalary(b.salary) - getMaxSalary(a.salary);
      
      case 'salary-low':
        // Sắp xếp lương từ thấp lên cao
        const getMinSalary = (salaryStr) => {
          const numbers = salaryStr.match(/\d+/g);
          return numbers ? Math.min(...numbers.map(Number)) : 0;
        };
        return getMinSalary(a.salary) - getMinSalary(b.salary);
      
      case 'title':
        // Sắp xếp theo tên công việc từ A-Z
        return a.title.localeCompare(b.title);
      
      default:
        return 0;
    }
  });

  // 12. Xử lý các trạng thái UI: loading và error
  if (loading) {
    return <div className="jobs-container"><p>Loading jobs...</p></div>;
  }

  if (error) {
    return <div className="jobs-container"><p>Error loading jobs: {error}</p></div>;
  }

  return (
    <div className="jobs-container">
      {/* Hero Section - Phần banner đầu trang */}
      <div className="jobs-hero">
        <div className="hero-content">
          <h1>Find Your Dream Job</h1>
          <p>Discover amazing opportunities that match your skills and passion</p>
        </div>
      </div>

      {/* Main Content - Nội dung chính */}
      <div className="jobs-main">
        <div className="jobs-layout">
          {/* Sidebar Filters - Thanh bộ lọc bên trái */}
          <aside className={`jobs-sidebar ${showFilters ? 'show' : ''}`}>
            <div className="sidebar-header">
              <h3>Filters</h3>
              <button 
                className="clear-filters-btn"
                onClick={clearFilters}
                disabled={getActiveFiltersCount() === 0}
              >
                Clear All ({getActiveFiltersCount()})
              </button>
            </div>

            {/* Phần tìm kiếm */}
            <div className="filter-section">
              <h4>Search</h4>
              <div className="search-input-wrapper">
                <input
                  type="text"
                  placeholder="Search jobs, companies..."
                  className="search-input"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <span className="search-icon"><FontAwesomeIcon icon={faSearch} /></span>
              </div>
            </div>

            {/* Lọc theo Danh mục */}
            <div className="filter-section">
              <h4>Category</h4>
              <div className="filter-buttons">
                {categories.map(category => (
                  <button
                    key={category}
                    className={`filter-btn ${selectedCategory === category ? 'active' : ''}`}
                    onClick={() => handleFilterChange('category', category)}
                  >
                    {category}
                  </button>
                ))}
              </div>
            </div>

            {/* Lọc theo Địa điểm */}
            <div className="filter-section">
              <h4>Location</h4>
              <div className="filter-buttons">
                {locations.map(location => (
                  <button
                    key={location}
                    className={`filter-btn ${selectedLocation === location ? 'active' : ''}`}
                    onClick={() => handleFilterChange('location', location)}
                  >
                    {location}
                  </button>
                ))}
              </div>
            </div>

            {/* Lọc theo Loại công việc */}
            <div className="filter-section">
              <h4>Job Type</h4>
              <div className="filter-buttons">
                {types.map(type => (
                  <button
                    key={type}
                    className={`filter-btn ${selectedType === type ? 'active' : ''}`}
                    onClick={() => handleFilterChange('type', type)}
                  >
                    {type}
                  </button>
                ))}
              </div>
            </div>

            {/* Lọc theo Mức kinh nghiệm */}
            <div className="filter-section">
              <h4>Experience Level</h4>
              <div className="filter-buttons">
                {experiences.map(experience => (
                  <button
                    key={experience}
                    className={`filter-btn ${selectedExperience === experience ? 'active' : ''}`}
                    onClick={() => handleFilterChange('experience', experience)}
                  >
                    {experience}
                  </button>
                ))}
              </div>
            </div>
          </aside>

          {/* Main Content Area - Khu vực nội dung chính */}
          <main className="jobs-content">
            {/* Results Header - Thanh header hiển thị kết quả */}
            <div className="results-header">
              <div className="results-info">
                <h2>Job Opportunities</h2>
                {/* Hiển thị số lượng jobs sau khi lọc */}
                <p>{sortedJobs.length} jobs available {getActiveFiltersCount() > 0 ? `(${jobs.length} total)` : ''}</p>
              </div>
              
              <div className="results-controls">
                {/* Nút Post a Job - Đăng công việc mới */}
                <button 
                  className="add-job-btn"
                  onClick={() => navigate('/add-job')}
                >
                  <FontAwesomeIcon icon={faPlus} /> Post a Job
                </button>
                
                {/* Dropdown sắp xếp */}
                <div className="sort-controls">
                  <label htmlFor="sort-select">Sort by:</label>
                  <select
                    id="sort-select"
                    className="sort-select"
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                  >
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                    <option value="salary-high">Salary: High to Low</option>
                    <option value="salary-low">Salary: Low to High</option>
                    <option value="title">Title A-Z</option>
                  </select>
                </div>
                
                {/* Nút toggle filters cho mobile */}
                <button 
                  className="mobile-filter-toggle"
                  onClick={() => setShowFilters(!showFilters)}
                >
                  Filters ({getActiveFiltersCount()})
                </button>
              </div>
            </div>

            {/* Jobs Grid - Hiển thị danh sách jobs dạng lưới */}
            <div className="jobs-grid">
              {sortedJobs.length > 0 ? (
                sortedJobs.map(job => (
                  <JobCard key={job.id} job={job} />
                ))
              ) : (
                // Hiển thị thông báo khi không tìm thấy jobs phù hợp
                <div className="no-jobs-found">
                  <p>No jobs found matching your criteria. Try adjusting your filters.</p>
                </div>
              )}
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}

export default Jobs;