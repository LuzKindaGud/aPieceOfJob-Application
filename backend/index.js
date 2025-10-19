// index.js
import express from 'express';
import cors from 'cors';
import 'dotenv/config'; 
import db from './models/db.js';
import authRoutes from './routes/auth.routes.js';
import jobRoutes from './routes/job.routes.js';

const app = express();
const PORT = 5000;

// Middleware
app.use(cors({
    origin: 'http://localhost:3000', 
    credentials: true 
})); 
app.use(express.json()); 

// Kiểm tra kết nối DB 
db.getConnection()
    .then(() => console.log("Database connected successfully! 🚀"))
    .catch(err => console.error("Database connection failed:", err.message));

// Định nghĩa Routes
app.use('/api/auth', authRoutes);
app.use('/api/jobs', jobRoutes);

app.get('/', (req, res) => {
    res.send('Job Board Backend API is Running!');
});

// Khởi động Server
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});