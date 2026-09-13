package com.example.data.repository

import com.example.data.local.dao.MindCoachDao
import com.example.data.model.DailyTask
import com.example.data.model.Goal
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow

class MindCoachRepository(private val dao: MindCoachDao) {
    val allGoals: Flow<List<Goal>> = dao.getAllGoals()
    val allDailyTasks: Flow<List<DailyTask>> = dao.getAllDailyTasks()
    val userStats: Flow<UserStats?> = dao.getUserStats()

    suspend fun insertUserStats(stats: UserStats) = dao.insertUserStats(stats)

    suspend fun insertGoal(goal: Goal) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = dao.updateGoal(goal)
    
    suspend fun insertDailyTask(task: DailyTask) = dao.insertDailyTask(task)
    suspend fun updateDailyTask(task: DailyTask) = dao.updateDailyTask(task)
}
