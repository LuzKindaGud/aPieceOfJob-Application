# Phase 4 Implementation Report: Read Receipts + UX polish

## Goal
Implement read receipts (READ) and ensure UI marks messages as read when ChatScreen becomes visible. Polish navigation and update Copilot instructions.

## Changes implemented
- Read receipts
  - Added DAO query to fetch unread received messages.
  - ChatRepository: new method `sendReadReceiptsForPeer(peerAddress)` which emits ACK_RECEIPT packets with `status = "READ"` and updates local DB.
  - Incoming ACK handling now respects `status` payload and marks messages as DELIVERED or READ.
  - ChatViewModel: upon initializing the peer (chat visible), calls `sendReadReceiptsForPeer` so unread messages are acknowledged as READ.

## Files changed
- app/src/main/java/com/bsit/p2p_chat/data/local/dao/ChatMessageDao.kt  (added getUnreadReceivedMessages)
- app/src/main/java/com/bsit/p2p_chat/data/datasource/ChatLocalDataSource.kt (added getUnreadReceived)
- app/src/main/java/com/bsit/p2p_chat/data/repository/ChatRepository.kt (added sendReadReceiptsForPeer)
- app/src/main/java/com/bsit/p2p_chat/data/repository/ChatRepositoryImpl.kt (ACK handling + new method)
- app/src/main/java/com/bsit/p2p_chat/ui/chat/ChatViewModel.kt (call sendReadReceiptsForPeer on init)
- .github/copilot-instructions.md (expanded)

## How to verify
1. Build: `./gradlew assembleDebug`
2. Start two devices/emulators and connect via Discovery.
3. Send messages from device A to device B while B is not viewing the chat.
4. Open ChatScreen on device B — messages should update to READ and device A should receive ACK with status=READ.
5. Check logs for `ACK_RECEIPT` packets and status updates.

If anything looks off, ask for the specific failing step and logs and further adjustments will be made.
