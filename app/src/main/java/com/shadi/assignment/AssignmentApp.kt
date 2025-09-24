package com.shadi.assignment

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AssignmentApp : Application(){
    companion object {
        lateinit var instance: AssignmentApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

