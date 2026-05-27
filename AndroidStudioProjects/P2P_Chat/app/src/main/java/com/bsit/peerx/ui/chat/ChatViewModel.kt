package com.bsit.peerx.ui.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsit.peerx.data.repository.ChatRepository
import com.bsit.peerx.data.repository.ProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.delay

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val peerAddress: String = savedStateHandle.get<String>("peerAddress")!!
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private var observeJob: Job? = null
    private var typingJob: Job? = null
    private var typingStatusJob: Job? = null
    private var progressJobs = mutableMapOf<String, Job>()

    init {
        initPeer(peerAddress)
        observeOwnName()
    }

    // Call controls
    fun startCall() {
        viewModelScope.launch {
            val result = repository.startCall(peerAddress)
            if (result.isSuccess) {
                val id = result.getOrNull()
                _uiState.update { it.copy(isInCall = true, currentCallId = id) }
            } else {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun stopCall() {
        val callId = _uiState.value.currentCallId ?: return
        viewModelScope.launch {
            val result = repository.stopCall(callId)
            if (result.isSuccess) {
                _uiState.update { it.copy(isInCall = false, currentCallId = null) }
            } else {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun observeOwnName() {
        profileRepository.getDisplayName()
            .onEach { name ->
                _uiState.update { it.copy(ownName = name) }
            }
            .launchIn(viewModelScope)
    }

    fun initPeer(peerAddress: String) {
        if (_uiState.value.peerAddress == peerAddress && observeJob != null) return

        _uiState.update {
            it.copy(peerAddress = peerAddress, messages = emptyList(), errorMessage = null)
        }

        observeJob?.cancel()
        typingStatusJob?.cancel()
        progressJobs.values.forEach { it.cancel() }
        progressJobs.clear()

        viewModelScope.launch {
            val name = repository.getPeerName(peerAddress)
            _uiState.update { it.copy(peerName = name) }
        }

        observeJob = viewModelScope.launch {
            repository.observeMessages(peerAddress).collect { messages ->
                _uiState.update { current -> current.copy(messages = messages) }
                
                // Observe progress for new file messages
                messages.forEach { message ->
                    val fileId = message.fileTransferId
                    if (fileId != null && !progressJobs.containsKey(fileId)) {
                        progressJobs[fileId] = repository.observeFileProgress(fileId)
                            .onEach { progress ->
                                _uiState.update { current ->
                                    current.copy(fileProgress = current.fileProgress + (fileId to progress))
                                }
                            }.launchIn(viewModelScope)
                    }
                }
            }
        }

        // Send READ receipts for any unread messages now that the chat is visible
        viewModelScope.launch {
            repository.sendReadReceiptsForPeer(peerAddress)
        }

        typingStatusJob = viewModelScope.launch {
            repository.observeTypingStatus(peerAddress).collect { isTyping ->
                _uiState.update { current -> current.copy(isPeerTyping = isTyping) }
            }
        }
    }

    fun onInputChange(value: String) {
        val prevValue = _uiState.value.inputText
        _uiState.update { it.copy(inputText = value) }

        if (value.isNotEmpty() && prevValue.isEmpty()) {
            sendTypingStatus(true)
        } else if (value.isEmpty() && prevValue.isNotEmpty()) {
            sendTypingStatus(false)
        }

        // Debounced stop typing
        typingJob?.cancel()
        if (value.isNotEmpty()) {
            typingJob = viewModelScope.launch {
                delay(3000)
                sendTypingStatus(false)
            }
        }
    }

    private fun sendTypingStatus(isTyping: Boolean) {
        viewModelScope.launch {
            repository.sendTypingStatus(peerAddress, isTyping)
        }
    }

    fun sendMessage() {
        val state = _uiState.value
        if (state.inputText.isBlank() || state.peerAddress.isBlank() || state.isSending) return
        viewModelScope.launch {
            typingJob?.cancel()
            sendTypingStatus(false)
            
            _uiState.update { it.copy(isSending = true, errorMessage = null) }

            val messageText = state.inputText.trim()
            val result = repository.sendMessage(state.peerAddress, messageText)

            _uiState.update {
                it.copy(
                    inputText = if (result.isSuccess) "" else messageText,
                    isSending = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun sendFile(uri: Uri) {
        viewModelScope.launch {
            repository.sendFile(peerAddress, uri)
        }
    }
}



