1.1 — App Lock (Biometric Gate)
* AppLockManager: Implemented a singleton manager that observes the app's lifecycle. It automatically locks the app if it remains in the background for more than 30 seconds.
* LockScreen: Created a secure, cyber-themed overlay that prevents access to the app until biometric authentication (fingerprint/face) or device credentials are provided.
* MainActivity Integration: Refactored MainActivity to extend FragmentActivity and wrapped the main navigation host with a lock check, ensuring total app protection.

1.2 — Local Identity (Key Pair Generation)
* KeyStoreManager: Implemented secure cryptographic identity management using the Android Keystore.
    * Generates a permanent EC (Elliptic Curve) key pair for the device.
    * Provides a stable Peer ID derived from the SHA-256 hash of the public key.
    * Handles digital signing and verification of data.
* Identity Persistence: Created PeerEntity and PeerDao to store and manage verified peer identities. Updated the Room database to version 2 to support this new schema.

1.3 — Cryptographic Handshake & Advanced Transport
* Secure Handshake: Implemented a 3-phase mutual authentication protocol in SocketMessageTransport:
    1. Exchange: Peers swap public keys and profiles.
    2. Challenge: One peer challenges the other with a random nonce.
    3. Verify: The challenged peer signs the nonce, and the challenger verifies the signature. This repeats for the other side.
* Robust Transport (Phase 2.1 & 2.2 Foundation):
    * Switched from PrintWriter/BufferedReader to DataOutputStream/DataInputStream for binary-safe communication.
    * Implemented Length-Prefix Framing to prevent TCP fragmentation issues.
    * Unified all communications under a JSON-encoded Packet Model (NetworkPacket) using kotlinx-serialization.
* Verified Navigation: Updated DiscoveryViewModel to ensure that users can only enter a chat session after a successful cryptographic handshake, effectively preventing
  unauthorized or spoofed connections.

Components Added/Updated:
- AppLockManager.kt, LockScreen.kt
- KeyStoreManager.kt
- PeerEntity.kt, PeerDao.kt, AppDatabase.kt
  The application now has a solid security foundation where every peer is cryptographically identified and every connection is mutually authenticated.

o test the functions implemented in Phase 1, you can follow this structured testing plan. Since Phase 1 covers security, identity, and transport, you will need to observe both
the UI behavior and the system logs (Logcat).

1. Testing Phase 1.1: App Lock (Biometric Gate)
   The goal is to verify that the application protects user data with a biometric overlay.

* Initial Lock:
  * Open the app for the first time. The LockScreen (Cyber-themed) should appear immediately.
  * Verify that you cannot bypass this screen using the "Back" button (handled by BackHandler).
* Authentication:
  * Click "Unlock" and provide your fingerprint, face, or device PIN. The app should navigate to the main screen.
* Background Timeout:
  * Put the app in the background (press Home).
  * Wait for more than 30 seconds.
  * Re-open the app. It should be locked again.
  * Put the app in the background for less than 30 seconds. Re-open it; it should stay unlocked.

2. Testing Phase 1.2: Local Identity (KeyStore)
   The goal is to verify that the device has a unique, persistent cryptographic identity.

* Log Verification:
  * Open Logcat in Android Studio and filter by SocketTransport.
  * When the app starts or a connection is initiated, the KeyStoreManager is invoked. While there isn't a "success" UI, you can verify it by checking the Database.
* Database Inspector:
  * In Android Studio, go to App Inspection -> Database Inspector.
  * Check the peers table. After your first successful connection (Phase 1.3), you should see a new entry with a publicKeyBase64 and a unique peerId.

3. Testing Phase 1.3: Cryptographic Handshake
   This requires two devices (or one device and one emulator capable of Wi-Fi Direct).

* The "Verified" Handshake Flow:
  1. Start discovery on both devices.
  2. Connect Device A to Device B.
  3. Watch Logcat (Filter: SocketTransport):
    * You should see: Server started, waiting for client... on the Group Owner.
    * You should see: Handshake successful with peer: [ID] on both devices.
* Navigation Check:
  * Verify that the Chat screen only opens after the "Handshake successful" log appears.
  * If you are testing with a modified (non-matching) key, the app should log Handshake failed and disconnect automatically, never reaching the Chat screen.

Summary of Log Tags to Watch:
* SocketTransport: For handshake phases (Exchange, Challenge, Verify).
* AppLockManager: For background timeout and lock status changes.


1. Ban da cap nhat nhung gi:
  - SocketMessageTransport: them connectedPeerAddress (Flow) va gan gia tri khi socket ket noi; reset khi disconnect.
  - DiscoveryViewModel: bo theo doi connectionInfo, chi dieu huong sau khi connectedPeerAddress co gia tri; loai bo navigateToChat voi "manual_test_chat".
  - ViewModelFactories: cap nhat DiscoveryViewModelFactory cho dung constructor moi va bo tham so wifiDirectManager.
  - Tai lieu Connect_Device.txt: cap nhat luong ket noi de phu hop viec dieu huong dua tren socket.

2. Ban da tao file nao, phuong thuc nao:
  - Tao file: updated\summary.txt.
  - Khong tao them phuong thuc moi; chi them Flow connectedPeerAddress va logic cap nhat gia tri trong SocketMessageTransport.

3. Tai sao lai lam dieu do:
  - Dam bao chat chi mo khi socket da ket noi that su, de gui/nhan tin nhan vao dung thread theo IP peer.
  - Loai bo ID gia "manual_test_chat" de tranh luu tin nhan sai cuoc tro chuyen.
  - Dong bo tai lieu luong ket noi voi implementation hien tai.


