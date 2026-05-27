# Tài Liệu Đặc Tả Hệ Thống — P2P Chat App

## Tổng quan

Bộ tài liệu này mô tả toàn bộ kiến trúc và các chức năng của **P2P Chat App** — ứng dụng nhắn tin ngang hàng (peer-to-peer) trên Android, sử dụng Wi-Fi Direct, xác thực mã hóa và giao thức socket tùy chỉnh.

---

## Danh sách tài liệu

### Nền tảng & Kiến trúc

| Mã UC | Tên tài liệu | File |
|---|---|---|
| UC-ARC-01 | Kiến trúc ứng dụng (MVVM + Flow) | `Architecture.md` |
| UC-DI-01 | Dependency Injection (Hilt) | `Dependency_Injection.md` |
| UC-REPO-01 | Điều phối dữ liệu (ChatRepository) | `ChatRepository.md` |
| UC-ERR-01 | Xử lý lỗi và tự kết nối lại | `Error_Handling_and_Reconnect.md` |

### Kết nối & Xác thực

| Mã UC | Tên tài liệu | File |
|---|---|---|
| UC-DISC-01 | Khám phá peer (Wi-Fi Direct) | `Discovery.md` |
| UC-NAV-01 | Điều hướng màn hình | `Navigation.md` |
| UC-CONN-01 | Quản lý trạng thái kết nối | `Connection_Status.md` |
| UC-HAND-01 | Bắt tay và xác thực | `Handshake_and_Auth.md` |

### Nhắn tin

| Mã UC | Tên tài liệu | File |
|---|---|---|
| UC-CHAT-01 | Chat 1:1 | `Chat_1to1.md` |
| UC-ACK-01 | Biên nhận tin nhắn (ACK) | `ACK_Receipt.md` |
| UC-TYPE-01 | Trạng thái đang nhập | `Typing_Indicator.md` |
| UC-GROUP-01 | Chat nhóm | `Group_Chat.md` |

### Tính năng nâng cao

| Mã UC | Tên tài liệu | File |
|---|---|---|
| UC-FILE-01 | Truyền file | `File_Transfer.md` |
| UC-CALL-01 | Báo hiệu cuộc gọi | `Call_Signaling.md` |

---

## Luồng tổng quan hệ thống

```
[Discovery] ──► [Navigation] ──► [Handshake & Auth]
                                        │
                                        ▼
                              [Connection Status]
                                        │
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
                [Chat 1:1]        [Group Chat]       [File Transfer]
                    │
           ┌────────┴────────┐
           ▼                 ▼
      [ACK Receipt]   [Typing Indicator]

                    [Call Signaling]

                          │ (tất cả đi qua)
                          ▼
                   [ChatRepository]
                          │
                    [Error Handling & Reconnect]

Nền tảng: [Architecture] · [Dependency Injection]
```

---

## Cổng mạng

| Cổng | Kênh | Packet types |
|---|---|---|
| `8988` | Control channel | `TEXT`, `ACK_RECEIPT`, `TYPING_STATUS`, `FILE_HEADER`, `FILE_COMPLETE`, `HELLO`, `CHALLENGE`, `RESPONSE`, `CALL_SDP`, `CALL_ICE_CANDIDATE` |
| `8989` | Data channel | `FILE_CHUNK` (stream bytes thực tế) |

---

## Trạng thái kết nối

```
DISCONNECTED → CONNECTING → CONNECTED → CONNECTED_AND_VERIFIED
                                    └──► AUTH_FAILED
```

---

> **Quy ước tài liệu:** Mỗi file đặc tả bao gồm mô tả tổng quan, endpoints/ports, payload JSON mẫu, sơ đồ luồng và bảng đặc tả use case đầy đủ bằng tiếng Việt.
