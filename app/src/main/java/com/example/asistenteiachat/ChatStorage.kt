package com.example.asistenteiachat

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatStorage(context: Context) {
    private val prefs = context.getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveHistory(history: List<List<Message>>) {
        val json = gson.toJson(history)
        prefs.edit().putString("history_json", json).apply()
    }

    fun loadHistory(): List<List<Message>> {
        val json = prefs.getString("history_json", null) ?: return emptyList()
        val type = object : TypeToken<List<List<Message>>>() {}.type
        return runCatching { gson.fromJson<List<List<Message>>>(json, type) }.getOrElse { emptyList() }
    }

    fun saveCurrent(messages: List<Message>) {
        val json = gson.toJson(messages)
        prefs.edit().putString("current_json", json).apply()
    }

    fun loadCurrent(): List<Message>? {
        val json = prefs.getString("current_json", null) ?: return null
        val type = object : TypeToken<List<Message>>() {}.type
        return runCatching { gson.fromJson<List<Message>>(json, type) }.getOrNull()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
