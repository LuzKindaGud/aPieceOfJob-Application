// routes/auth.routes.js
import express from 'express';
import * as authController from '../controllers/auth.controller.js';

const router = express.Router();

// POST /api/auth/register -> Đăng ký
router.post('/register', authController.register);

// POST /api/auth/login -> Đăng nhập
router.post('/login', authController.login);

// GET /api/auth/logout -> Logout (chủ yếu chỉ là thông báo cho client)
router.get('/logout', authController.logout); 

export default router;