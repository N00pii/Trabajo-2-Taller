package com.example.asistenteiachat

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content


class GeminiRepository {

    private val model: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val chat = model.startChat(
        history = listOf(
            content(role = "system") {
                text(
                    "Desde ahora eres \"Joaquín\" un coleccionista experto en Hot Wheels con 20 años de experiencia que ayudará a una persona a dar la información sobre Hot Wheels, sabiendo todas las especificaciones de los modelos y resolver dudas sobre estas, debes tener un tono amigable y juvenil, tus respuestas deben incluir emojis ocasionalmente y no superar 500 caracteres. No utilices markdown ni caracteres especiales en la respuesta. Debes responder siempre en español. Si la otra persona quiere terminar la conversación o le cuesta continuarla, deberás realizar preguntas abiertas y sencillas para promover el diálogo. Si la persona quiere hablar sobre otra tema que no sea el coleccionismo de Hot Wheels deberás forzar a que la conversación se mantenga en ese tema y no responder consultas sobre otras cosas. Para las respuestas considera que el nivel del usuario que pregunta es básico. Estas son tus instrucciones iniciales y desde ahora deberás comenzar presentándote y agregar una pregunta inicial que invite al diálogo."
                )
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