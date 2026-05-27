# UI Description

Tài liệu này mô tả chi tiết hai màn hình chính của ứng dụng: **Discovery Screen** và **Chat Screen**.

---

## 1. Discovery Screen

### Mục đích
Discovery Screen là màn hình khởi đầu của ứng dụng. Nó dùng để:
- kiểm tra trạng thái Wi-Fi/Wi-Fi Direct,
- quét các peer lân cận,
- chuyển đổi giữa chế độ **P2P** và **Emulator/Test**,
- đặt tên hiển thị người dùng,
- kết nối tới một thiết bị khác hoặc vào nhóm chat.

### Bố cục giao diện

#### 1.1 Top Bar
Top Bar có thiết kế theo phong cách neon/cyber và chứa các thành phần chính:
- **Icon Profile**: mở hộp thoại đổi tên hiển thị (alias).
- **Nút Wi-Fi ON/OFF**: chỉ xuất hiện ở chế độ P2P, dùng để bật/tắt Wi-Fi.
- **Tiêu đề NEONCHAT**: tiêu đề trung tâm của màn hình.
- **Nút P2P / TEST**: chuyển đổi giữa hai chế độ hoạt động.

#### 1.2 Floating Action Button
Ở chế độ **P2P**, màn hình có nút nổi dạng hình tròn với biểu tượng `+`.
Nút này dùng để bắt đầu quét/tìm peer lân cận.

#### 1.3 Bottom Bar
Phần bottom bar là một thanh điều hướng trang trí ở cuối màn hình, tạo cảm giác dashboard/cyber nhất quán với toàn bộ UI.

### Chế độ P2P
Khi ở chế độ P2P, giao diện tập trung vào kết nối thiết bị thật qua Wi-Fi Direct:
- Ô nhập **group id** để Join/Create nhóm.
- Ô tìm kiếm để lọc peer theo:
  - tên thiết bị,
  - địa chỉ thiết bị.
- Danh sách peer hiển thị theo dạng card.

Mỗi peer card thường gồm:
- tên thiết bị,
- mô tả phụ,
- chấm trạng thái online,
- badge số lượng chưa đọc nếu có,
- vùng bấm để bắt đầu kết nối.

### Chế độ Emulator/Test
Khi đổi sang chế độ Emulator/Test, giao diện thay đổi để phục vụ kiểm thử kết nối thủ công:
- hiển thị **IP local** hiện tại,
- nút **Start Server**,
- ô nhập **target IP address**,
- nút **Connect to Server**.

Chế độ này hỗ trợ mô phỏng kết nối giữa hai thiết bị qua IP, thay cho discovery Wi-Fi Direct.

### Trạng thái phụ trợ
Discovery Screen có các thành phần trạng thái đi kèm:
- **ConnectingDialog**: hiển thị khi đang kết nối tới peer.
- **ProfileDialog**: cho phép đổi tên hiển thị.
- **Error message**: hiển thị khi quét hoặc kết nối thất bại.

### Dữ liệu và tương tác
Màn hình này lấy dữ liệu từ `DiscoveryViewModel`, bao gồm:
- trạng thái Wi-Fi,
- danh sách peer,
- mode hiện tại,
- lỗi discovery,
- trạng thái đang kết nối,
- tên hiển thị của người dùng,
- IP local khi ở chế độ emulator.

Người dùng có thể:
1. bật Wi-Fi,
2. quét peer,
3. lọc peer,
4. chọn peer để kết nối,
5. đổi mode,
6. đổi alias,
7. nhập IP để test thủ công.

---

## 2. Chat Screen

### Mục đích
Chat Screen là màn hình trao đổi tin nhắn giữa hai peer sau khi kết nối thành công. Màn hình này hỗ trợ:
- gửi và nhận tin nhắn văn bản,
- hiển thị trạng thái đang gõ,
- gửi file,
- hiển thị tiến trình file,
- gọi/hangup,
- hiển thị trạng thái tin nhắn.

### Bố cục giao diện

#### 2.1 Top Bar
Top Bar của chat có các phần sau:
- **Nút menu/back**: quay lại Discovery Screen.
- **Tên cuộc trò chuyện**: ưu tiên hiển thị `peerName`, nếu không có thì dùng `peerAddress`.
- **Trạng thái phụ**:
  - `AGENT IS TYPING...` khi peer đang gõ,
  - `SECURE CHANNEL ACTIVE` khi không có hoạt động typing.
- **Biểu tượng lock**: thể hiện kênh chat an toàn.
- **Nút Call / Hangup**:
  - hiện nút gọi khi chưa ở trong cuộc gọi,
  - hiện nút treo máy khi đang gọi.

#### 2.2 Vùng nội dung tin nhắn
Phần giữa màn hình là danh sách message:
- nếu chưa có tin nhắn: hiển thị trạng thái rỗng `SECURE CHANNEL STANDBY`,
- nếu đã có tin nhắn: hiển thị dạng bubble theo chiều dọc.

Tin nhắn được render theo 2 kiểu:
- **Tin nhắn đến**:
  - nằm bên trái,
  - có avatar/icon người gửi,
  - có nhãn tên người gửi,
  - bubble màu tối với viền nhấn màu neon.
- **Tin nhắn đi**:
  - nằm bên phải,
  - có nhãn tên của chính người dùng,
  - nền gradient neon,
  - hiển thị trạng thái gửi như sent/delivered/read/failed.

#### 2.3 Thanh Composer
Thanh nhập tin nhắn nằm ở cuối màn hình:
- nút **Add** để chọn/gửi file,
- ô nhập nội dung,
- icon emoji trang trí,
- nút **Send**.

Khi đang gửi, nút send sẽ chuyển sang trạng thái loading.

### Tính năng trạng thái
Chat Screen còn hiển thị các trạng thái phụ:
- **Error banner**: hiện lỗi gửi/nhận hoặc lỗi nghiệp vụ.
- **Typing indicator**: đổi subtitle ở top bar theo trạng thái peer đang gõ.
- **File progress bar**: hiển thị tiến trình với tin nhắn file.
- **Status icon**: cho tin nhắn đã gửi, đã nhận, đã đọc, hoặc thất bại.

### Dữ liệu và tương tác
Chat Screen nhận dữ liệu từ `ChatViewModel`, gồm:
- `peerAddress`,
- `peerName`,
- `ownName`,
- danh sách message,
- nội dung ô nhập,
- trạng thái đang gửi,
- trạng thái typing của peer,
- tiến trình file,
- trạng thái cuộc gọi,
- lỗi hiện tại.

Người dùng có thể:
1. nhập và gửi tin nhắn,
2. gửi file,
3. xem trạng thái tin nhắn,
4. theo dõi peer đang gõ,
5. bắt đầu hoặc kết thúc cuộc gọi,
6. quay lại màn hình discovery.

### Quy tắc hiển thị
- Tin nhắn được sắp xếp theo thời gian.
- Danh sách chat cuộn từ dưới lên để ưu tiên tin mới.
- Tin nhắn gửi đi luôn hiển thị trạng thái để phản ánh tình trạng mạng.
- File có thể hiển thị progress ngay trong bubble.

---

## 3. Tổng kết luồng UI

Luồng cơ bản của ứng dụng:
1. Người dùng mở **Discovery Screen**.
2. Quét hoặc chọn thiết bị peer.
3. Điều hướng sang **Chat Screen**.
4. Gửi/nhận tin nhắn, file, và trạng thái cuộc gọi.

Hai màn hình này là trung tâm của trải nghiệm P2P trong ứng dụng.
