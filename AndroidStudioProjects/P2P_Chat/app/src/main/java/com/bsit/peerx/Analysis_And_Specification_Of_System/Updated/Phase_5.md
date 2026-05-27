Lie# Phase 5 Implementation Report: Group Chat & WebRTC (Advanced)

## Summary
Implemented foundational support for group chat messaging and call signaling (WebRTC signaling). This phase focuses on signaling, message payload conventions, and scaffolding for a real WebRTC integration (CallManager stub). Full media pipeline (PeerConnection) is left as TODOs because it requires adding the WebRTC native dependency and platform-specific setup.

## What was added
- Group chat support:
  - TEXT packets can include `groupId` in payload. ChatRepositoryImpl stores group messages under `peerAddress = groupId` so UI can subscribe to a channel.
  - New API: `ChatRepository.sendGroupMessage(groupId, text)` to send channel messages.

- Call signaling (WebRTC):
  - New model `CallSignalingEvent` for SDP / ICE events.
  - ChatRepositoryImpl emits CallSignalingEvent when receiving `CALL_SDP` or `CALL_ICE_CANDIDATE` packets.
  - `CallManager` stub added with TODOs and guidance to implement PeerConnection/MediaStream handling.

## Files changed / added
- Modified: `ChatRepository.kt` (new group + signaling APIs)
- Modified: `ChatRepositoryImpl.kt` (handle groupId in TEXT, emit call signaling events, implement sendGroupMessage)
- Added: `network/CallManager.kt` (stub and guidance)
- Added: `network/model/CallSignalingEvent.kt`
- Updated: `.github/copilot-instructions.md` (WebRTC & group notes)

## Integration steps (next tasks)
1. Add WebRTC dependency to `app/build.gradle.kts`:
   - implementation("org.webrtc:google-webrtc:1.0.32006")
2. Implement `CallManager`:
   - Initialize PeerConnectionFactory
   - Create PeerConnection(s) and LocalMediaStream
   - On startCall(): createOffer(), setLocalDescription(), send CALL_SDP
   - On incoming CALL_SDP: setRemoteDescription(), createAnswer() if needed
   - On local ICE candidates: send CALL_ICE_CANDIDATE packets
3. UI: add a Call button in ChatScreen to start a call (use CallManager.startCall)
4. Handle group routing if you need broadcast across multiple peers: decide whether GO (Group Owner) should forward messages or app uses server relay.

## How to verify
- Unit / manual tests:
  - Send a TEXT packet with payload {"text":"hi","groupId":"room-1"} and verify ChatScreen subscribed to "room-1" receives it.
  - Observe incoming CALL_SDP / CALL_ICE_CANDIDATE events via `ChatRepository.observeCallSignaling()` and log them.

If you want, proceed and I can:
- Add a simple UI action to create/join a group channel and send group messages
- Wire CallManager to ChatRepositoryImpl so signaling is automatically forwarded to CallManager
- Add the WebRTC dependency and implement PeerConnection with a minimal audio-only call example

Which subtask to implement next?