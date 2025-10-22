import mysql from 'mysql2';
import dotenv from 'dotenv';

dotenv.config();

const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    port: process.env.DB_PORT || 3306,
    waitForConnections: true,
    connectionLimit: 10, // Tối đa 10 cái mạng cùng login đồng thời
    queueLimit: 0
});

// Test connection
pool.getConnection((err, connection) => {
    if (err) {
        console.error('Lỗi kết nối database:', err.message);
        if (err.code === 'PROTOCOL_CONNECTION_LOST') {
            console.error('Database connection was closed.');
        }
        if (err.code === 'ER_CON_COUNT_ERROR') {
            console.error('Database has too many connections.');
        }
        if (err.code === 'ECONNREFUSED') {
            console.error('Database connection was refused.');
        }
        return;
    }
    
    if (connection) {
        console.log('Kết nối database thành công!');
        console.log(`Database: ${process.env.DB_NAME}`);
        connection.release();
    }
});


export default pool.promise(); // Trả về promise-based API

// lợi ích của pooling:

// Tái sử dụng connections thay vì tạo mới mỗi request
// Tự động quản lý connections
// Tăng performance