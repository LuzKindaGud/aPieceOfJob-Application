// routes/job.routes.js
import express from 'express';
import * as jobController from '../controllers/job.controller.js';
import { protect, restrictTo } from '../middleware/auth.middleware.js'; 

const router = express.Router();

// Public routes
router.get('/', jobController.getJobList); 
router.get('/:id', jobController.getJobDetails);

// Protected routes (Recruiter)
router.post(
    '/', 
    protect, 
    restrictTo('employer', 'admin'), 
    jobController.createJob
); 

router.put(
    '/:id',
    protect,
    restrictTo('employer', 'admin'),  // ← Sửa đây
    jobController.updateJob
);

router.delete(
    '/:id',
    protect,
    restrictTo('employer', 'admin'),  // ← Sửa đây
    jobController.deleteJob
);

export default router;