-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: localhost:3307
-- Thời gian đã tạo: Th10 22, 2025 lúc 01:45 PM
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
(4, 'Backend Engineer', 'DataFlow Inc', 'Da Nang, Vietnam', '$110,000 - $140,000', 'Full-time', 'Development', 'Mid-level', 'Build scalable backend systems using Node.js, Python, and cloud technologies. Work on high-traffic applications serving millions of users.', '5+ years backend experience, Node.js/Python expertise, Database design, Cloud platforms (AWS/GCP)', 'Health insurance, Stock options, Learning budget, Flexible hours', 6, 'active', '2025-10-22', '2025-10-22 09:35:24', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400&h=300&fit=crop', 1),
(5, 'Senior Frontend Developer', 'Tech Innovations', 'Ho Chi Minh City, Vietnam', '$90,000 - $120,000', 'Full-time', 'Development', 'Senior', 'Lead frontend development using React, TypeScript, and modern web technologies. Build responsive and performant web applications.', '5+ years React experience, TypeScript proficiency, Redux/MobX, Testing (Jest/Cypress), CI/CD knowledge', 'Competitive salary, Remote work, Health insurance, Annual bonus, Team building', 2, 'active', '2025-10-22', '2025-10-22 03:00:00', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=300&fit=crop', 1),
(6, 'Product Manager', 'StartupHub', 'Hanoi, Vietnam', '$100,000 - $130,000', 'Full-time', 'Management', 'Mid-level', 'Define product strategy and roadmap. Work closely with engineering, design, and business teams to deliver exceptional products.', '3+ years product management, Agile/Scrum experience, Data-driven decision making, Excellent communication skills', 'Stock options, Flexible hours, Learning budget, Gym membership, Team events', 4, 'active', '2025-10-22', '2025-10-22 03:15:00', 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop', 1),
(7, 'Data Scientist', 'AI Solutions Co', 'Da Nang, Vietnam', '$95,000 - $125,000', 'Full-time', 'Data Science', 'Mid-level', 'Analyze large datasets, build predictive models, and derive actionable insights. Work with machine learning and AI technologies.', 'Python/R proficiency, Machine learning algorithms, SQL expertise, Statistical analysis, Data visualization (Tableau/PowerBI)', 'Health insurance, Remote work option, Conference attendance, Professional development, Bonus scheme', 2, 'active', '2025-10-22', '2025-10-22 03:30:00', 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&h=300&fit=crop', 0),
(8, 'DevOps Engineer', 'CloudTech Systems', 'Ho Chi Minh City, Vietnam', '$105,000 - $135,000', 'Full-time', 'DevOps', 'Senior', 'Design and maintain CI/CD pipelines, manage cloud infrastructure, and ensure system reliability and scalability.', '4+ years DevOps experience, AWS/Azure/GCP expertise, Docker/Kubernetes, Terraform/Ansible, Monitoring tools (Prometheus/Grafana)', 'Competitive salary, Remote work, Certification support, Health insurance, Annual leave', 6, 'active', '2025-10-22', '2025-10-22 03:45:00', 'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=400&h=300&fit=crop', 1),
(9, 'Mobile App Developer', 'AppMasters', 'Hanoi, Vietnam', '$75,000 - $95,000', 'Full-time', 'Development', 'Junior', 'Develop cross-platform mobile applications using React Native or Flutter. Work on consumer-facing apps with millions of users.', '2+ years mobile development, React Native or Flutter, REST API integration, Git version control, App store deployment', 'Health insurance, Learning budget, Flexible hours, Team events, Career growth', 4, 'active', '2025-10-22', '2025-10-22 04:00:00', 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=400&h=300&fit=crop', 0),
(10, 'QA Automation Engineer', 'QualityFirst Tech', 'Da Nang, Vietnam', '$70,000 - $90,000', 'Full-time', 'Quality Assurance', 'Mid-level', 'Design and implement automated test frameworks. Ensure software quality through comprehensive testing strategies.', '3+ years QA experience, Selenium/Cypress/Playwright, API testing (Postman), CI/CD integration, Programming skills (Java/Python)', 'Health insurance, Remote work, Training programs, Performance bonus, Team building', 2, 'active', '2025-10-22', '2025-10-22 04:15:00', 'https://images.unsplash.com/photo-1516321497487-e288fb19713f?w=400&h=300&fit=crop', 0),
(11, 'Digital Marketing Manager', 'MarketPro Agency', 'Ho Chi Minh City, Vietnam', '$65,000 - $85,000', 'Full-time', 'Marketing', 'Mid-level', 'Lead digital marketing campaigns across multiple channels. Manage SEO, SEM, social media, and content marketing strategies.', '4+ years digital marketing, Google Analytics/Ads, SEO/SEM expertise, Content strategy, Social media management', 'Performance bonus, Creative freedom, Flexible schedule, Health insurance, Team events', 4, 'active', '2025-10-22', '2025-10-22 04:30:00', 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400&h=300&fit=crop', 0),
(12, 'Cybersecurity Analyst', 'SecureNet Solutions', 'Hanoi, Vietnam', '$85,000 - $110,000', 'Full-time', 'Security', 'Mid-level', 'Monitor and protect systems from security threats. Conduct vulnerability assessments and implement security best practices.', '3+ years security experience, Network security, Penetration testing, SIEM tools, Security certifications (CISSP/CEH)', 'Certification support, Health insurance, Remote work option, Professional development, Bonus scheme', 6, 'active', '2025-10-22', '2025-10-22 04:45:00', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=400&h=300&fit=crop', 1),
(13, 'Business Analyst', 'Consulting Group', 'Da Nang, Vietnam', '$60,000 - $80,000', 'Full-time', 'Business', 'Junior', 'Analyze business processes, gather requirements, and bridge the gap between business and technology teams.', '2+ years BA experience, Requirements gathering, Data analysis, SQL knowledge, Agile methodology, Excellent communication', 'Health insurance, Career development, Flexible hours, Team events, Performance bonus', 2, 'active', '2025-10-22', '2025-10-22 05:00:00', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=300&fit=crop', 0),
(14, 'Cloud Architect', 'Enterprise Solutions', 'Ho Chi Minh City, Vietnam', '$120,000 - $150,000', 'Full-time', 'Architecture', 'Senior', 'Design and implement cloud-native architectures. Lead cloud migration projects and establish best practices for scalability and security.', '7+ years cloud experience, Multi-cloud expertise (AWS/Azure/GCP), Microservices architecture, Kubernetes, Cloud certifications', 'Executive compensation, Stock options, Remote work, Conference travel, Professional development, Leadership opportunities', 6, 'active', '2025-10-22', '2025-10-22 05:15:00', 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400&h=300&fit=crop', 1);

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
(6, 'RiyaKi', 'truykichpro1995@gmail.com', '$2b$10$L10iwZEmhQkKk1mCI/a2N.q3cE.0OC6vNbIHKKtgpQYQr28/dB3FC', NULL, NULL, NULL, 'admin', 1, '2025-10-22 07:27:39', '2025-10-22 09:57:15'),
(7, 'mambo', 'vvankhanh022@gmail.com', '$2b$10$NfEBNa08gNRj0dlY61PfyOR0B2wchkOmB1V9dUryHJ5zDYVD4.y5.', NULL, NULL, NULL, 'employer', 1, '2025-10-22 11:12:51', '2025-10-22 11:12:51');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

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
