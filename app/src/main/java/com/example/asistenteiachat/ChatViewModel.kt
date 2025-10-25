package com.example.asistenteiachat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel : ViewModel() {

    private val repository = GeminiRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private val _history = MutableStateFlow<List<List<Message>>>(emptyList())
    val history: StateFlow<List<List<Message>>> = _history

    init {
        val initialMessage = Message(
            text = _uiState.value.initialGreeting,
            isUser = false,
            timestamp = getCurrentTimestamp()
        )
        _uiState.update { it.copy(messages = listOf(initialMessage)) }
    }

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = Message(
            text = prompt,
            isUser = true,
            timestamp = getCurrentTimestamp()
        )
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
            )
        }

        viewModelScope.launch {
            val responseText = repository.sendMessage(prompt)

            val aiMessage = Message(
                text = responseText,
                isUser = false,
                timestamp = getCurrentTimestamp()
            )

            _uiState.update {
                it.copy(
                    messages = it.messages + aiMessage,
                )
            }
        }
    }

    fun saveCurrentConversation() {
        if (_uiState.value.messages.size > 1) {
            val currentChat = _uiState.value.messages
            _history.update { it + listOf(currentChat) }
        }
    }

    fun resumeConversation(historyIndex: Int) {
        val conversationToLoad = _history.value.getOrNull(historyIndex)

        conversationToLoad?.let { messages ->
            _uiState.update {
                it.copy(messages = messages)
            }
        }
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date())
    }
}