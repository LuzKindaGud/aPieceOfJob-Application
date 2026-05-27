# Phase 3 Implementation Report: File Transfer

## 1. Những gì đã được cập nhật (Updates)

### Data Layer
- **`ChatMessageEntity` & `ChatMessageModel`**: Thêm trường `messageType` (TEXT, FILE) và `fileTransferId` để hỗ trợ phân loại tin nhắn và quản lý tiến trình gửi file.
- **`MessageType` Enum**: Định nghĩa các loại tin nhắn được hỗ trợ.

### Transport Layer
- **`FileTransferServer` (Port 8989)**: Lớp xử lý việc nhận file trên một socket riêng biệt. Sử dụng cơ chế chunking (8KB) và đóng gói vào `NetworkPacket` để đảm bảo tính nhất quán.
- **`FileTransferClient` (Port 8989)**: Lớp xử lý việc gửi file. Tự động chia nhỏ file thành các chunk và gửi kèm `fileTransferId`.
- **`MessageTransport`**: Thêm phương thức `getRemoteAddress()` để lấy IP của peer, hỗ trợ việc thiết lập kết nối socket cho file transfer.

### Domain Layer (Repository)
- **`ChatRepositoryImpl`**: 
    - Xử lý các gói tin `FILE_HEADER` để chuẩn bị nhận file.
    - Xử lý gói tin `FILE_READY` để bắt đầu gửi file khi đối phương đã sẵn sàng.
    - Quản lý tiến trình gửi/nhận file thông qua `_fileProgressMap` (StateFlow).
    - Tự động lưu file vào `MediaStore` (Thư mục Downloads) sau khi nhận hoàn tất.

### UI Layer
- **`ChatUiState`**: Thêm `fileProgress` map để theo dõi tiến trình của nhiều file cùng lúc.
- **`ChatViewModel`**: 
    - Cung cấp hàm `sendFile(uri)` để bắt đầu quy trình gửi file.
    - Tự động đăng ký observe progress khi có tin nhắn loại `FILE` xuất hiện.
- **`ChatScreen`**:
    - **File Picker**: Tích hợp `ActivityResultContracts.GetContent()` để người dùng chọn file từ máy.
    - **Message Bubbles**: Hiển thị `LinearProgressIndicator` cho các tin nhắn loại `FILE` đang trong quá trình truyền tải.
    - **Composer**: Thêm nút "+" để mở trình chọn file.

## 2. Các file và phương thức mới (New Files & Methods)

### File mới được tạo
- `app/src/main/java/com/bsit/p2p_chat/network/FileTransferServer.kt`
- `app/src/main/java/com/bsit/p2p_chat/network/FileTransferClient.kt`
- `app/src/main/java/com/bsit/p2p_chat/data/model/MessageType.kt`

### Phương thức mới/Quan trọng
- `ChatRepository.sendFile(peerAddress, uri)`: Khởi tạo quy trình gửi file (gửi Header và đợi Ready).
- `ChatRepository.observeFileProgress(fileId)`: Theo dõi tiến trình gửi/nhận file theo ID.
- `FileTransferServer.receiveFile(...)`: Chấp nhận kết nối và ghi dữ liệu chunk vào file tạm, sau đó lưu vào MediaStore.

## 3. Tại sao lại làm như vậy (Rationale)

- **Tránh nghẽn kênh điều khiển (Non-blocking Control Channel)**: Việc sử dụng Port 8989 riêng cho file transfer giúp kênh Port 8988 (tin nhắn, typing status, handshake) không bị nghẽn khi đang truyền tải file dung lượng lớn.
- **Tiết kiệm bộ nhớ (Memory Efficiency)**: Dữ liệu được đọc và ghi theo từng chunk 8KB thay vì load toàn bộ file vào RAM, tránh lỗi OutOfMemory (OOM).
- **Tuân thủ Scoped Storage**: Việc sử dụng `MediaStore` API đảm bảo ứng dụng hoạt động chính xác trên các phiên bản Android mới (10+) mà không cần quyền ghi bộ nhớ ngoài quá rộng.
- **Trải nghiệm người dùng mượt mà**: Tiến trình gửi file được hiển thị trực tiếp trên bong bóng tin nhắn, giúp người dùng dễ dàng theo dõi trạng thái.
