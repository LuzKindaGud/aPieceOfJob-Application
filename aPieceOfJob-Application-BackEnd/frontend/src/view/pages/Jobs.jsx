import React, { useState } from 'react';
import './pages-style/Jobs.css';
import jobsData from './data/jobs.json';
import JobCard from '../components/JobCard.jsx';

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from '@fortawesome/free-solid-svg-icons';

function Jobs() {
  const [jobs] = useState(jobsData);

  // Lấy giá trị unique cho các filter options (chỉ để hiển thị)
  const categories = [...new Set(jobs.map(job => job.category))];
  const locations = [...new Set(jobs.map(job => job.location))];
  const types = [...new Set(jobs.map(job => job.type))];
  const experiences = [...new Set(jobs.map(job => job.experience))];

  // Các hàm không hoạt động (này trong backend. Maybe?)
  const handleFilterChange = () => {
    // Để không vì anh thích thếthế
  };

  const clearFilters = () => {
    // Để không vì anh thích thế 
  };

  const getActiveFiltersCount = () => {
    return 0; // Luôn trả về 0
  };

  const setSearchTerm = () => {
    // Để không vì anh thích thế 
  };

  const setSortBy = () => {
    // Để không vì anh thích thế 
  };

  const setShowFilters = () => {
    // Để không vì anh thích thế 
  };

  return (
    <div className="jobs-container">
      {/* Hero Section */}
      <div className="jobs-hero">
        <div className="hero-content">
          <h1>Find Your Dream Job</h1>
          <p>Discover amazing opportunities that match your skills and passion</p>
        </div>
      </div>

      {/* Main Content */}
      <div className="jobs-main">
        <div className="jobs-layout">
          {/* Sidebar Filters - UI tương tác nhưng chức năng không hoạt động */}
          <aside className="jobs-sidebar">
            <div className="sidebar-header">
              <h3>Filters</h3>
              <button 
                className="clear-filters-btn"
                onClick={clearFilters}
              >
                Clear All (0)
              </button>
            </div>

            <div className="filter-section">
              <h4>Search</h4>
              <div className="search-input-wrapper">
                <input
                  type="text"
                  placeholder="Search jobs, companies..."
                  className="search-input"
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <span className="search-icon"><FontAwesomeIcon icon={faSearch} /></span>
              </div>
            </div>

            <div className="filter-section">
              <h4>Category</h4>
              <div className="filter-buttons">
                {categories.map(category => (
                  <button
                    key={category}
                    className="filter-btn"
                    onClick={() => handleFilterChange('category', category)}
                  >
                    {category}
                  </button>
                ))}
              </div>
            </div>

            <div className="filter-section">
              <h4>Location</h4>
              <div className="filter-buttons">
                {locations.map(location => (
                  <button
                    key={location}
                    className="filter-btn"
                    onClick={() => handleFilterChange('location', location)}
                  >
                    {location}
                  </button>
                ))}
              </div>
            </div>

            <div className="filter-section">
              <h4>Job Type</h4>
              <div className="filter-buttons">
                {types.map(type => (
                  <button
                    key={type}
                    className="filter-btn"
                    onClick={() => handleFilterChange('type', type)}
                  >
                    {type}
                  </button>
                ))}
              </div>
            </div>

            <div className="filter-section">
              <h4>Experience Level</h4>
              <div className="filter-buttons">
                {experiences.map(experience => (
                  <button
                    key={experience}
                    className="filter-btn"
                    onClick={() => handleFilterChange('experience', experience)}
                  >
                    {experience}
                  </button>
                ))}
              </div>
            </div>
          </aside>

          {/* Main Content Area */}
          <main className="jobs-content">
            {/* Results Header */}
            <div className="results-header">
              <div className="results-info">
                <h2>Job Opportunities</h2>
                <p>{jobs.length} jobs available</p>
              </div>
              
              <div className="results-controls">
                <div className="sort-controls">
                  <label htmlFor="sort-select">Sort by:</label>
                  <select
                    id="sort-select"
                    className="sort-select"
                    onChange={(e) => setSortBy(e.target.value)}
                  >
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                    <option value="salary-high">Salary: High to Low</option>
                    <option value="salary-low">Salary: Low to High</option>
                    <option value="title">Title A-Z</option>
                  </select>
                </div>
                
                <button 
                  className="mobile-filter-toggle"
                  onClick={() => setShowFilters(!showFilters)}
                >
                  Filters (0)
                </button>
              </div>
            </div>

            {/* Jobs Grid */}
            <div className="jobs-grid">
              {jobs.map(job => (
                <JobCard key={job.id} job={job} />
              ))}
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}

export default Jobs;