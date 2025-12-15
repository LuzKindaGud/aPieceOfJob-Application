import jwt from 'jsonwebtoken';

// Middleware xác thực token (alias: protect)
export const authenticateToken = (req, res, next) => {
    // Lấy token từ header "Authorization: Bearer TOKEN"
    try {
        const authHeader = req.headers['authorization'];
        const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

        if (!token) {
            return res.status(401).json({ 
                success: false,
                message: 'Không tìm thấy token xác thực' 
            });
        }
        // Verify token với JWT_SECRET
        jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
            if (err) {
                return res.status(403).json({ 
                    success: false,
                    message: 'Token không hợp lệ hoặc đã hết hạn' 
                });
            }
        // Attach user info vào request
            req.user = user; // { id, role, email }
            next();
        });
    } catch (error) {
        res.status(500).json({ 
            success: false,
            message: 'Lỗi xác thực', 
            error: error.message 
        });
    }
};

// Alias cho authenticateToken
export const protect = authenticateToken;

// Middleware phân quyền theo role
export const restrictTo = (...roles) => {
    return (req, res, next) => {
        // req.user được set từ authenticateToken/protect middleware
        if (!req.user) {
            return res.status(401).json({
                success: false,
                message: 'Bạn chưa đăng nhập'
            });
        }

        if (!roles.includes(req.user.role)) {
            return res.status(403).json({
                success: false,
                message: `Chỉ ${roles.join(', ')} mới có quyền thực hiện thao tác này`
            });
        }

        next();
    };
};

// Middleware kiểm tra role cụ thể
export const isRecruiter = (req, res, next) => {
    if (req.user.role !== 'employer' && req.user.role !== 'admin') {
        return res.status(403).json({
            success: false,
            message: 'Chỉ employer mới có quyền đăng tin tuyển dụng'
        });
    }
    next();
};

export const isAdmin = (req, res, next) => {
    if (req.user.role !== 'admin') {
        return res.status(403).json({
            success: false,
            message: 'Chỉ Admin mới có quyền truy cập'
        });
    }
    next();
};

