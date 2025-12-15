import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { 
  faPlus, 
  faEdit, 
  faTrash, 
  faBriefcase, 
  faChartLine, 
  faCheckCircle, 
  faClock,
  faMapMarkerAlt,
  faEye,
  faUsers,
  faFileAlt,
  faTimes,
  faCheck,
  faBan,
  faEnvelope,
  faPhone,
  faCalendar,
  faDownload,
  faExternalLinkAlt,
  faUser,
  faFilter
} from '@fortawesome/free-solid-svg-icons';
import './pages-style/EmployerAdmin.css';

function EmployerAdmin() {
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('jobs'); // 'jobs' or 'applications'
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    const userData = localStorage.getItem('user');
    if (userData) {
      setUser(JSON.parse(userData));
    } else {
      navigate('/login');
    }
  }, [navigate]);

  useEffect(() => {
    const fetchData = async () => {
      if (!user) return;

      try {
        // Fetch jobs from API
        const response = await axios.get('http://localhost:5000/api/jobs');
        const allJobs = response.data.jobs;
        const employerJobs = allJobs.filter(job => job.employer_id === user.id);
        setJobs(employerJobs);

        // Fetch applications from localStorage
        const storedApplications = JSON.parse(localStorage.getItem('jobApplications') || '[]');
        
        // Filter applications for employer's jobs
        const employerJobIds = employerJobs.map(job => job.id);
        const employerApplications = storedApplications.filter(app => 
          employerJobIds.includes(app.jobId)
        );
        
        // Add status if not exists
        const applicationsWithStatus = employerApplications.map(app => ({
          ...app,
          status: app.status || 'pending'
        }));
        
        setApplications(applicationsWithStatus);
      } catch (err) {
        setError(err.message);
        console.error("Error fetching data:", err);
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchData();
    }
  }, [user]);

  const handleDelete = async (jobId) => {
    if (window.confirm('Are you sure you want to delete this job?')) {
      try {
        const token = localStorage.getItem('token');
        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };
        await axios.delete(`http://localhost:5000/api/jobs/${jobId}`, config);
        setJobs(jobs.filter(job => job.id !== jobId));
      } catch (err) {
        console.error("Error deleting job:", err);
        alert('Failed to delete job');
      }
    }
  };

  // Update application status
  const updateApplicationStatus = (applicationIndex, newStatus) => {
    const updatedApplications = [...applications];
    updatedApplications[applicationIndex].status = newStatus;
    setApplications(updatedApplications);

    // Update localStorage
    const allApplications = JSON.parse(localStorage.getItem('jobApplications') || '[]');
    const appToUpdate = applications[applicationIndex];
    const globalIndex = allApplications.findIndex(app => 
      app.jobId === appToUpdate.jobId && 
      app.applicantData.email === appToUpdate.applicantData.email &&
      app.appliedDate === appToUpdate.appliedDate
    );
    
    if (globalIndex !== -1) {
      allApplications[globalIndex].status = newStatus;
      localStorage.setItem('jobApplications', JSON.stringify(allApplications));
    }

    // Close modal if open
    if (selectedApplication) {
      setSelectedApplication({ ...selectedApplication, status: newStatus });
    }
  };

  // Get company initials for avatar
  const getInitials = (name) => {
    if (!name) return 'N/A';
    return name.split(' ').map(word => word[0]).join('').substring(0, 2).toUpperCase();
  };

  // Format date nicely
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const options = { month: 'short', day: 'numeric', year: 'numeric' };
    return date.toLocaleDateString('en-US', options);
  };

  // Filter applications by status
  const filteredApplications = filterStatus === 'all' 
    ? applications 
    : applications.filter(app => app.status === filterStatus);

  // Disable body scroll when modal is open
  useEffect(() => {
    if (selectedApplication) {
      document.body.style.overflow = 'hidden';
      document.documentElement.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
      document.documentElement.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
      document.documentElement.style.overflow = '';
    };
  }, [selectedApplication]);

  // Get status counts
  const statusCounts = {
    all: applications.length,
    pending: applications.filter(app => app.status === 'pending').length,
    accepted: applications.filter(app => app.status === 'accepted').length,
    rejected: applications.filter(app => app.status === 'rejected').length
  };

  if (loading) return (
    <div className="employer-admin-container">
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p className="loading-text">Loading your dashboard...</p>
      </div>
    </div>
  );
  
  if (error) return (
    <div className="employer-admin-container">
      <div className="loading-container">
        <p style={{fontSize: '1.5rem', color: '#ef4444'}}>Error: {error}</p>
      </div>
    </div>
  );

  return (
    <div className="employer-admin-container">
      <div className="employer-admin-header">
        <div className="header-content">
          <h1>Employer Dashboard</h1>
          <p className="header-subtitle">Manage your job postings and applicants</p>
        </div>
        <button className="add-job-btn-admin" onClick={() => navigate('/add-job')}>
          <FontAwesomeIcon icon={faPlus} /> Post New Job
        </button>
      </div>

      {/* Stats Section */}
      <div className="stats-container">
        <div className="stat-card">
          <div className="stat-icon">
            <FontAwesomeIcon icon={faBriefcase} />
          </div>
          <h3>Total Jobs Posted</h3>
          <div className="stat-value">{jobs.length}</div>
          <span className="stat-change positive">
            <FontAwesomeIcon icon={faChartLine} /> Active
          </span>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">
            <FontAwesomeIcon icon={faUsers} />
          </div>
          <h3>Total Applicants</h3>
          <div className="stat-value">{applications.length}</div>
          <span className="stat-change positive">
            <FontAwesomeIcon icon={faChartLine} /> +{statusCounts.pending} pending
          </span>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <FontAwesomeIcon icon={faCheckCircle} />
          </div>
          <h3>Accepted</h3>
          <div className="stat-value">{statusCounts.accepted}</div>
          <span className="stat-change positive">
            Candidates hired
          </span>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <FontAwesomeIcon icon={faClock} />
          </div>
          <h3>Pending Review</h3>
          <div className="stat-value">{statusCounts.pending}</div>
          <span className="stat-change positive">
            {statusCounts.pending === 0 ? 'All clear!' : 'Needs attention'}
          </span>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="tab-navigation">
        <button 
          className={`tab-btn ${activeTab === 'jobs' ? 'active' : ''}`}
          onClick={() => setActiveTab('jobs')}
        >
          <FontAwesomeIcon icon={faBriefcase} /> Job Listings
        </button>
        <button 
          className={`tab-btn ${activeTab === 'applications' ? 'active' : ''}`}
          onClick={() => setActiveTab('applications')}
        >
          <FontAwesomeIcon icon={faUsers} /> Applicants
          {statusCounts.pending > 0 && (
            <span className="notification-badge">{statusCounts.pending}</span>
          )}
        </button>
      </div>

      {/* Jobs Tab */}
      {activeTab === 'jobs' && (
        <div className="jobs-table-container">
          <div className="jobs-table-header">
            <h2>Your Job Listings</h2>
          </div>
          
          {jobs.length > 0 ? (
            <div className="table-wrapper">
              <table className="jobs-table">
                <thead>
                  <tr>
                    <th>Job Title</th>
                    <th>Company</th>
                    <th>Location</th>
                    <th>Applicants</th>
                    <th>Status</th>
                    <th>Date Posted</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {jobs.map(job => {
                    const jobApplications = applications.filter(app => app.jobId === job.id);
                    return (
                      <tr key={job.id}>
                        <td>{job.title}</td>
                        <td>
                          <div className="company-cell">
                            <div className="company-avatar">
                              {getInitials(job.company)}
                            </div>
                            <span>{job.company}</span>
                          </div>
                        </td>
                        <td>
                          <div className="location-cell">
                            <FontAwesomeIcon icon={faMapMarkerAlt} />
                            <span>{job.location}</span>
                          </div>
                        </td>
                        <td>
                          <span className="applicant-count">
                            <FontAwesomeIcon icon={faUsers} /> {jobApplications.length}
                          </span>
                        </td>
                        <td>
                          <span className="status-badge active">Active</span>
                        </td>
                        <td className="date-cell">{formatDate(job.created_at)}</td>
                        <td>
                          <button 
                            className="action-btn view-btn"
                            onClick={() => {
                              setActiveTab('applications');
                              setFilterStatus('all');
                            }}
                            title="View Applicants"
                          >
                            <FontAwesomeIcon icon={faEye} />
                          </button>
                          <button 
                            className="action-btn edit-btn"
                            onClick={() => navigate(`/update-job/${job.id}`, { state: { job } })}
                            title="Edit"
                          >
                            <FontAwesomeIcon icon={faEdit} />
                          </button>
                          <button 
                            className="action-btn delete-btn"
                            onClick={() => handleDelete(job.id)}
                            title="Delete"
                          >
                            <FontAwesomeIcon icon={faTrash} />
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="no-jobs">
              <div className="no-jobs-icon">
                <FontAwesomeIcon icon={faBriefcase} />
              </div>
              <h3>No jobs posted yet</h3>
              <p>Start attracting top talent by posting your first job</p>
              <button className="no-jobs-btn" onClick={() => navigate('/add-job')}>
                <FontAwesomeIcon icon={faPlus} /> Post Your First Job
              </button>
            </div>
          )}
        </div>
      )}

      {/* Applications Tab */}
      {activeTab === 'applications' && (
        <div className="applications-section">
          <div className="applications-header">
            <h2>Applicants</h2>
            <div className="ea-filter-section">
              <FontAwesomeIcon icon={faFilter} />
              <select 
                value={filterStatus} 
                onChange={(e) => setFilterStatus(e.target.value)}
                className="ea-filter-select"
              >
                <option value="all">All ({statusCounts.all})</option>
                <option value="pending">Pending ({statusCounts.pending})</option>
                <option value="accepted">Accepted ({statusCounts.accepted})</option>
                <option value="rejected">Rejected ({statusCounts.rejected})</option>
              </select>
            </div>
          </div>

          {filteredApplications.length > 0 ? (
            <div className="applications-grid">
              {filteredApplications.map((app, index) => (
                <div key={index} className={`application-card status-${app.status}`}>
                  <div className="application-card-header">
                    <div className="applicant-avatar">
                      {getInitials(app.applicantData.fullName)}
                    </div>
                    <div className="applicant-info">
                      <h3>{app.applicantData.fullName}</h3>
                      <p className="applied-for">Applied for: <strong>{app.jobTitle}</strong></p>
                      <p className="company-name">{app.company}</p>
                    </div>
                    <span className={`status-badge ${app.status}`}>
                      {app.status.charAt(0).toUpperCase() + app.status.slice(1)}
                    </span>
                  </div>

                  <div className="application-card-body">
                    <div className="contact-info">
                      <p><FontAwesomeIcon icon={faEnvelope} /> {app.applicantData.email}</p>
                      <p><FontAwesomeIcon icon={faPhone} /> {app.applicantData.phone}</p>
                      <p><FontAwesomeIcon icon={faCalendar} /> {formatDate(app.appliedDate)}</p>
                    </div>
                    
                    {app.applicantData.experience && (
                      <p className="experience-tag">
                        <FontAwesomeIcon icon={faUser} /> {app.applicantData.experience} years exp.
                      </p>
                    )}
                  </div>

                  <div className="application-card-actions">
                    <button 
                      className="action-btn view-details-btn"
                      onClick={() => setSelectedApplication({ ...app, index: applications.indexOf(app) })}
                    >
                      <FontAwesomeIcon icon={faFileAlt} /> View Details
                    </button>
                    
                    {app.status === 'pending' && (
                      <div className="quick-actions">
                        <button 
                          className="action-btn accept-btn"
                          onClick={() => updateApplicationStatus(applications.indexOf(app), 'accepted')}
                          title="Accept"
                        >
                          <FontAwesomeIcon icon={faCheck} />
                        </button>
                        <button 
                          className="action-btn reject-btn"
                          onClick={() => updateApplicationStatus(applications.indexOf(app), 'rejected')}
                          title="Reject"
                        >
                          <FontAwesomeIcon icon={faBan} />
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="no-applications">
              <div className="no-apps-icon">
                <FontAwesomeIcon icon={faUsers} />
              </div>
              <h3>No applicants yet</h3>
              <p>When candidates apply to your jobs, they will appear here</p>
            </div>
          )}
        </div>
      )}

      {/* Application Detail Modal */}
      {selectedApplication && (
        <div className="modal-overlay" onClick={() => setSelectedApplication(null)}>
          <div 
            className="modal-content" 
            onClick={(e) => e.stopPropagation()}
            onWheel={(e) => e.stopPropagation()}
          >
            <button className="modal-close" onClick={() => setSelectedApplication(null)}>
              <FontAwesomeIcon icon={faTimes} />
            </button>

            <div className="modal-header">
              <div className="modal-applicant-info">
                <div className="modal-avatar">
                  {getInitials(selectedApplication.applicantData.fullName)}
                </div>
                <div>
                  <h2>{selectedApplication.applicantData.fullName}</h2>
                  <p>Applied for: <strong>{selectedApplication.jobTitle}</strong></p>
                  <span className={`status-badge ${selectedApplication.status}`}>
                    {selectedApplication.status.charAt(0).toUpperCase() + selectedApplication.status.slice(1)}
                  </span>
                </div>
              </div>
            </div>

            <div className="modal-body">
              <div className="modal-section">
                <h3>Contact Information</h3>
                <div className="info-grid">
                  <div className="info-item">
                    <FontAwesomeIcon icon={faEnvelope} />
                    <div>
                      <label>Email</label>
                      <p>{selectedApplication.applicantData.email}</p>
                    </div>
                  </div>
                  <div className="info-item">
                    <FontAwesomeIcon icon={faPhone} />
                    <div>
                      <label>Phone</label>
                      <p>{selectedApplication.applicantData.phone}</p>
                    </div>
                  </div>
                  {selectedApplication.applicantData.address && (
                    <div className="info-item">
                      <FontAwesomeIcon icon={faMapMarkerAlt} />
                      <div>
                        <label>Address</label>
                        <p>{selectedApplication.applicantData.address}</p>
                      </div>
                    </div>
                  )}
                  <div className="info-item">
                    <FontAwesomeIcon icon={faCalendar} />
                    <div>
                      <label>Applied Date</label>
                      <p>{formatDate(selectedApplication.appliedDate)}</p>
                    </div>
                  </div>
                </div>
              </div>

              {selectedApplication.applicantData.experience && (
                <div className="modal-section">
                  <h3>Experience</h3>
                  <p className="experience-value">{selectedApplication.applicantData.experience} years</p>
                </div>
              )}

              {selectedApplication.applicantData.portfolio && (
                <div className="modal-section">
                  <h3>Portfolio / LinkedIn</h3>
                  <a 
                    href={selectedApplication.applicantData.portfolio} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="portfolio-link"
                  >
                    <FontAwesomeIcon icon={faExternalLinkAlt} />
                    {selectedApplication.applicantData.portfolio}
                  </a>
                </div>
              )}

              <div className="modal-section">
                <h3>Cover Letter</h3>
                <div className="cover-letter-content">
                  {selectedApplication.applicantData.coverLetter}
                </div>
              </div>

              <div className="modal-section">
                <h3>Resume / CV</h3>
                <div className="resume-section">
                  <div className="resume-file">
                    <FontAwesomeIcon icon={faFileAlt} />
                    <span>{selectedApplication.applicantData.resume}</span>
                  </div>
                  <button className="download-cv-btn">
                    <FontAwesomeIcon icon={faDownload} /> Download CV
                  </button>
                </div>
              </div>
            </div>

            <div className="modal-footer">
              {selectedApplication.status === 'pending' ? (
                <>
                  <button 
                    className="modal-btn reject"
                    onClick={() => updateApplicationStatus(selectedApplication.index, 'rejected')}
                  >
                    <FontAwesomeIcon icon={faBan} /> Reject
                  </button>
                  <button 
                    className="modal-btn accept"
                    onClick={() => updateApplicationStatus(selectedApplication.index, 'accepted')}
                  >
                    <FontAwesomeIcon icon={faCheck} /> Accept
                  </button>
                </>
              ) : (
                <button 
                  className="modal-btn secondary"
                  onClick={() => updateApplicationStatus(selectedApplication.index, 'pending')}
                >
                  <FontAwesomeIcon icon={faClock} /> Reset to Pending
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default EmployerAdmin;
