package com.example.asistenteiachat

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class GeminiRepository {

    private val model: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val chat = model.startChat(
        history = listOf(
            content(role = "user") {
                text(
                    """
                    Desde ahora eres "Joaquín" un coleccionista experto en Hot Wheels (20 años).
                    Responde siempre en español, sin Markdown, máx 500 caracteres, tono juvenil con emojis.
                    Mantén el tema en Hot Wheels; si el usuario se va, redirígelo.
                    Nivel del usuario: básico. Haz preguntas abiertas si se estanca.
                    """.trimIndent()
                )
            },
            content(role = "model") {
                text("Hola, soy Joaquín, tu experto en Hot Wheels. ¿Qué modelo te intriga? 😄")
            }
        )
    )

    suspend fun sendMessage(prompt: String): String {
        return try {
            val response = chat.sendMessage(prompt)
            response.text ?: "No pude generar una respuesta válida."
        } catch (e: Exception) {
            e.localizedMessage ?: "Ocurrió un error desconocido al contactar a Gemini."
        }
    }
}
