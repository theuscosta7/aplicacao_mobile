package com.example.projetomobile

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class Notification(private val context: Context) {
    private val channelId = "default_channel"
    private val notificationId = 2
    private val notificationPermissionRequestCode = 1001

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "Canal para notificações"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBasicNotification(title: String, content: String) {
        // Verificar permissão para Android 13 (Mesmas verificações ocorrem para Notificações avançadas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Se não tiver permissão, solicitar permissão
                if (context is android.app.Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        notificationPermissionRequestCode
                    )
                }
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    fun showAdvancedNotification(title: String, content: String, imageResId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (context is android.app.Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        notificationPermissionRequestCode
                    )
                }
                return
            }
        }

        val style = NotificationCompat.BigPictureStyle()
            .bigPicture(BitmapFactory.decodeResource(context.resources, imageResId))
            .setBigContentTitle(title)
            .setSummaryText(content)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val actionIntent1 = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_1"
        }
        val actionPendingIntent1 = PendingIntent.getBroadcast(
            context, 0, actionIntent1, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_dialog_info,
                "Marcar como lida",
                actionPendingIntent1
            )

        builder.setStyle(style)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId + 1, builder.build())
        }
    }
}