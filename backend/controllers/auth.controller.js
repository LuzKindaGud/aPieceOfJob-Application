// controllers/auth.controller.js
import db from '../models/db.js';
import bcrypt from 'bcrypt';

// Số lần băm mật khẩu
const saltRounds = 10; 

// @desc    Đăng ký người dùng mới (Candidate hoặc Recruiter)
// @route   POST /api/auth/register
export const register = async (req, res) => {
    const { email, password, name, role } = req.body;

    if (!email || !password || !name || !['Candidate', 'Recruiter'].includes(role)) {
        return res.status(400).json({ message: "Missing required fields or invalid role." });
    }

    try {
        // 1. Kiểm tra Email đã tồn tại chưa
        const [existingUser] = await db.query('SELECT user_id FROM Users WHERE email = ?', [email]);
        if (existingUser.length > 0) {
            return res.status(400).json({ message: "Email already exists." });
        }

        // 2. Băm (Hash) mật khẩu
        const hashedPassword = await bcrypt.hash(password, saltRounds);

        // 3. Tạo người dùng mới
        const sql = 'INSERT INTO Users (email, password_hash, name, role) VALUES (?, ?, ?, ?)';
        const [result] = await db.query(sql, [email, hashedPassword, name, role]);

        res.status(201).json({ 
            success: true, 
            message: `${role} registered successfully.`, 
            user_id: result.insertId,
            role: role
        });

    } catch (error) {
        console.error("Registration Error:", error.message);
        res.status(500).json({ message: 'Server error during registration.' });
    }
};


// @desc    Đăng nhập người dùng
// @route   POST /api/auth/login
export const login = async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ message: "Please enter email and password." });
    }

    try {
        // 1. Tìm người dùng theo Email
        const [users] = await db.query('SELECT user_id, password_hash, name, role FROM Users WHERE email = ?', [email]);
        const user = users[0];

        if (!user) {
            return res.status(401).json({ message: "Invalid credentials (Email not found)." });
        }

        // 2. So sánh mật khẩu
        const isMatch = await bcrypt.compare(password, user.password_hash);

        if (isMatch) {
            // ĐĂNG NHẬP THÀNH CÔNG: Trả về thông tin cơ bản
            // Frontend React có trách nhiệm lưu user_id và role này
            res.status(200).json({ 
                success: true, 
                message: "Login successful.",
                user: { 
                    id: user.user_id, 
                    name: user.name,
                    role: user.role 
                } 
            });
        } else {
            return res.status(401).json({ message: "Invalid credentials (Password incorrect)." });
        }

    } catch (error) {
        console.error("Login Error:", error.message);
        res.status(500).json({ message: 'Server error during login.' });
    }
};

// Hàm Logout không cần thiết trong mô hình Stateless này, 
// vì client chỉ cần xóa thông tin user_id và role đã lưu.
export const logout = (req, res) => {
    res.status(200).json({ message: "Logged out on client-side (stateless backend)." });
};