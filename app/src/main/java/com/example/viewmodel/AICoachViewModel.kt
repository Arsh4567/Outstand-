package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(val text: String, val isUser: Boolean)

class AICoachViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("Hi there! I'm your Outstand AI Coach. How can I help you focus and achieve your goals today?", isUser = false))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            _messages.value = _messages.value + ChatMessage(userText, isUser = true) + ChatMessage("API Key is missing. Please configure it in settings or the Secrets panel.", isUser = false)
            return
        }

        val userMsg = ChatMessage(userText, isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val apiContents = _messages.value.map { msg ->
                    Content(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(Part(text = msg.text))
                    )
                }

                val request = GenerateContentRequest(
                    contents = apiContents,
                    systemInstruction = Content(parts = listOf(Part(text = "You are Outstand's AI Coach, a digital wellbeing and personal growth coach. Provide concise, empathetic, and actionable advice based on behavioral science. Do not hallucinate.")))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val aiResponseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't process that."
                _messages.value = _messages.value + ChatMessage(aiResponseText, isUser = false)
            } catch (e: Exception) {
                Log.e("AICoachViewModel", "Error: ${e.message}")
                _messages.value = _messages.value + ChatMessage("Network error. Please try again.", isUser = false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
