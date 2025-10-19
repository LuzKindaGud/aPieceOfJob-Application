// controllers/job.controller.js
import db from '../models/db.js';

// @desc    Lấy danh sách công việc
// @route   GET /api/jobs
export const getJobList = async (req, res) => {
    try {
        // Query JOIN cơ bản để lấy tên công ty và logo
        const sql = `
            SELECT 
                j.job_id, j.title, j.job_level, j.job_type, j.location, 
                j.min_salary, j.max_salary, j.salary_currency, j.posted_at, j.is_featured,
                c.name as company_name, c.logo_url
            FROM Jobs j
            JOIN Companies c ON j.company_id = c.company_id
            WHERE j.status = 'Active'
            ORDER BY j.is_featured DESC, j.posted_at DESC
            LIMIT 50
        `;
        const [jobs] = await db.query(sql);
        
        res.status(200).json({ success: true, count: jobs.length, data: jobs });
    } catch (error) {
        console.error("GET Job List Error:", error.message);
        res.status(500).json({ success: false, message: 'Server error fetching jobs.' });
    }
};

// @desc    Lấy chi tiết công việc
// @route   GET /api/jobs/:id
export const getJobDetails = async (req, res) => {
    const { id } = req.params;
    try {
        const sql = `
            SELECT 
                j.*, c.name as company_name, c.logo_url
            FROM Jobs j
            JOIN Companies c ON j.company_id = c.company_id
            WHERE j.job_id = ? AND j.status = 'Active'
        `;
        const [results] = await db.query(sql, [id]);

        if (results.length === 0) {
            return res.status(404).json({ success: false, message: "Job not found." });
        }
        res.status(200).json({ success: true, data: results[0] }); 
    } catch (error) {
        console.error("GET Job Details Error:", error.message);
        res.status(500).json({ success: false, message: 'Server error fetching job details.' });
    }
};

// @desc    Tạo bài đăng công việc (Recruiter phải đăng nhập)
// @route   POST /api/jobs
export const createJob = async (req, res) => {
    const recruiterId = req.user.id; // Lấy ID Recruiter từ middleware
    const { companyName, logoUrl, jobData } = req.body;
    
    // --- B1: Xử lý Công ty ---
    let companyId;
    try {
        // Giả sử logic kiểm tra/tạo công ty được thực hiện ở đây:
        // 1. Kiểm tra Công ty đã tồn tại chưa (theo tên)
        const [existingCompany] = await db.query('SELECT company_id FROM Companies WHERE name = ?', [companyName]);
        
        if (existingCompany.length > 0) {
            companyId = existingCompany[0].company_id;
        } else {
            // 2. Nếu chưa, tạo công ty mới
            const insertSql = 'INSERT INTO Companies (name, logo_url, added_by_user_id) VALUES (?, ?, ?)';
            const [result] = await db.query(insertSql, [companyName, logoUrl, recruiterId]);
            companyId = result.insertId;
        }
    } catch (error) {
        return res.status(500).json({ message: "Failed to process company information." });
    }

    // --- B2: Tạo Bài đăng Công việc ---
    try {
        const { title, jobLevel, jobType, location, shortDescription, fullRequirements, minSalary, maxSalary, salaryCurrency } = jobData;
        
        const jobSql = `
            INSERT INTO Jobs (company_id, recruiter_id, title, job_level, job_type, location, short_description, full_requirements, min_salary, max_salary, salary_currency, posted_at, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'Active')
        `;
        const jobValues = [
            companyId, recruiterId, title, jobLevel, jobType, location, 
            shortDescription, fullRequirements, minSalary, maxSalary, salaryCurrency
        ];
        
        const [jobResult] = await db.query(jobSql, jobValues);

        res.status(201).json({ 
            success: true, 
            message: "Job posted successfully.", 
            job_id: jobResult.insertId 
        });

    } catch (error) {
        console.error("Create Job Error:", error.message);
        res.status(500).json({ message: 'Server error when posting job.' });
    }
};