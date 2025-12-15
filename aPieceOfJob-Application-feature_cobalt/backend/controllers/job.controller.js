// controllers/job.controller.js
import db from '../models/db.js';

// @desc    Lấy danh sách công việc
// @route   GET /api/jobs
export const getJobList = async (req, res) => {
    try {
        const [jobs] = await db.query(
            'SELECT * FROM jobs ORDER BY created_at DESC'
        );

        res.json({
            success: true,
            count: jobs.length,
            jobs
        });
    } catch (error) {
        console.error('Get job list error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách công việc',
            error: error.message
        });
    }
};

// @desc    Lấy chi tiết công việc
// @route   GET /api/jobs/:id
export const getJobDetails = async (req, res) => {
    try {
        const { id } = req.params;

        const [jobs] = await db.query(
            'SELECT * FROM jobs WHERE id = ?',
            [id]
        );

        if (jobs.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy công việc'
            });
        }

        res.json({
            success: true,
            job: jobs[0]
        });
    } catch (error) {
        console.error('Get job details error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy chi tiết công việc',
            error: error.message
        });
    }
};

// @desc    Tạo bài đăng công việc (Recruiter phải đăng nhập)
// @route   POST /api/jobs
export const createJob = async (req, res) => {
    try {
        // 1. Thêm các trường mới vào destructuring
        const { title, company, location, salary, type, category, experience, description, requirements, benefits, image, featured } = req.body;
        const employerId = req.user.id; // Lấy từ token

        // Validate
        if (!title || !company || !location) {
            return res.status(400).json({
                success: false,
                message: 'Title, company và location là bắt buộc'
            });
        }

        // 2. Cập nhật câu lệnh INSERT
        const [result] = await db.query(
            'INSERT INTO jobs (title, company, location, salary, type, category, experience, description, requirements, benefits, image, featured, employer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
            // 3. Thêm các biến mới vào mảng tham số
            [title, company, location, salary || null, type || null, category || null, experience || null, description || null, requirements || null, benefits || null, image || null, featured || false, employerId]
        );

        res.status(201).json({
            success: true,
            message: 'Tạo công việc thành công',
            jobId: result.insertId
            
        });
    } catch (error) {
        console.error('Create job error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi tạo công việc',
            error: error.message
        });
    }
};

// Xóa job (chỉ employer/admin)
export const deleteJob = async (req, res) => {
    try {
        const { id } = req.params;
        const employerId = req.user.id;

        // Kiểm tra job có tồn tại không
        const [jobs] = await db.query('SELECT * FROM jobs WHERE id = ?', [id]);

        if (jobs.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy công việc'
            });
        }

        // Kiểm tra quyền sở hữu
        if (req.user.role !== 'admin' && jobs[0].employer_id !== employerId) {
            return res.status(403).json({
                success: false,
                message: 'Bạn không có quyền xóa công việc này'
            });
        }

        await db.query('DELETE FROM jobs WHERE id = ?', [id]);

        res.json({
            success: true,
            message: 'Xóa công việc thành công'
        });
    } catch (error) {
        console.error('Delete job error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi xóa công việc',
            error: error.message
        });
    }
};

export const updateJob = async (req, res) => {
    try {
        const { id } = req.params;
        // 1. Thêm các trường mới vào destructuring
        const { title, company, location, salary, type, category, experience, description, requirements, benefits, image, featured } = req.body;
        const employerId = req.user.id;

        // Kiểm tra job có tồn tại không
        const [jobs] = await db.query('SELECT * FROM jobs WHERE id = ?', [id]);

        if (jobs.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy công việc'
            });
        }

        // Kiểm tra quyền sở hữu (chỉ employer tạo job hoặc admin mới được sửa)
        if (req.user.role !== 'admin' && jobs[0].employer_id !== employerId) {
            return res.status(403).json({
                success: false,
                message: 'Bạn không có quyền cập nhật công việc này'
            });
        }

        // 2. Cập nhật câu lệnh UPDATE
        await db.query(
            'UPDATE jobs SET title = ?, company = ?, location = ?, salary = ?, type = ?, category = ?, experience = ?, description = ?, requirements = ?, benefits = ?, image = ?, featured = ? WHERE id = ?',
            // 3. Thêm các biến mới vào mảng tham số
            [title, company, location, salary, type, category, experience, description, requirements, benefits, image, featured, id]
        );

        res.json({
            success: true,
            message: 'Cập nhật công việc thành công'
        });
    } catch (error) {
        console.error('Update job error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi cập nhật công việc',
            error: error.message
        });
    }
};
