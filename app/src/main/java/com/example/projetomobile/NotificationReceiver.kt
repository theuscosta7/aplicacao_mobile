package com.example.projetomobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Verifique se a ação é "ACTION_1" e executa a ação de cancelar a notificação
        if (intent.action == "ACTION_1") {
            Toast.makeText(context, "Notificação marcada como lida", Toast.LENGTH_SHORT).show()

            // Cancelar a notificação com o ID usado ao mostrar a notificação
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(2)
        }
    }
}