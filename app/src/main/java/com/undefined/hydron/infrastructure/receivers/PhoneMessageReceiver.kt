package com.undefined.hydron.infrastructure.receivers

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.undefined.hydron.domain.useCases.wear.HandleWearMessage
import com.undefined.hydron.dummy.simulateIncomingWearMessages
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneMessageReceiver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val handler: HandleWearMessage
) : MessageClient.OnMessageReceivedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    fun register() {
        Wearable.getMessageClient(context).addListener(this)
    }

    fun unregister() {
        Wearable.getMessageClient(context).removeListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        Log.d("WearReceiver", "Message received: ${event.path}")
        val path = event.path
        val data = event.data

        CoroutineScope(Dispatchers.IO).launch {
            try {
                handler(path, 1)
            } catch (e: Exception) {
                Log.e("WearReceiver", "Error processing message", e)
            }
        }

    }

    fun simulateMessage(){
        scope.launch {
            simulateIncomingWearMessages(handler)
        }
    }

}
