package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.model.Goal
import com.example.data.repository.MindCoachRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoalsViewModel(private val repository: MindCoachRepository) : ViewModel() {

    val allGoals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addGoal(title: String, description: String, category: String) {
        viewModelScope.launch {
            val newGoal = Goal(title = title, description = description, category = category)
            repository.insertGoal(newGoal)
            generateRoadmap(title, description)
        }
    }

    private fun generateRoadmap(title: String, description: String) {
        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                Log.e("GoalsViewModel", "API Key is missing or default!")
                return@launch
            }

            val prompt = "Create a practical step-by-step roadmap to achieve this goal: $title - $description. Break it down into 3-5 actionable milestones using behavioral science principles for success."
            
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "You are MindCoach, an AI digital wellbeing and personal growth coach. Provide concise, actionable advice.")))
            )

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }
                val aiResponseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Could not generate roadmap."
                Log.d("GoalsViewModel", "AI Roadmap: $aiResponseText")
                // In future: parse response and save RoadmapSteps to DB
            } catch (e: Exception) {
                Log.e("GoalsViewModel", "Error generating roadmap: ${e.message}")
            }
        }
    }
}
