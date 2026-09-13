package com.example.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.MindCoachRepository
import com.example.viewmodel.GoalsViewModel

object AppContainer {
    private lateinit var database: AppDatabase
    lateinit var mindCoachRepository: MindCoachRepository

    fun initialize(context: Context) {
        database = AppDatabase.getDatabase(context)
        mindCoachRepository = MindCoachRepository(database.mindCoachDao())
    }
}

class MindCoachViewModelFactory(private val repository: MindCoachRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(com.example.viewmodel.AICoachViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.viewmodel.AICoachViewModel() as T
        }
        if (modelClass.isAssignableFrom(com.example.viewmodel.DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.viewmodel.DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
