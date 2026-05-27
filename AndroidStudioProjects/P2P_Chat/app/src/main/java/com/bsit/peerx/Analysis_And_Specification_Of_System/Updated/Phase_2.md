# Phase 2 Implementation Report: Data Protocol & Multiplexing

## 1. Những gì đã được cập nhật (Updates)

### Data Layer
- **`ChatMessageEntity` & `ChatMessageModel`**: Thêm trường `packetId` (String) để định danh duy nhất mỗi gói tin và `status` (MessageStatus) để theo dõi vòng đời của tin nhắn.
- **`ChatMessageDao` & `ChatLocalDataSource`**: Thêm phương thức `updateStatus(packetId, status)` để cập nhật trạng thái tin nhắn (ví dụ: từ SENT sang DELIVERED) khi nhận được gói tin ACK từ đối phương.

### Transport Layer
- **`MessageTransport` Interface**: Nâng cấp từ việc truyền nhận `String` đơn thuần sang đối tượng `NetworkPacket` toàn diện. Thêm các phương thức `sendPacket()` và `incomingPackets()`.
- **`SocketMessageTransport`**:
    - Triển khai cơ chế **Auto-ACK**: Hệ thống tự động phản hồi gói tin `ACK_RECEIPT` ngay khi nhận được `TEXT` packet thành công.
    - Tích hợp logic xử lý đa luồng gói tin (`TEXT`, `ACK_RECEIPT`, `TYPING_STATUS`).
- **`InMemoryMessageTransport`**: Cập nhật để tương thích hoàn toàn với interface mới, đảm bảo việc test loopback cục bộ vẫn hoạt động chính xác.

### Domain Layer (Repository)
- **`ChatRepositoryImpl`**:
    - Chuyển sang thu thập dữ liệu từ `incomingPackets()` của Transport.
    - Khi nhận gói `ACK_RECEIPT`, repository sẽ gọi DAO để cập nhật trạng thái tin nhắn trong Room DB thành `DELIVERED`.
    - Quản lý luồng logic cho trạng thái "đang soạn thảo" (`Typing Status`).

### UI Layer
- **`ChatUiState`**: Thêm biến `isPeerTyping` để cập nhật giao diện khi đối phương đang gõ phím.
- **`ChatViewModel`**:
    - Triển khai logic **Debounce** (tạm dừng 3 giây): Tự động gửi lệnh "ngừng gõ" nếu người dùng không nhập thêm ký tự nào trong 3 giây.
    - Gửi trạng thái `isTyping = true` ngay khi có thay đổi trong ô nhập liệu.
- **`ChatScreen`**:
    - **TopBar**: Cập nhật subtitle thành "AGENT IS TYPING..." với màu sắc và hiệu ứng Neon đặc trưng khi có tín hiệu từ peer.
    - **MessageBubble**: Thay thế icon tĩnh bằng các logic hiển thị icon động (Check đơn cho Sent, Check màu Neon cho Delivered) dựa trên `MessageStatus`.

## 2. Các file và phương thức mới (New Files & Methods)

### File mới được tạo
- `app/src/main/java/com/bsit/p2p_chat/data/model/MessageStatus.kt`: Enum định nghĩa các trạng thái của tin nhắn: `NONE`, `SENDING`, `SENT`, `DELIVERED`, `READ`, `FAILED`.

### Phương thức mới/Quan trọng
- `ChatMessageDao.updateStatus(packetId, status)`: Query cập nhật trạng thái tin nhắn dựa trên ID gói tin.
- `MessageTransport.sendPacket(packet)`: Phương thức tổng quát để gửi bất kỳ loại dữ liệu nào đã được đóng gói.
- `ChatRepository.observeTypingStatus(peerAddress)`: Flow theo dõi trạng thái gõ phím của một peer cụ thể.
- `ChatViewModel.sendTypingStatus(isTyping)`: Hàm nội bộ để đóng gói và gửi packet trạng thái gõ phím.

## 3. Tại sao lại làm như vậy (Rationale)

- **Tính mở rộng (Extensibility)**: Việc chuẩn hóa luồng dữ liệu thông qua `NetworkPacket` giúp việc thêm các tính năng ở Phase 3 (Gửi file) và Phase 5 (WebRTC) trở nên dễ dàng hơn, vì Transport layer giờ đây có thể truyền tải bất kỳ loại payload JSON nào.
- **Độ tin cậy của tin nhắn (Message Reliability)**: Trong mạng P2P Wi-Fi Direct, kết nối có thể chập chờn. Cơ chế ACK và `MessageStatus` giúp người dùng biết rõ tin nhắn của mình đã thực sự đến máy đối phương hay chưa.
- **Trải nghiệm người dùng (UX)**: Typing indicator và các trạng thái tin nhắn (Sent/Delivered) là những tiêu chuẩn "vàng" của ứng dụng chat hiện đại, giúp ứng dụng cảm giác "alive" và chuyên nghiệp hơn.
- **Đúng tiến độ kiến trúc (Architectural Alignment)**: Tuân thủ chiến lược "Tầng dưới lên tầng trên" (Bottom-Up) đề ra trong tài liệu phân tích hệ thống. Việc xử lý xong Data Protocol ở Phase 2 là tiền đề bắt buộc trước khi chuyển sang xử lý các luồng dữ liệu nặng hơn như Media ở các Phase sau.
