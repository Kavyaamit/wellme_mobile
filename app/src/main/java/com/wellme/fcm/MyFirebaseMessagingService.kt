package com.wellme.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.v("Hello Notification", "==> ")
        p0?.data?.let {
            Log.d("Notification hello", "Message data payload: " + p0.data)
        }

        // TODO Step 3.6 check messages for notification and call sendNotification
        // Check if message contains a notification payload.
        p0?.notification?.let {
            Log.d("Notification hello", "Message data payload: " + p0.notification)
            Log.d("Notification hello", "Message Notification Body: ${it.title}")
            //sendNotification(it.body!!)
        }


    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}