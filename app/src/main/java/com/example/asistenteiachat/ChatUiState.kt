package com.example.asistenteiachat

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

data class ConversationSummary(
    val id: Int,
    val assistantGreeting: String,
    val userPromptSummary: String,
)

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val initialGreeting: String = "Hola, soy Joaquín. Hoy seré tu asistente virtual experto en Hot Wheels. ¿En qué te puedo ayudar?"
)
