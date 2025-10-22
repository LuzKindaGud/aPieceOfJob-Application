-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 22, 2025 lúc 12:00 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `apieceofjob`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `jobs`
--

CREATE TABLE `jobs` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `company` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `salary` varchar(100) DEFAULT NULL,
  `type` varchar(50) NOT NULL,
  `category` varchar(100) NOT NULL,
  `experience` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `requirements` text DEFAULT NULL,
  `benefits` text DEFAULT NULL,
  `employer_id` int(11) NOT NULL,
  `status` enum('active','inactive','closed') DEFAULT 'active',
  `created_at` date NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `image` varchar(250) DEFAULT NULL,
  `featured` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `jobs`
--

INSERT INTO `jobs` (`id`, `title`, `company`, `location`, `salary`, `type`, `category`, `experience`, `description`, `requirements`, `benefits`, `employer_id`, `status`, `created_at`, `updated_at`, `image`, `featured`) VALUES
(2, 'UX/UI Designer', 'Creative Studio', 'Hanoi, Vietnam', '$80,000 - $100,000', 'Full-time', 'Design', 'Mid-level', 'Join our design team to create beautiful and intuitive user experiences. Work on diverse projects from mobile apps to web platforms.', '3+ years UX/UI experience, Figma/Sketch proficiency, User research skills, Prototyping experience', 'Health insurance, Professional development, Creative freedom, Team events', 2, 'active', '2025-10-22', '2025-10-22 08:11:34', 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=400&h=300&fit=crop', 0),
(4, 'Backend Engineer', 'DataFlow Inc', 'Da Nang, Vietnam', '$110,000 - $140,000', 'Full-time', 'Development', 'Mid-level', 'Build scalable backend systems using Node.js, Python, and cloud technologies. Work on high-traffic applications serving millions of users.', '5+ years backend experience, Node.js/Python expertise, Database design, Cloud platforms (AWS/GCP)', 'Health insurance, Stock options, Learning budget, Flexible hours', 6, 'active', '2025-10-22', '2025-10-22 09:35:24', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400&h=300&fit=crop', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `role` enum('user','employer','admin') DEFAULT 'user',
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `phone`, `avatar`, `role`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'admin', 'admin@apieceofjob.com', '$2b$10$YourHashedPasswordHere', 'Administrator', NULL, NULL, 'admin', 1, '2025-10-21 14:17:35', '2025-10-21 14:17:35'),
(2, 'recruiter1', 'recruiter@company.com', '$2b$10$q5f2U4Us93j3/UmGDA7uouYbEAgqhp50uI.tg7Kzj.W9j2T0iX6om', 'Nhà tuyển dụng', NULL, 'Recruiter', 'employer', 1, '2025-10-21 14:58:17', '2025-10-21 15:23:55'),
(3, 'recruiter123', 'recruiter123@company.com', '$2b$10$.cE7uoPrHHFcMJrBvKksw.qIUu.OpaaWsOyXzEPeITdAFqRv7hb0K', 'Nhà tuyển dụng', NULL, 'Recruiter', 'employer', 1, '2025-10-21 15:17:51', '2025-10-21 15:20:43'),
(4, 'employer1', 'employer@company.com', '$2b$10$FSKlLOANeJ9OfWZCY2F/j.BuPMwf3YYA8KteXua4lKwtf5Vrm1Pw.', 'Nhà tuyển dụng', NULL, NULL, 'employer', 1, '2025-10-21 15:42:05', '2025-10-21 15:42:05'),
(6, 'RiyaKi', 'truykichpro1995@gmail.com', '$2b$10$L10iwZEmhQkKk1mCI/a2N.q3cE.0OC6vNbIHKKtgpQYQr28/dB3FC', NULL, NULL, NULL, 'admin', 1, '2025-10-22 07:27:39', '2025-10-22 09:57:15');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_employer` (`employer_id`),
  ADD KEY `idx_status` (`status`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_username` (`username`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `jobs`
--
ALTER TABLE `jobs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `jobs`
--
ALTER TABLE `jobs`
  ADD CONSTRAINT `jobs_ibfk_1` FOREIGN KEY (`employer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
