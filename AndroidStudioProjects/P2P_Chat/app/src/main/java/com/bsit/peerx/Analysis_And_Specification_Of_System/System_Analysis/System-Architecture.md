# P2P Chat (PeerX)

Mô tả: Ứng dụng chat ngang hàng (P2P) Android sử dụng Jetpack Compose, MVVM, Hilt và một lớp transport socket tùy chỉnh để trao đổi tin nhắn, file và tín hiệu cuộc gọi.

## Tổng quan cấu trúc

- app/
  - src/main/
    - java/com/bsit/PeerX/  — mã nguồn chính (UI, viewmodels, repository, network)
      - ui/                   — Compose UI và navigation (AppNavigation, screens)
      - viewmodel/           — ViewModel cho Discovery, Chat, v.v.
      - data/                — model, Room entities/DAOs
      - network/             — transport, FileTransfer*, CallManager
      - di/                  — cấu hình Hilt (AppModule.kt)
      - repository/          — ChatRepository / ChatRepositoryImpl
    - res/                   — layout, drawable, strings, themes
  - build.gradle

- gradle/, build.gradle (root), settings.gradle

## Thành phần chính & vị trí

- SocketMessageTransport.kt (network) — handshake 3-phase (pubkey exchange + challenge/response), trạng thái kết nối.
- ChatRepositoryImpl.kt (repository) — lưu tin nhắn (Room), gửi/nhận, xử lý ACK/READ, file transfer orchestration.
- FileTransferServer / FileTransferClient (network) — dùng port 8989 để truyền file (tách khỏi control channel).
- CallManager.kt (network) — stub cho signaling WebRTC (CALL_SDP, CALL_ICE_CANDIDATE).
- di/AppModule.kt — đăng ký các dependency (Hilt), ApplicationScope, transport, repository.

## Quy ước & kiến trúc

- Kiến trúc: MVVM + Flow (StateFlow / SharedFlow). UI dùng collectAsStateWithLifecycle hoặc LaunchedEffect cho event một lần.
- DI: Hilt. Long-lived CoroutineScope được cung cấp qua @ApplicationScope.
- Message lifecycle: Tin nhắn lưu local trước khi gửi. ACK_RECEIPT cập nhật trạng thái (DELIVERED / READ).
- Typing & presence: TYPING_STATUS packet, debounce 3s để gửi stop-typing.
- Group chat: gửi `groupId` trong payload và lưu với peerAddress = groupId.

## Port & transport

- Control channel (mặc định) — dùng cho packet điều khiển / chat (ví dụ: port 8988 trong code).
- File transfer — port 8989, gửi FILE_HEADER → receiver trả FILE_READY → truyền bytes.

## Lệnh build / test (Windows)

- Build debug APK: .\gradlew assembleDebug
- Unit tests: .\gradlew testDebugUnitTest
- Instrumented tests (device/emulator): .\gradlew connectedDebugAndroidTest
- Lint: .\gradlew lintDebug

## Tệp đáng chú ý khi debug

- app/src/main/java/com/bsit/PeerX/network/SocketMessageTransport.kt
- app/src/main/java/com/bsit/PeerX/repository/ChatRepositoryImpl.kt
- app/src/main/java/com/bsit/PeerX/di/AppModule.kt
- app/src/main/java/com/bsit/PeerX/ui/ (DiscoveryScreen, ChatScreen, MainActivity)
- app/src/main/java/com/bsit/PeerX/network/CallManager.kt

## Ghi chú cho người phát triển

- Khi thêm WebRTC: thêm dependency `org.webrtc:google-webrtc` và triển khai PeerConnection/PeerConnectionFactory trong CallManager.
- Khi truyền địa chỉ peer qua Navigation, dùng Uri.encode(peerAddress) để tránh lỗi với ký tự đặc biệt.

---

Nếu muốn mở rộng README (thêm API packet format, diagram handshake, hoặc hướng dẫn dev environment), báo để cập nhật.