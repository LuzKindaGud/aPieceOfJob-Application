export const protect = (req, res, next) => {
    
    const userId = req.headers['x-user-id']; // Giả sử client gửi X-User-Id
    const userRole = req.headers['x-user-role']; // Giả sử client gửi X-User-Role

    if (userId && userRole) {
        // Gắn thông tin người dùng vào request
        req.user = { 
            id: parseInt(userId), 
            role: userRole 
        };
        next();
    } else {
        // Nếu không có thông tin giả lập, từ chối
        res.status(401).json({ message: 'Not authorized. Please provide X-User-Id and X-User-Role headers.' });
    }
};

// Middleware kiểm tra vai trò (giữ nguyên logic kiểm tra req.user)
export const restrictTo = (role) => (req, res, next) => {
    if (!req.user || req.user.role !== role) {
        return res.status(403).json({ message: `Forbidden. Only ${role} can perform this action.` });
    }
    next();
};