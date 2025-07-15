package com.undefined.hydron

import android.app.Application
import com.undefined.hydron.infrastructure.db.MainDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MainDatabase.getInstance(this)

    }
}