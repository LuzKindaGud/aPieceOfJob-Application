import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import db from '../models/db.js';

// Đăng ký
export const register = async (req, res) => {
    try {
        const { username, email, password, full_name, phone, role } = req.body;

        // Validate input
        if (!username || !email || !password) {
            return res.status(400).json({ 
                success: false,
                message: 'Username, email và password là bắt buộc' 
            });
        }

        // Validate role
        const validRoles = ['user', 'employer', 'admin'];
        const userRole = role || 'user';
        
        if (!validRoles.includes(userRole)) {
            return res.status(400).json({ 
                success: false,
                message: 'Role không hợp lệ. Chỉ chấp nhận: user, employer, admin' 
            });
        }

        // Kiểm tra email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return res.status(400).json({ 
                success: false,
                message: 'Email không hợp lệ' 
            });
        }

        // Kiểm tra password length
        if (password.length < 6) {
            return res.status(400).json({ 
                success: false,
                message: 'Password phải có ít nhất 6 ký tự' 
            });
        }

        // Kiểm tra user đã tồn tại
        const [existingUsers] = await db.query(
            'SELECT * FROM users WHERE email = ? OR username = ?',
            [email, username]
        );

        if (existingUsers.length > 0) {
            return res.status(400).json({ 
                success: false,
                message: 'Username hoặc email đã tồn tại' 
            });
        }

        // Mã hóa password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Lưu user vào database
        const [result] = await db.query(
            'INSERT INTO users (username, email, password, full_name, phone, role) VALUES (?, ?, ?, ?, ?, ?)',
            [username, email, hashedPassword, full_name || null, phone || null, userRole]
        );

        res.status(201).json({ 
            success: true,
            message: 'Đăng ký thành công',
            userId: result.insertId
        });
    } catch (error) {
        console.error('Register error:', error);
        res.status(500).json({ 
            success: false,
            message: 'Lỗi server', 
            error: error.message 
        });
    }
};

// Đăng nhập
export const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        // Validate input
        if (!email || !password) {
            return res.status(400).json({ 
                success: false,
                message: 'Email và password là bắt buộc' 
            });
        }

        // Tìm user
        const [users] = await db.query(
            'SELECT * FROM users WHERE email = ?',
            [email]
        );

        if (users.length === 0) {
            return res.status(401).json({ 
                success: false,
                message: 'Email hoặc password không đúng' 
            });
        }

        const user = users[0];

        // Kiểm tra password
        const isPasswordValid = await bcrypt.compare(password, user.password);

        if (!isPasswordValid) {
            return res.status(401).json({ 
                success: false,
                message: 'Email hoặc password không đúng' 
            });
        }

        // Tạo JWT token
        const token = jwt.sign(
            { 
                id: user.id, 
                username: user.username,
                email: user.email,
                role: user.role
            },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN || '24h' }
        );

        // Trả về token và user info

        res.json({
            success: true,
            message: 'Đăng nhập thành công',
            token,
            user: {
                id: user.id,
                username: user.username,
                email: user.email,
                full_name: user.full_name,
                phone: user.phone,
                avatar: user.avatar,
                role: user.role
            }
        });
    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({ 
            success: false,
            message: 'Lỗi server', 
            error: error.message 
        });
    }
};

// Get user profile
export const getProfile = async (req, res) => {
     // req.user được set từ middleware authenticateToken
    try {
        const userId = req.user.id;

        const [users] = await db.query(
            'SELECT id, username, email, full_name, phone, avatar, role, created_at FROM users WHERE id = ?',
            [userId]
        );

        if (users.length === 0) {
            return res.status(404).json({ 
                success: false,
                message: 'User không tồn tại' 
            });
        }

        res.json({
            success: true,
            user: users[0]
        });
    } catch (error) {
        console.error('Get profile error:', error);
        res.status(500).json({ 
            success: false,
            message: 'Lỗi server', 
            error: error.message 
        });
    }
};