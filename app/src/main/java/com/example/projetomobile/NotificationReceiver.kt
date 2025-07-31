package com.example.projetomobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_1") {
            Toast.makeText(context, "Notificação marcada como lida", Toast.LENGTH_SHORT).show()

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(2)
        }
    }
}