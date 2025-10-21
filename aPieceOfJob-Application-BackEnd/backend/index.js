// index.js
import express from 'express';
import cors from 'cors';
import 'dotenv/config'; 
import db from './src/models/db.js';
import authRoutes from './src/routes/auth.routes.js';
import jobRoutes from './src/routes/job.routes.js';

const app = express();
const PORT = 5000;

// Middleware
app.use(cors({
    origin:  ['http://localhost:3000', 'http://localhost:5173', 'http://127.0.0.1:3000'],
    credentials: true ,
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization']
})); // Cho phép frontend gọi API

app.use(express.json()); // Parse JSON request body

// Kiểm tra kết nối DB 
db.getConnection()
    .then(() => console.log("Database connected successfully! 🚀"))
    .catch(err => console.error("Database connection failed:", err.message));

// Định nghĩa Routes
app.use('/api/auth', authRoutes); // Authentication endpoints
app.use('/api/jobs', jobRoutes); // Job management endpoints

app.get('/', (req, res) => {
    res.send(' Backend API is Running!');
});

// Khởi động Server
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});

