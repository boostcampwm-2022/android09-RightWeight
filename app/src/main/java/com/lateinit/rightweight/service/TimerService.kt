package com.lateinit.rightweight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.lateinit.rightweight.R
import java.util.*

fun convertTimeStamp(count: Int): String {
    val hours: Int = (count / 60) / 60
    val minutes: Int = (count / 60) % 60
    val seconds: Int = count % 60

    return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
}

class TimerService : LifecycleService() {
    private var isTimerRunning = false
    private var timeCount = 0
    private lateinit var timer: Timer
    private lateinit var notificationManager: NotificationManager
    private lateinit var customNotification: Notification
    private lateinit var notificationLayout: RemoteViews

    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setNotificationDeepLink(intent)
        operateTimer(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun setNotificationDeepLink(intent: Intent?) {
        intent?.getParcelableExtra<PendingIntent>(PENDING_INTENT_TAG)?.let {
            customNotification.contentIntent = it
        }
    }

    private fun operateTimer(intent: Intent?) {
        when (intent?.getStringExtra(MANAGE_ACTION_NAME)) {
            START -> {
                startTimer()
            }
            PAUSE -> {
                pauseTimer()
            }
            STOP -> {
                stopTimer()
            }
            STATUS -> {
                sendStatus()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setSound(null, null)
            setShowBadge(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createNotification() {
        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        notificationLayout = RemoteViews(packageName, R.layout.notification_foreground)
        notificationLayout.setTextViewText(
            R.id.text_view_notification_timer,
            convertTimeStamp(timeCount)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            customNotification = NotificationCompat.Builder(this, "timer_notification")
                .setSmallIcon(R.drawable.img_right_weight)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setAutoCancel(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        } else {
            customNotification = NotificationCompat.Builder(this, "timer_notification")
                .setSmallIcon(R.drawable.img_right_weight)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(convertTimeStamp(timeCount))
                .setAutoCancel(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        }

    }


    private fun startTimer() {
        startForeground(NOTIFICATION_ID, customNotification)
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    timeCount++
                    updateNotification()
                }
            }, 0, 1000)
        }
        changeTimerRunningState(true)
    }

    private fun updateNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationLayout.setTextViewText(
                R.id.text_view_notification_timer,
                convertTimeStamp(timeCount)
            )
            notificationManager.notify(NOTIFICATION_ID, customNotification)
        } else {
            customNotification = NotificationCompat.Builder(this, "timer_notification")
                .setSmallIcon(R.drawable.img_right_weight)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(convertTimeStamp(timeCount))
                .setAutoCancel(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        }
        notificationManager.notify(NOTIFICATION_ID, customNotification)
        sendStatus()
    }

    private fun pauseTimer() {
        timer.cancel()
        changeTimerRunningState(false)
    }

    private fun stopTimer() {
        stopForeground(true)
        timer.cancel()
        changeTimerRunningState(false)
    }

    private fun changeTimerRunningState(isTimerRunning: Boolean) {
        this.isTimerRunning = isTimerRunning
        sendStatus()
    }

    private fun sendStatus() {
        val statusIntent = Intent().apply {
            action = STATUS_ACTION_NAME
            putExtra(IS_TIMER_RUNNING_INTENT_EXTRA, isTimerRunning)
            putExtra(TIME_COUNT_INTENT_EXTRA, timeCount)
        }
        sendBroadcast(statusIntent)
    }

    companion object {
        const val PENDING_INTENT_TAG = "pending_intent"
        const val MANAGE_ACTION_NAME = "timer_manage_action"
        const val STATUS_ACTION_NAME = "timer_status_action"
        const val CHANNEL_ID = "timer_notification"
        const val CHANNEL_NAME = "Timer"
        const val START = "start"
        const val PAUSE = "pause"
        const val STOP = "stop"
        const val STATUS = "status"
        const val TIME_COUNT_INTENT_EXTRA = "time_count"
        const val IS_TIMER_RUNNING_INTENT_EXTRA = "is_timer_running"
        const val NOTIFICATION_ID = 1
    }
}