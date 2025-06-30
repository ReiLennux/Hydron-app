package com.undefined.hydron

import android.app.Application
import com.undefined.hydron.infrastructure.receivers.PhoneMessageReceiver
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val receiver = EntryPointAccessors.fromApplication(this, ReceiverEntryPoint::class.java).receiver()
        receiver.register()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReceiverEntryPoint {
        fun receiver(): PhoneMessageReceiver
    }
}
