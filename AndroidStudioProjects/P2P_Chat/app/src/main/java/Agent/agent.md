# agent.md — P2P Chat Android System (Wi-Fi Direct)

> **Project:** Chat P2P Through Wi-Fi Direct  
> **Platform:** Android (API 29+)  
> **Language:** Kotlin  
> **IDE:** Android Studio  
> **Student:** Huỳnh Kim Thống — MSV: 24ITE103 — Class: 24GITe  
> **Supervisor:** ThS. Mai Lam  

---

## 1. Project Overview

This is an Android peer-to-peer messaging application that allows two or more mobile devices to connect and exchange data **directly** without any intermediate Access Point or Internet connection. The core networking technology is **Wi-Fi Direct (Wi-Fi P2P)**.

The project emphasizes modern Android architecture and libraries:
- **Kotlin** — primary language
- **Jetpack Compose** — declarative UI (Material Design 3)
- **MVVM Architecture** — with clear layer separation
- **Room (SQLite)** — local persistence
- **Hilt** — dependency injection
- **Kotlin Coroutines / StateFlow** — async operations and reactive state

---

## 2. System Architecture

The system is structured into **3 main layers** following MVVM:

```
┌──────────────────────────────────────────────┐
│              UI LAYER (Jetpack Compose)       │
│  DiscoveryScreen │ ChatScreen │ HistoryScreen │
└────────────────────┬─────────────────────────┘
                     │ User Events / UI State (StateFlow)
┌────────────────────▼─────────────────────────┐
│           VIEWMODEL LAYER                     │
│  DiscoveryViewModel │ ChatViewModel           │
│  (viewModelScope + Hilt injection)            │
└────────────────────┬─────────────────────────┘
                     │ Repository calls
┌────────────────────▼─────────────────────────┐
│              DATA LAYER                       │
│  ┌─────────────────┐  ┌─────────────────────┐│
│  │  Room Database  │  │  Wi-Fi Direct       ││
│  │  (SQLite)       │  │  Network Service    ││
│  │  ChatDao        │  │  WifiDirectManager  ││
│  │  UserEntity     │  │  SocketHandler      ││
│  │  MessageEntity  │  │  (ServerSocket /    ││
│  └─────────────────┘  │   Socket / IO)      ││
│                        └─────────────────────┘│
└──────────────────────────────────────────────┘
```

**Repository Pattern** (`ChatRepository`) acts as the single source of truth, deciding whether data comes from the local DB or the network.

---

## 3. Database Schema (Room)

### Table: `users`
| Column       | Type    | Constraint  | Description                        |
|--------------|---------|-------------|------------------------------------|
| macAddress   | TEXT    | PRIMARY KEY | MAC address of the peer device     |
| deviceName   | TEXT    |             | Human-readable device name         |
| lastSeen     | INTEGER |             | Unix timestamp of last connection  |

### Table: `messages`
| Column         | Type            | Constraint                        | Description                              |
|----------------|-----------------|-----------------------------------|------------------------------------------|
| messageId      | TEXT            | PRIMARY KEY                       | UUID of the message                      |
| peerMacAddress | TEXT            | FOREIGN KEY → users.macAddress    | Identifies which conversation            |
| content        | TEXT            |             | Message text or file URI                |
| timestamp      | INTEGER         |             | Unix timestamp of send/receive           |
| isMine         | INTEGER (0/1)   |             | 1 = sent by me, 0 = received            |
| type           | TEXT            |             | `"TEXT"` \| `"IMAGE"` \| `"FILE"`       |

---

## 4. Functional Requirements

| ID     | Requirement                                                                 |
|--------|-----------------------------------------------------------------------------|
| FR1.1  | Toggle Wi-Fi Direct on/off                                                  |
| FR1.2  | Scan and discover nearby peer devices                                       |
| FR1.3  | Display list of discovered devices                                          |
| FR1.4  | Send a connection invitation to a selected device                           |
| FR1.5  | Accept or decline an incoming connection request                            |
| FR1.6  | Disconnect the current P2P session                                          |
| FR2.1  | Provide 1-to-1 chat UI after connection                                     |
| FR2.2  | Compose and send text messages                                              |
| FR2.3  | Receive and display incoming messages in real-time                          |
| FR2.4  | Send and receive files (images, documents, any file)                        |
| FR2.5  | Show send/receive file progress indicator                                   |
| FR3.1  | Auto-save all messages (sent & received) to local Room DB                  |
| FR3.2  | Persist info of previously connected devices (P2P contact book)             |
| FR3.3  | Load and display conversation history on app launch                         |

---

## 5. Non-Functional Requirements

| ID   | Category           | Requirement                                                                           |
|------|--------------------|---------------------------------------------------------------------------------------|
| NFR1 | Platform           | Android API level 29 (Android 10) and above                                           |
| NFR2 | Architecture       | MVVM + Hilt DI — clean, testable, maintainable code structure                        |
| NFR3 | Performance        | Message latency < 1 second under ideal conditions; scanning must not block the UI     |
| NFR4 | UI/UX              | Jetpack Compose + Material Design 3 — intuitive and responsive                       |
| NFR5 | Async              | All network and DB operations run on background threads via Kotlin Coroutines         |
| NFR6 | Security           | Connection encrypted by Wi-Fi Direct's built-in WPA2 mechanism                       |

---

## 6. Use Cases

### UC-01 — Scan for Peers
- **Actor:** User
- **Preconditions:** App launched; Location & Wi-Fi permissions granted; Wi-Fi hardware on
- **Main Flow:**
  1. User taps "Scan" (or auto-scan on launch)
  2. System calls Wi-Fi Direct peer discovery API
  3. System listens for peer events
  4. UI updates with list of found devices and their names
- **Exceptions:**
  - No devices found → show "No devices nearby" message
  - Wi-Fi off → prompt user to enable Wi-Fi

### UC-02 — Connect to Device
- **Actor:** User (both Initiator and Recipient sides)
- **Preconditions:** UC-01 completed with ≥1 device found; both devices ready
- **Main Flow:**
  1. Initiator selects a device from the list
  2. System sends P2P connection request
  3. Recipient sees dialog with sender's device name
  4. Recipient taps "Accept"
  5. Both devices confirm connection; Group Owner / Client roles assigned
  6. Both apps navigate to Chat Screen
- **Exceptions:**
  - Recipient declines → connection cancelled; initiator sees "Request declined"
  - Device unreachable → show "Could not connect to device"

### UC-03 — Text Chat
- **Actor:** User (both devices)
- **Preconditions:** UC-02 completed; active P2P connection; on Chat Screen
- **Send Flow:**
  1. User types message in input field
  2. User taps "Send"
  3. System packages message (content + timestamp + sender info)
  4. System writes data to Socket's OutputStream
  5. Message displayed on sender's screen (right-aligned)
  6. Message saved to Room DB with `isMine = true`
- **Receive Flow:**
  1. System listens continuously on Socket's InputStream
  2. Incoming bytes deserialized to `MessageEntity`
  3. Message displayed (left-aligned)
  4. Message saved with `isMine = false`
- **Exception:** Socket disconnected mid-send → mark message as "Failed" on UI

### UC-04 — Disconnect
- **Actor:** User
- **Preconditions:** Active P2P connection exists
- **Main Flow:**
  1. User taps "Disconnect"
  2. System calls Wi-Fi Direct API to remove P2P group
  3. Socket closed
  4. Both apps receive disconnect event and navigate back to Discovery Screen

---

## 7. Message Send Sequence (Simplified)

```
User → ChatScreen.sendMessage("Hello")
     → ChatViewModel.sendMessage()       [creates MessageEntity]
     → ChatRepository.sendMessage()
          ├─→ SocketHandler.sendData()   [serialize → ByteArray → OutputStream]
          └─→ ChatDao.insertMessage()    [INSERT INTO messages]

[Receiver side]
SocketHandler.onReceive()               [InputStream → deserialize → MessageEntity]
     → ChatRepository.onMessageReceived()
          └─→ ChatDao.insertMessage()   [INSERT INTO messages]

[Both sides]
ChatDao emits Flow<List<MessageEntity>> on new INSERT
     → ChatViewModel collects Flow → updates UI State
     → ChatScreen recomposes LazyColumn with new message
```

---

## 8. Key Classes & Responsibilities

| Class / File              | Layer       | Responsibility                                                         |
|---------------------------|-------------|------------------------------------------------------------------------|
| `DiscoveryScreen.kt`      | UI          | Display peer list, trigger scan, initiate connection                   |
| `ChatScreen.kt`           | UI          | Message list (LazyColumn), input field, send button                    |
| `HistoryScreen.kt`        | UI          | Show list of past conversations from Room DB                           |
| `DiscoveryViewModel.kt`   | ViewModel   | Handle scan/connect events; expose peer list as StateFlow              |
| `ChatViewModel.kt`        | ViewModel   | Send/receive messages; expose message list as StateFlow                |
| `ChatRepository.kt`       | Repository  | Coordinate between SocketHandler and ChatDao                           |
| `WifiDirectManager.kt`    | Data/Ntwk   | Singleton; manage full Wi-Fi Direct lifecycle (init, scan, connect)    |
| `SocketHandler.kt`        | Data/Ntwk   | Manage ServerSocket/Socket; read/write byte streams                    |
| `AppDatabase.kt`          | Data/DB     | Room database; connects entities and DAOs                              |
| `ChatDao.kt`              | Data/DB     | SQL queries: `@Insert`, `@Query` for messages                          |
| `UserEntity.kt`           | Data/DB     | Room entity for `users` table                                          |
| `MessageEntity.kt`        | Data/DB     | Room entity for `messages` table                                       |
| `AppModule.kt`            | DI (Hilt)   | Provides singleton instances of DB, DAOs, repositories                 |

---

## 9. Permissions Required (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.NEARBY_WIFI_DEVICES"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.INTERNET" />
```

> On Android 13+ (API 33), request `NEARBY_WIFI_DEVICES` at runtime instead of `ACCESS_FINE_LOCATION`.

---

## 10. Tech Stack Summary

| Category        | Technology                         |
|-----------------|------------------------------------|
| Language        | Kotlin                             |
| UI              | Jetpack Compose + Material 3       |
| Architecture    | MVVM                               |
| DI              | Hilt                               |
| Database        | Room (SQLite)                      |
| Async           | Kotlin Coroutines + StateFlow      |
| Networking      | Wi-Fi Direct API + Java Sockets    |
| Min SDK         | 29 (Android 10)                    |
| Build System    | Gradle (Kotlin DSL)                |

---

## 11. AI Agent Prompt

See **`PROMPT.md`** for the full system prompt to use with AI coding assistants (Claude, Copilot, Gemini, etc.) when working on this project.
