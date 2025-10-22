# Hướng Dẫn Sử Dụng Chức Năng Sắp Xếp và Lọc Jobs

## Tổng Quan
Đã cài đặt thành công chức năng **Sắp xếp (Sort)** và **Lọc (Filter)** cho trang Jobs với đầy đủ comments tiếng Việt.

---

## Các Tính Năng Đã Cài Đặt

### 1. 🔍 Tìm Kiếm (Search)
- **Chức năng**: Tìm kiếm jobs theo từ khóa
- **Tìm trong**: 
  - Tên công việc (title)
  - Tên công ty (company)
  - Mô tả công việc (description)
- **Cách dùng**: Nhập từ khóa vào ô search trong sidebar

### 2. 🏷️ Lọc Theo Danh Mục (Category Filter)
- **Các danh mục**: Development, Design, Marketing, DevOps, Product, Data Science, Sales, Security, Quality Assurance, Game Development, Content
- **Chức năng**: Lọc jobs theo category
- **Toggle**: Click vào button để chọn/bỏ chọn
- **Hiển thị**: Button được chọn sẽ có màu gradient và shadow

### 3. 📍 Lọc Theo Địa Điểm (Location Filter)
- **Các địa điểm**: Ho Chi Minh City, Hanoi, Da Nang, Remote, Tokyo
- **Chức năng**: Lọc jobs theo vị trí
- **Toggle**: Click vào button để chọn/bỏ chọn

### 4. 💼 Lọc Theo Loại Công Việc (Job Type Filter)
- **Các loại**: Full-time, Part-time, Contract, Freelance
- **Chức năng**: Lọc jobs theo hình thức làm việc
- **Toggle**: Click vào button để chọn/bỏ chọn

### 5. 🎓 Lọc Theo Kinh Nghiệm (Experience Level Filter)
- **Các mức**: Entry-level, Mid-level, Senior
- **Chức năng**: Lọc jobs theo mức độ kinh nghiệm yêu cầu
- **Toggle**: Click vào button để chọn/bỏ chọn

### 6. 🔄 Sắp Xếp (Sort)
- **Newest First**: Sắp xếp từ mới nhất đến cũ nhất (theo created_at)
- **Oldest First**: Sắp xếp từ cũ nhất đến mới nhất
- **Salary: High to Low**: Sắp xếp lương từ cao xuống thấp
- **Salary: Low to High**: Sắp xếp lương từ thấp lên cao
- **Title A-Z**: Sắp xếp theo tên công việc từ A-Z

### 7. 🧹 Xóa Bộ Lọc (Clear All)
- **Chức năng**: Xóa tất cả các filter đã chọn
- **Hiển thị**: Số lượng filter đang active (vd: "Clear All (3)")
- **Trạng thái**: Disabled khi không có filter nào được chọn

---

## Cách Hoạt Động

### Luồng Xử Lý Dữ Liệu
```
1. Fetch jobs từ API (useEffect)
   ↓
2. Áp dụng các filter (filteredJobs)
   - Kiểm tra searchTerm
   - Kiểm tra selectedCategory
   - Kiểm tra selectedLocation
   - Kiểm tra selectedType
   - Kiểm tra selectedExperience
   ↓
3. Sắp xếp kết quả đã lọc (sortedJobs)
   - Theo tiêu chí sortBy được chọn
   ↓
4. Hiển thị kết quả (JobCard components)
```

### State Management
```javascript
// Dữ liệu từ API
const [jobs, setJobs] = useState([]);
const [loading, setLoading] = useState(true);
const [error, setError] = useState(null);

// Các filter state
const [searchTerm, setSearchTerm] = useState('');
const [selectedCategory, setSelectedCategory] = useState('');
const [selectedLocation, setSelectedLocation] = useState('');
const [selectedType, setSelectedType] = useState('');
const [selectedExperience, setSelectedExperience] = useState('');

// Sort và UI state
const [sortBy, setSortBy] = useState('newest');
const [showFilters, setShowFilters] = useState(false);
```

---

## UI/UX Features

### Visual Feedback
1. **Active Filter Buttons**: 
   - Background gradient (orange to pink)
   - Border color thay đổi
   - Box shadow nổi bật

2. **Disabled Clear Button**:
   - Màu xám, opacity giảm
   - Cursor: not-allowed
   - Không thể click khi không có filter

3. **Filter Count Display**:
   - Hiển thị số lượng filter active
   - "X jobs available (Y total)" khi có filter

4. **No Results Message**:
   - Hiển thị khi không tìm thấy jobs phù hợp
   - Gợi ý điều chỉnh filter

### Responsive Design
- **Desktop**: Sidebar cố định bên trái
- **Tablet**: Sidebar thu nhỏ
- **Mobile**: Sidebar trở thành overlay slide-in

---

## Các File Đã Thay Đổi

### 1. `Jobs.jsx` (Frontend Component)
**Thay đổi chính**:
- ✅ Thêm state cho filters và sort
- ✅ Implement logic lọc (filteredJobs)
- ✅ Implement logic sắp xếp (sortedJobs)
- ✅ Cập nhật UI để hiển thị filter active
- ✅ Thêm đầy đủ comments tiếng Việt

**Các hàm quan trọng**:
- `handleFilterChange()`: Xử lý khi chọn/bỏ chọn filter
- `clearFilters()`: Xóa tất cả filters
- `getActiveFiltersCount()`: Đếm số filter đang active

### 2. `Jobs.css` (Styling)
**Thay đổi chính**:
- ✅ Thêm style cho `.filter-btn.active`
- ✅ Thêm style cho `.clear-filters-btn:disabled`
- ✅ Thêm style cho `.no-jobs-found`
- ✅ Thêm comments tiếng Việt

### 3. `JobCard.jsx` (Component)
**Thay đổi chính**:
- ✅ Thêm đầy đủ comments tiếng Việt cho từng phần
- ✅ Giải thích rõ ràng logic formatDate
- ✅ Comments cho các phần UI

---

## Testing Guide

### Test Cases

#### 1. Test Search
- [ ] Nhập "Developer" → Chỉ hiển thị jobs có "Developer" trong title/company/description
- [ ] Xóa search term → Hiển thị lại tất cả jobs

#### 2. Test Category Filter
- [ ] Click "Development" → Chỉ hiển thị jobs category Development
- [ ] Click lại "Development" → Bỏ chọn, hiển thị tất cả

#### 3. Test Multiple Filters
- [ ] Chọn Category: "Design" + Location: "Hanoi" → Chỉ hiển thị jobs thỏa CẢ HAI điều kiện
- [ ] Click "Clear All" → Xóa tất cả filters

#### 4. Test Sort
- [ ] Chọn "Newest First" → Jobs mới nhất ở trên
- [ ] Chọn "Salary: High to Low" → Jobs lương cao nhất ở trên
- [ ] Chọn "Title A-Z" → Jobs sắp xếp theo alphabet

#### 5. Test No Results
- [ ] Chọn filters không có jobs nào thỏa mãn → Hiển thị "No jobs found" message

#### 6. Test Filter Count
- [ ] Không có filter → "Clear All (0)", button disabled
- [ ] Chọn 3 filters → "Clear All (3)", button enabled

#### 7. Test Responsive
- [ ] Desktop: Sidebar cố định bên trái
- [ ] Mobile: Click "Filters (X)" → Sidebar slide in từ trái

---

## Notes for Developers

### Parse Salary Logic
```javascript
// Xử lý chuỗi salary có dấu phẩy và ký tự đặc biệt
// Input: "$120,000 - $150,000"
// Output cho High to Low: 150000 (lấy số lớn nhất)
// Output cho Low to High: 120000 (lấy số nhỏ nhất)

const getMaxSalary = (salaryStr) => {
  const numbers = salaryStr.match(/\d+/g); // ["120", "000", "150", "000"]
  return numbers ? Math.max(...numbers.map(Number)) : 0; // 150000
};
```

### Filter Logic
```javascript
// Tất cả filter đều dùng AND logic (phải thỏa mãn TẤT CẢ)
const filteredJobs = jobs.filter(job => {
  return matchesSearch && matchesCategory && matchesLocation && matchesType && matchesExperience;
});
```

### Toggle Filter Behavior
```javascript
// Click vào filter đã chọn → Bỏ chọn
// Click vào filter chưa chọn → Chọn
setSelectedCategory(selectedCategory === value ? '' : value);
```

---

## Future Improvements (Đề xuất)

1. **Multiple Selection**: Cho phép chọn nhiều category/location cùng lúc
2. **Salary Range Slider**: Thanh trượt để chọn khoảng lương
3. **Date Range Filter**: Lọc theo khoảng thời gian đăng
4. **Save Filter Preferences**: Lưu filters vào localStorage
5. **URL Parameters**: Sync filters với URL để có thể share link
6. **Advanced Search**: Tìm kiếm nâng cao với boolean operators

---

## Troubleshooting

### Jobs không hiển thị sau khi filter
- **Nguyên nhân**: Filter quá strict, không có jobs nào thỏa mãn
- **Giải pháp**: Click "Clear All" và thử lại

### Sort không hoạt động
- **Nguyên nhân**: Dữ liệu `created_at` hoặc `salary` không đúng format
- **Giải pháp**: Kiểm tra data từ API, đảm bảo có field `created_at` (ISO date string)

### Filter count không đúng
- **Nguyên nhân**: State không được reset đúng cách
- **Giải pháp**: Kiểm tra hàm `clearFilters()` có reset hết tất cả state chưa

---

**Tác giả**: AI Assistant  
**Ngày tạo**: October 22, 2025  
**Version**: 1.0.0

