# P2P Wi-Fi Direct Chat — Copilot Agent Instructions

## Project Overview

This is an **Android P2P chat application** written in **Kotlin** using **Jetpack Compose**, communicating over **Wi-Fi Direct** (no internet required). The app follows **Clean Architecture** with layered separation: UI → ViewModel → Repository → DataSource → Manager.

### What is already DONE (do NOT rewrite):
- Wi-Fi toggle via `WifiDirectManager` + `WiFiDirectBroadcastReceiver`
- Peer discovery (`discoverPeers`) and peer list display in `DiscoveryScreen`
- Device connection (`manager.connect()`, `WifiP2pConfig`)
- Socket establishment after `groupFormed == true` (basic `PrintWriter`/`BufferedReader` over port 8988)
- Simple text send saved to **Room DB** (`ChatLocalDataSource`)
- Basic `ChatScreen` using Jetpack Compose
- Navigation from `DiscoveryScreen` → `ChatScreen`

### Core Architecture Components (existing — reference these names exactly):
| Component | Role |
|---|---|
| `WifiDirectManager` | Manages `WifiP2pManager`, holds `StateFlow`s for wifi/peer/connection state |
| `WiFiDirectBroadcastReceiver` | Receives system P2P broadcasts, fires callbacks |
| `SocketMessageTransport` | Opens/reads/writes the socket; currently uses `PrintWriter`/`BufferedReader` |
| `PeerDiscoveryDataSource` | Abstraction layer over `WifiDirectManager` for discovery |
| `ChatLocalDataSource` | Room DB DAOs for messages and peers |
| `ChatRepository` | Orchestrates socket transport + DB persistence |
| `DiscoveryViewModel` | UI state + events for `DiscoveryScreen` |
| `ChatViewModel` | UI state + events for `ChatScreen` |
| `PeerEntity` | Room entity — currently uses MAC address as primary key |
| `MessageEntity` | Room entity for chat messages |

---

## Implementation Roadmap — Build in Strict Phase Order

> **Rule:** Never skip phases. Phase 1 and 2 are structural foundations. Building media or group chat before Phase 2 is complete will require a full rewrite.

---

## PHASE 1 — Security Foundation & Local Identity

### 1.1 — App Lock (Biometric Gate)

**Goal:** Wrap the entire `NavHost` in a biometric lock that triggers after the app is backgrounded beyond a configurable timeout.

**Dependencies to add in `build.gradle.kts`:**
```kotlin
implementation("androidx.biometric:biometric:1.2.0-alpha05")
implementation("androidx.lifecycle:lifecycle-process:2.8.0")
```

**Steps:**

1. **Create `AppLockManager.kt`** (singleton, inject via Hilt):
   - Hold `val isLocked: MutableStateFlow<Boolean>` initialized to `true`.
   - Register a `DefaultLifecycleObserver` on `ProcessLifecycleOwner.get().lifecycle`.
   - In `onStop`: record `backgroundTimestamp = SystemClock.elapsedRealtime()`.
   - In `onStart`: if `elapsedRealtime() - backgroundTimestamp > LOCK_TIMEOUT_MS` (default 30_000), set `isLocked.value = true`.

2. **Create `LockScreen.kt`** (Composable):
   - Full-screen opaque overlay. Show app logo + single button "Unlock".
   - On button click, call `BiometricPrompt` (see below).
   - Intercept back press (`BackHandler`) to consume it without action.

3. **Wire `BiometricPrompt`** inside `LockScreen`:
   ```kotlin
   val executor = ContextCompat.getMainExecutor(context)
   val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
       override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
           appLockManager.isLocked.value = false
       }
   })
   val info = BiometricPrompt.PromptInfo.Builder()
       .setTitle("Unlock P2P Chat")
       .setAllowedAuthenticators(
           BiometricManager.Authenticators.BIOMETRIC_STRONG or
           BiometricManager.Authenticators.DEVICE_CREDENTIAL
       ).build()
   prompt.authenticate(info)
   ```

4. **Wrap `NavHost` in `MainActivity.kt`**:
   ```kotlin
   val isLocked by appLockManager.isLocked.collectAsStateWithLifecycle()
   if (isLocked) {
       LockScreen(onUnlocked = { appLockManager.isLocked.value = false })
   } else {
       AppNavHost(navController)
   }
   ```

---

### 1.2 — Local Identity (Key Pair Generation)

**Goal:** Replace MAC address as peer ID with a hash of an EC public key stored in Android Keystore.

**Dependencies to add:**
```kotlin
implementation("com.google.crypto.tink:tink-android:1.13.0")
```

**Steps:**

1. **Create `KeyStoreManager.kt`**:
   - Use `KeyPairGenerator` with `KeyProperties.KEY_ALGORITHM_EC` and `KeyProperties.PURPOSE_SIGN or PURPOSE_VERIFY`.
   - Key alias: `"p2p_chat_identity_key"`.
   - Store inside `AndroidKeyStore` with `setUserAuthenticationRequired(false)`.
   - `fun getOrCreateKeyPair(): KeyPair` — generate only once; return existing on subsequent calls.
   - `fun getPublicKeyBytes(): ByteArray` — returns `keyPair.public.encoded`.
   - `fun getPeerIdHash(): String` — returns `Base64.encodeToString(SHA-256(publicKeyBytes), NO_WRAP)`. This is the stable peer ID.
   - `fun sign(data: ByteArray): ByteArray` — uses `Signature.getInstance("SHA256withECDSA")` with the private key from Keystore.
   - `fun verify(publicKeyBytes: ByteArray, data: ByteArray, signature: ByteArray): Boolean`.

2. **Migrate `PeerEntity`**:
   ```kotlin
   @Entity(tableName = "peers")
   data class PeerEntity(
       @PrimaryKey val peerId: String,      // SHA-256 hash of their public key (Base64)
       val publicKeyBase64: String,          // their raw public key bytes (Base64)
       val displayName: String,
       val avatarHash: String?,              // SHA-256 of avatar bytes, nullable
       val macAddress: String,              // still store for WifiP2pConfig, but NOT primary key
       val firstSeenAt: Long,
       val lastSeenAt: Long
   )
   ```

3. Add a **Room migration** (`Migration(1, 2)`) to drop the old MAC-keyed peer table and create the new schema.

4. Update `ChatRepository` to use `KeyStoreManager.getPeerIdHash()` as `senderId` in every outgoing `MessageEntity`.

---

### 1.3 — Cryptographic Handshake

**Goal:** After `groupFormed == true` and socket connects, perform a 3-phase mutual authentication before allowing `ChatScreen`.

**Handshake Packet format** (use within the new framing protocol from Phase 2):
```
PacketType: AUTH_PUBKEY_EXCHANGE | AUTH_CHALLENGE | AUTH_RESPONSE | AUTH_RESULT
```

**Modify `SocketMessageTransport`:**

1. After `socket.accept()` (server side) or `socket.connect()` (client side), immediately call `performHandshake(socket, isGroupOwner)` before exposing any message flow.

2. **`performHandshake` logic:**

   **Phase 1 — Exchange Public Keys & Profile:**
   ```
   Both sides send: { type: AUTH_PUBKEY_EXCHANGE, publicKey: Base64, displayName: String, avatarHash: String? }
   Both sides receive and store the remote public key.
   ```

   **Phase 2 — Client challenges Group Owner:**
   ```
   Client sends: { type: AUTH_CHALLENGE, nonce: Base64(random 32 bytes) }
   GO signs nonce with its private key.
   GO sends: { type: AUTH_RESPONSE, signature: Base64 }
   Client verifies signature against GO's public key.
   ```

   **Phase 3 — GO challenges Client back:**
   ```
   GO sends: { type: AUTH_CHALLENGE, nonce: Base64(random 32 bytes) }
   Client signs nonce.
   Client sends: { type: AUTH_RESPONSE, signature: Base64 }
   GO verifies.
   ```

   **On success:**
   - Emit `ConnectionStatus.CONNECTED_AND_VERIFIED`.
   - Save peer's public key + peerId to `PeerEntity` in Room DB.
   - Allow `ChatViewModel` to navigate to `ChatScreen`.

   **On failure:**
   - Close the socket immediately.
   - Emit `ConnectionStatus.AUTH_FAILED`.

---

## PHASE 2 — Data Protocol & Multiplexed Transport

### 2.1 — Length-Prefix Packet Framing

**Goal:** Replace `PrintWriter`/`BufferedReader` with `DataOutputStream`/`DataInputStream` and a proper length-prefix framing to prevent TCP fragmentation bugs.

**Replace in `SocketMessageTransport`:**
```kotlin
// REMOVE:
PrintWriter(socket.getOutputStream(), true)
BufferedReader(InputStreamReader(socket.getInputStream()))

// ADD:
val dos = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
val dis = DataInputStream(BufferedInputStream(socket.getInputStream()))
```

**Write packet:**
```kotlin
fun writePacket(dos: DataOutputStream, payloadBytes: ByteArray) {
    dos.writeInt(payloadBytes.size)   // 4-byte length prefix
    dos.write(payloadBytes)
    dos.flush()
}
```

**Read packet:**
```kotlin
fun readPacket(dis: DataInputStream): ByteArray {
    val length = dis.readInt()         // blocks until 4 bytes arrive
    val buffer = ByteArray(length)
    dis.readFully(buffer)              // blocks until exactly `length` bytes arrive
    return buffer
}
```

---

### 2.2 — Unified Packet Model (JSON-encoded payload)

**Create `NetworkPacket.kt`:**
```kotlin
enum class PacketType {
    TEXT,
    ACK_RECEIPT,
    TYPING_STATUS,
    PROFILE_EXCHANGE,
    AUTH_PUBKEY_EXCHANGE,
    AUTH_CHALLENGE,
    AUTH_RESPONSE,
    FILE_HEADER,
    FILE_CHUNK,
    FILE_COMPLETE,
    CALL_SDP,
    CALL_ICE_CANDIDATE,
    CALL_HANGUP
}

@Serializable
data class NetworkPacket(
    val type: PacketType,
    val senderId: String,         // SHA-256(publicKey) of sender
    val packetId: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val payload: JsonElement      // type-specific content
)
```

**Serialize with `kotlinx.serialization`:**
```kotlin
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
```

**Payload schemas per type:**
- `TEXT`: `{ "messageId": String, "text": String }`
- `ACK_RECEIPT`: `{ "messageId": String, "status": "DELIVERED" | "READ" }`
- `TYPING_STATUS`: `{ "isTyping": Boolean }`
- `PROFILE_EXCHANGE`: `{ "displayName": String, "avatarHash": String?, "avatarBase64": String? }`
- `FILE_HEADER`: `{ "fileTransferId": String, "fileName": String, "mimeType": String, "totalSize": Long, "totalChunks": Int }`
- `FILE_CHUNK`: `{ "fileTransferId": String, "chunkIndex": Int, "data": Base64String }`
- `FILE_COMPLETE`: `{ "fileTransferId": String }`

---

### 2.3 — ACK Receipt & Message Status

**`MessageEntity` status field** (update enum):
```kotlin
enum class MessageStatus { SENDING, SENT, DELIVERED, READ, FAILED }
```

**In `SocketMessageTransport` receive loop:**
1. On receiving a `TEXT` packet → save to DB with `status = DELIVERED` → immediately send back an `ACK_RECEIPT` packet with `status = "DELIVERED"` and the original `messageId`.
2. On receiving an `ACK_RECEIPT` packet → call `chatLocalDataSource.updateMessageStatus(messageId, status)`. Do NOT treat this as a new message. Guard against ACK loops by checking `packet.senderId != localPeerId`.

**In `ChatViewModel`:**
- When `ChatScreen` enters composition (becomes visible), send `ACK_RECEIPT` with `status = "READ"` for all unread messages from the current peer.

---

### 2.4 — Typing Indicator

**In `ChatViewModel`:**
```kotlin
private var typingDebounceJob: Job? = null

fun onTextInputChanged(text: String) {
    typingDebounceJob?.cancel()
    if (text.isNotEmpty()) {
        sendTypingStatus(true)
        typingDebounceJob = viewModelScope.launch {
            delay(2000)
            sendTypingStatus(false)
        }
    } else {
        sendTypingStatus(false)
    }
}
```

**`ChatUiState`:** add `val isPeerTyping: Boolean = false`.

**In receive loop:** on `TYPING_STATUS` packet → update `_uiState`.

---

## PHASE 3 — File Transfer

### 3.1 — Dedicated File Transfer Socket (Port 8989)

**Create `FileTransferServer.kt`** and **`FileTransferClient.kt`**:
- `FileTransferServer` opens `ServerSocket(8989)` and accepts one connection per transfer.
- `FileTransferClient` connects to `Socket(groupOwnerIp, 8989)`.
- Both use the same length-prefix framing from Phase 2.

The **control channel** (port 8988) sends `FILE_HEADER` to announce an incoming transfer. The receiver then opens `FileTransferServer` and signals readiness. The sender opens `FileTransferClient` and begins streaming chunks.

### 3.2 — Chunked File Streaming

**Sender side:**
```kotlin
fun sendFile(uri: Uri, fileTransferId: String, socket: Socket) {
    val CHUNK_SIZE = 8 * 1024  // 8KB
    val inputStream = context.contentResolver.openInputStream(uri)!!
    var chunkIndex = 0
    val buffer = ByteArray(CHUNK_SIZE)
    var bytesRead: Int
    var totalBytesSent = 0L

    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        val chunkData = buffer.copyOf(bytesRead)
        val packet = buildFileChunkPacket(fileTransferId, chunkIndex, chunkData)
        writePacket(dos, packet)
        totalBytesSent += bytesRead
        chunkIndex++
        _transferProgress.value = totalBytesSent.toFloat() / totalFileSize
    }
    writePacket(dos, buildFileCompletePacket(fileTransferId))
}
```
> **CRITICAL:** Never load the entire file into `ByteArray` at once. Always stream in chunks to prevent OOM on large files.

**Receiver side:**
- Buffer chunks in a `FileOutputStream` to app's `cacheDir`.
- On `FILE_COMPLETE`, move the file to **Scoped Storage** via `MediaStore` API (required for Android 10+):
```kotlin
val values = ContentValues().apply {
    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
}
val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
```

### 3.3 — Progress UI

In `ChatScreen`, for messages of type `FILE`, show a `LinearProgressIndicator`:
```kotlin
LinearProgressIndicator(
    progress = { transferProgress },
    modifier = Modifier.fillMaxWidth()
)
```
Bind to `StateFlow<Float>` per `fileTransferId` in `ChatViewModel`.

---

## PHASE 4 — Background Service & End-to-End Encryption

### 4.1 — Foreground Service

**Create `P2PChatService.kt`** extending `Service`:

```kotlin
class P2PChatService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        // Move WifiDirectManager and SocketMessageTransport lifecycle here
        return START_STICKY
    }
}
```

**`AndroidManifest.xml`** — required for Android 14+ (API 34):
```xml
<service
    android:name=".service.P2PChatService"
    android:foregroundServiceType="dataSync|connectedDevice"
    android:exported="false" />

<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
```

**Local notification on incoming message (while backgrounded):**
```kotlin
val notification = NotificationCompat.Builder(context, CHANNEL_ID)
    .setContentTitle(senderName)
    .setContentText(messagePreview)
    .setSmallIcon(R.drawable.ic_message)
    .setAutoCancel(true)
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .build()
NotificationManagerCompat.from(context).notify(notificationId, notification)
```

---

### 4.2 — End-to-End Encryption (E2EE)

**Goal:** All `TEXT` and `FILE` payloads encrypted with AES-256-GCM using a session key derived from ECDH.

**Steps using Google Tink:**
```kotlin
implementation("com.google.crypto.tink:tink-android:1.13.0")
```

1. **Extend `KeyStoreManager`** with an ECDH key pair (separate from the signing key):
   ```kotlin
   fun getECDHPublicKey(): ByteArray   // sent during handshake Phase 1
   fun computeSharedSecret(remotePubKey: ByteArray): ByteArray  // ECDH key agreement
   ```

2. **Derive session AES key** after handshake completes:
   ```kotlin
   val sharedSecret = keyStoreManager.computeSharedSecret(remotePubKey)
   val sessionKey = HKDF.computeHkdf("HmacSHA256", sharedSecret, salt, info, 32)
   ```

3. **Encrypt before `writePacket`:**
   ```kotlin
   val aead = AesGcmJce(sessionKey)
   val ciphertext = aead.encrypt(plaintext, associatedData)
   ```

4. **Decrypt after `readPacket`:**
   ```kotlin
   val plaintext = aead.decrypt(ciphertext, associatedData)
   ```

5. **Session key rotation:** On reconnect, always re-derive a fresh session key. Store `sessionKey` only in memory (never in Room DB or SharedPreferences).

> **WARNING:** If a device reconnects and derives a new session key while the peer still uses the old one, decryption will throw. Handle `AEADBadTagException` → trigger re-handshake.

---

## PHASE 5 — Group Chat & WebRTC (Advanced)

### 5.1 — Group Chat (GO as Router)

**On the Group Owner device**, after `SocketMessageTransport` setup:

1. Maintain `val connectedClients: MutableMap<String, Socket>` — keyed by `peerId`.
2. When a `TEXT` packet arrives from `Client A`:
   - Save it to local DB.
   - Iterate `connectedClients` and call `writePacket()` for every entry **except** `Client A`'s socket.
3. **`GroupMemberScreen`** (new screen): list all `connectedClients.keys` mapped to display names from `PeerEntity`.
4. Handle GO disconnect: on client side, if socket closes, re-trigger `discoverPeers()` and display "Reconnecting..." state in UI.

---

### 5.2 — WebRTC Voice/Video Call

**Dependency:**
```kotlin
implementation("io.getstream:stream-webrtc-android:1.1.4")
```

**Signaling via existing control socket (port 8988):**
- Use packet types `CALL_SDP` and `CALL_ICE_CANDIDATE` to exchange WebRTC session parameters.
- No STUN/TURN server needed — peers are already on the same LAN.

**Flow:**
1. Caller sends `CALL_SDP` (`{ "sdp": sdpString, "type": "offer" }`).
2. Callee displays incoming call UI.
3. Callee sends `CALL_SDP` (`{ "sdp": sdpString, "type": "answer" }`).
4. Both sides exchange `CALL_ICE_CANDIDATE` packets.
5. WebRTC establishes direct audio/video stream.
6. Either side sends `CALL_HANGUP` to terminate.

**Permissions required:**
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

**Memory safety:** Always call `peerConnection.dispose()` and `localVideoTrack.dispose()` when call ends or socket disconnects.

---

## General Coding Rules (apply to ALL phases)

### Threading
- Socket read loops run on `Dispatchers.IO`.
- All Room DB writes use `Dispatchers.IO`.
- UI state updates use `Dispatchers.Main`.
- Use `Mutex` from `kotlinx.coroutines` to guard concurrent DB writes:
  ```kotlin
  private val dbMutex = Mutex()
  dbMutex.withLock { chatLocalDataSource.insertMessage(entity) }
  ```

### Error Handling
- Wrap every `socket.read()` in `try/catch(IOException)`. On exception → emit `ConnectionStatus.DISCONNECTED` and attempt socket reconnect if `ForegroundService` is alive.
- Never silently swallow exceptions in coroutines — use `CoroutineExceptionHandler`.

### Dependency Injection
- Use **Hilt** for all new classes (`@Singleton`, `@Inject constructor`).
- `KeyStoreManager`, `AppLockManager`, `P2PChatService` must be Hilt-injected.

### Testing
- Unit-test `KeyStoreManager.sign()` / `.verify()` with a mock key pair.
- Unit-test `readPacket`/`writePacket` with a `PipedInputStream`/`PipedOutputStream`.
- Unit-test ACK loop guard: verify that an `ACK_RECEIPT` packet does NOT trigger another `ACK_RECEIPT`.

---

## File/Package Structure to Follow

```
com.yourapp/
├── crypto/
│   ├── KeyStoreManager.kt        # Phase 1.2
│   └── SessionCipher.kt          # Phase 4.2
├── lock/
│   ├── AppLockManager.kt         # Phase 1.1
│   └── LockScreen.kt             # Phase 1.1
├── network/
│   ├── model/
│   │   ├── NetworkPacket.kt      # Phase 2.2
│   │   └── PacketType.kt         # Phase 2.2
│   ├── SocketMessageTransport.kt # Phase 2.1 (refactor existing)
│   ├── FileTransferServer.kt     # Phase 3.1
│   └── FileTransferClient.kt     # Phase 3.1
├── service/
│   └── P2PChatService.kt         # Phase 4.1
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   ├── MessageEntity.kt  # update status enum
│   │   │   └── PeerEntity.kt     # Phase 1.2 migration
│   │   └── ChatLocalDataSource.kt
│   └── repository/
│       └── ChatRepository.kt
├── ui/
│   ├── discovery/
│   │   ├── DiscoveryScreen.kt
│   │   └── DiscoveryViewModel.kt
│   ├── chat/
│   │   ├── ChatScreen.kt         # add typing indicator, progress bar
│   │   └── ChatViewModel.kt      # add ACK, typing debounce
│   └── call/
│       └── CallScreen.kt         # Phase 5.2
└── di/
    └── AppModule.kt              # register all new Hilt bindings
```

---

## Summary: Build Order Checklist

- [ ] **Phase 1.1** — `AppLockManager` + `LockScreen` + `BiometricPrompt`
- [ ] **Phase 1.2** — `KeyStoreManager` + `PeerEntity` migration (MAC → PublicKeyHash)
- [ ] **Phase 1.3** — Cryptographic handshake in `SocketMessageTransport`
- [ ] **Phase 2.1** — Replace `PrintWriter`/`BufferedReader` with `DataOutputStream`/`DataInputStream` + length-prefix framing
- [ ] **Phase 2.2** — `NetworkPacket` model + `kotlinx.serialization`
- [ ] **Phase 2.3** — ACK receipt loop + `MessageStatus` enum update
- [ ] **Phase 2.4** — Typing indicator debounce + UI state
- [ ] **Phase 3.1** — `FileTransferServer`/`Client` on port 8989
- [ ] **Phase 3.2** — Chunked streaming + Scoped Storage save
- [ ] **Phase 3.3** — `LinearProgressIndicator` bound to transfer `StateFlow`
- [ ] **Phase 4.1** — `P2PChatService` (Foreground) + local notifications
- [ ] **Phase 4.2** — ECDH session key + AES-GCM encrypt/decrypt wrapper
- [ ] **Phase 5.1** — Group Owner message router + member list screen
- [ ] **Phase 5.2** — WebRTC signaling via control socket + call UI
