package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyTask
import com.example.data.repository.MindCoachRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.model.UserStats
import kotlinx.coroutines.flow.first
import java.util.Calendar

class DashboardViewModel(private val repository: MindCoachRepository) : ViewModel() {

    val dailyTasks: StateFlow<List<DailyTask>> = repository.allDailyTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun toggleTaskCompletion(task: DailyTask, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateDailyTask(task.copy(isCompleted = isCompleted))
            checkStreak()
        }
    }

    private suspend fun checkStreak() {
        val tasks = repository.allDailyTasks.first()
        if (tasks.isNotEmpty() && tasks.all { it.isCompleted }) {
            val stats = repository.userStats.first() ?: UserStats()
            val today = getStartOfDay(System.currentTimeMillis())
            val lastCompleted = stats.lastCompletedDate

            if (lastCompleted < today) {
                // If it's a new day, increment streak.
                val yesterday = today - 86400000L
                val newStreak = if (lastCompleted >= yesterday) {
                    stats.currentStreak + 1
                } else {
                    1
                }
                repository.insertUserStats(stats.copy(currentStreak = newStreak, lastCompletedDate = today))
            }
        }
    }

    private fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
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
