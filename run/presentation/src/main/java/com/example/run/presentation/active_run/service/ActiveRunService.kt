package com.example.run.presentation.active_run.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.example.core.presentation.ui.ui.formatted
import com.example.run.domain.RunningTracker
import com.example.run.presentation.R
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class ActiveRunService : Service() {

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(com.example.core.presentation.designsystem.R.drawable.logo)
            .setContentTitle(getString(R.string.active_run))

    }

    private val runningTracker by inject<RunningTracker>()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun start(activityClass: Class<*>) {
        if (!isServiceActive) {
            isServiceActive = true
            createNotificationChannel()

            val activityIntent = Intent(applicationContext, activityClass).apply {
                data = "runtracker://active_run".toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build()

            startForeground(1, notification)
            updateNotification()
        }
    }

    private fun updateNotification() {
        runningTracker.elapsedTime.onEach { elapsedTime ->
            val notification= baseNotification.setContentText(elapsedTime.formatted()).build()
            notificationManager.notify(1, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.active_run),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        var isServiceActive = false
        private const val CHANNEL_ID = "active_run"
    }
}