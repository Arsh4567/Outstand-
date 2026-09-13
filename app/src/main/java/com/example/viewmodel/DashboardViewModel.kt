package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyTask
import com.example.data.repository.MindCoachRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: MindCoachRepository) : ViewModel() {

    val dailyTasks: StateFlow<List<DailyTask>> = repository.allDailyTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String, timeSlot: String, description: String = "") {
        viewModelScope.launch {
            repository.insertDailyTask(DailyTask(title = title, timeSlot = timeSlot, description = description))
        }
    }

    fun toggleTaskCompletion(task: DailyTask, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateDailyTask(task.copy(isCompleted = isCompleted))
        }
    }

    // Mock initial generation of timetable for the prototype
    fun generateMockTimetable() {
        viewModelScope.launch {
            repository.insertDailyTask(DailyTask(title = "Morning Meditation", timeSlot = "08:00 AM - 08:15 AM"))
            repository.insertDailyTask(DailyTask(title = "Deep Work Session (Goal #1)", timeSlot = "09:00 AM - 11:00 AM"))
            repository.insertDailyTask(DailyTask(title = "Phone-free Lunch Walk", timeSlot = "12:30 PM - 01:15 PM"))
            repository.insertDailyTask(DailyTask(title = "Review AI Roadmap Progress", timeSlot = "04:30 PM - 05:00 PM"))
        }
    }
}
