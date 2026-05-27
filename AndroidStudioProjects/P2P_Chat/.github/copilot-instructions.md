# Copilot Instructions

## Build, test, lint
- Build (debug APK): `./gradlew assembleDebug` (Windows: `.\gradlew assembleDebug`)
- Unit tests: `./gradlew testDebugUnitTest`
- Single unit test (example): `./gradlew testDebugUnitTest --tests "com.bsit.peerx.ExampleUnitTest.addition_isCorrect"`
- Instrumented tests (device/emulator): `./gradlew connectedDebugAndroidTest`
- Single instrumented test (example): `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.bsit_PeerX.ExampleInstrumentedTest`
- Lint: `./gradlew lintDebug` (or `./gradlew lint`)

## High-level architecture
- **Compose UI + Navigation**: code under `app/src/main/java/com/bsit/PeerX/ui/`. `MainActivity` composes `AppNavigation` (NavHost) and wires lifecycle events to ViewModels for Wiâ€‘Fi Direct receiver registration.
- **MVVM + Flow**: ViewModels expose StateFlow/SharedFlow; UI collects via `collectAsStateWithLifecycle()` or `LaunchedEffect` for one-shot events.
- **Data layer**: `ChatRepository` is the single entry point (implemented in `ChatRepositoryImpl`). It consumes `MessageTransport` (socket layer) and persists messages via Room.
- **Transport & Handshake**: `SocketMessageTransport` performs a 3-phase handshake (pubkey exchange + mutual challenge/response). Connection states are exposed via `ConnectionStatus` (DISCONNECTED, CONNECTING, CONNECTED, CONNECTED_AND_VERIFIED, AUTH_FAILED).
- **File transfer**: Implemented on a separate port (8989) via `FileTransferServer` / `FileTransferClient` to avoid blocking the control channel (8988).

## Key conventions
- **Dependency injection**: use Hilt and register providers in `di/AppModule.kt`. A long-lived application CoroutineScope is provided (annotated `@ApplicationScope`) and passed into transports/repositories.
- **Navigation events**: short-lived navigation events (Discovery â†’ Chat) are emitted as a SharedFlow from the DiscoveryViewModel. Consumers should use a replay=1 SharedFlow or collect in a LaunchedEffect to avoid missing events. When passing peerAddress in routes, encode arguments with `Uri.encode(peerAddress)` to avoid navigation errors with special chars.
- **Message lifecycle**: Messages are saved locally before sending. `ACK_RECEIPT` packets update local status to DELIVERED or READ when payload contains `status = "READ"`. `ChatRepository` exposes read/typing APIs.
- **Typing & Presence**: Typing indicator is sent as `TYPING_STATUS` packet; debounce stop-typing on the client side (3s). Observe typing via `ChatRepository.observeTypingStatus()`.
- **File transfers**: Use `sendFile(...)` on the repository which sends a FILE_HEADER then the receiver replies with FILE_READY; actual file bytes are transferred over port 8989.
- **Group chat**: Send group messages by including `groupId` in the TEXT packet payload. Store group messages with peerAddress = groupId so UI can subscribe to a channel.
- **Call signaling / WebRTC**: Signaling uses `PacketType.CALL_SDP` and `PacketType.CALL_ICE_CANDIDATE`. A `CallManager` stub exists at `app/src/main/java/com/bsit/PeerX/network/CallManager.kt`. To add real WebRTC support:
  - Add dependency: `implementation "org.webrtc:google-webrtc:1.0.32006"` (or the latest stable).
  - Implement PeerConnectionFactory, PeerConnection instances, and LocalMediaStream inside `CallManager`.
  - Use `CALL_SDP` to exchange offers/answers (payload: { "callId": "...", "sdp": "..." }) and `CALL_ICE_CANDIDATE` for ICE (payload: { "callId": "...", "candidate": "..." }).

## Notable files to inspect when troubleshooting
- `SocketMessageTransport.kt` â€” handshake, connection lifecycle, incoming packet parsing
- `ChatRepositoryImpl.kt` â€” packet handlers, read receipts, file transfer orchestration, call signaling
- `DiscoveryViewModel.kt` / `DiscoveryScreen.kt` â€” peer discovery and navigation flow
- `ChatViewModel.kt` / `ChatScreen.kt` â€” UI behavior for typing, read receipts, file progress
- `network/CallManager.kt` â€” stub for WebRTC integration and guidance for implementing PeerConnection logic

## Other AI assistant configs
No CLAUDE.md, AGENTS.md, or other assistant config files were detected in the repo root. If you add one, include a short summary here so Copilot sessions can pick it up.

