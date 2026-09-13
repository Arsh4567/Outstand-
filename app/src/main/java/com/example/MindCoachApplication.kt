package com.example

import android.app.Application
import com.example.di.AppContainer

class MindCoachApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)
    }
}
