package com.lateinit.rightweight.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.lateinit.rightweight.R
import java.util.*

fun convertTimeStamp(count: Int): String {
    val hours: Int = (count / 60) / 60
    val minutes: Int = count / 60
    val seconds: Int = count % 60

    return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
}

class TimerService : Service() {
    private var isTimerRunning = false
    private var timeCount = 0
    private lateinit var timer: Timer
    private lateinit var notificationManager: NotificationManager
    private lateinit var customNotification: Notification
    private lateinit var notificationLayout: RemoteViews

    companion object {
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


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

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

        return super.onStartCommand(intent, flags, startId)
    }


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setSound(null, null)
                setShowBadge(true)
            }

            notificationManager = getSystemService(NotificationManager::class.java).also {
                it.createNotificationChannel(notificationChannel)
            }
        }
    }

    private fun createNotification() {
        notificationLayout = RemoteViews(packageName, R.layout.notification_foreground)
        notificationLayout.setTextViewText(R.id.text_view_notification_timer, timeCount.toString())

        customNotification = NotificationCompat.Builder(this, "timer_notification")
            .setSmallIcon(R.drawable.img_right_weight)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setContentIntent(createDeepLink())
            .setAutoCancel(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createDeepLink(): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "app://page/exercise".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
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
        notificationLayout.setTextViewText(
            R.id.text_view_notification_timer,
            convertTimeStamp(timeCount)
        )
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
}