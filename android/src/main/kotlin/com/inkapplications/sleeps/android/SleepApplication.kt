package com.inkapplications.sleeps.android

import android.app.Application

class SleepApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        module = ApplicationModule(this).apply {
            initRunner.initialize()
        }
    }

    companion object {
        lateinit var module: ApplicationModule
    }
}
