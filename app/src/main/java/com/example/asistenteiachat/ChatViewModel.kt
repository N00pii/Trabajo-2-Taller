package com.example.asistenteiachat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = GeminiRepository()
    private val storage = ChatStorage(app) // Persistencia

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private val _history = MutableStateFlow<List<List<Message>>>(emptyList())
    val history: StateFlow<List<List<Message>>> = _history

    init {
        _history.value = storage.loadHistory()

        val restored = storage.loadCurrent()
        if (!restored.isNullOrEmpty()) {
            _uiState.update { it.copy(messages = restored) }
        } else {
            val initialMessage = Message(
                text = _uiState.value.initialGreeting,
                isUser = false,
                timestamp = getCurrentTimestamp()
            )
            _uiState.update { it.copy(messages = listOf(initialMessage)) }
            storage.saveCurrent(_uiState.value.messages)
        }
    }

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = Message(
            text = prompt,
            isUser = true,
            timestamp = getCurrentTimestamp()
        )
        _uiState.update { it.copy(messages = it.messages + userMessage) }
        storage.saveCurrent(_uiState.value.messages)

        viewModelScope.launch {
            val responseText = repository.sendMessage(prompt)

            val aiMessage = Message(
                text = responseText,
                isUser = false,
                timestamp = getCurrentTimestamp()
            )
            _uiState.update { it.copy(messages = _uiState.value.messages + aiMessage) }
            storage.saveCurrent(_uiState.value.messages)
        }
    }

    fun saveCurrentConversation() {
        val current = _uiState.value.messages
        if (current.size <= 1) return
        if (_history.value.any { areSameConversation(it, current) }) return
        val newHistory = _history.value + listOf(current)
        _history.value = newHistory
        storage.saveHistory(newHistory)
    }

    fun resumeConversation(historyIndex: Int) {
        _history.value.getOrNull(historyIndex)?.let { messages ->
            _uiState.update { it.copy(messages = messages) }
            storage.saveCurrent(messages)
        }
    }

    private fun areSameConversation(a: List<Message>, b: List<Message>): Boolean {
        if (a.size != b.size) return false

        return a.indices.all { i ->
            a[i].isUser == b[i].isUser && a[i].text == b[i].text
        }
    }

    private fun getCurrentTimestamp(): String =
        SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date())
}
