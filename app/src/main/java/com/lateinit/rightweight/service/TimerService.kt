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
import androidx.lifecycle.lifecycleScope
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.util.convertTimeStamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : LifecycleService() {

    @Inject
    lateinit var db: AppDatabase
    private var isTimerRunning = false
    private var timeCount = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var customNotification: Notification
    private lateinit var notificationLayout: RemoteViews
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch(Dispatchers.Default) {
            while (true) {
                if (isTimerRunning.not()) {
                    continue
                }
                timeCount++
                saveTime()
                updateNotification()
                delay(1_000L)
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setNotificationDeepLink(intent)
        setTimeCount(intent)
        operateTimer(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun setNotificationDeepLink(intent: Intent?) {
        intent?.getParcelableExtra<PendingIntent>(PENDING_INTENT_TAG)?.let { pendingIntent ->
            this.pendingIntent = pendingIntent
            createNotification()
        }
    }

    private fun setTimeCount(intent: Intent?) {
        intent?.getIntExtra(SET_TIME_COUNT, timeCount)?.let { timeCount ->
            if (this.timeCount < timeCount) {
                this.timeCount = timeCount
            }
        }
    }

    private fun operateTimer(intent: Intent?) {
        when (intent?.getStringExtra(MANAGE_ACTION_NAME)) {
            START -> {
                startForeground(NOTIFICATION_ID, customNotification)
                changeTimerRunningState(true)
            }
            PAUSE -> {
                changeTimerRunningState(false)
            }
            STATUS -> {
                sendStatus()
            }
        }
    }

    private fun createNotification() {
        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        notificationLayout = RemoteViews(packageName, R.layout.notification_foreground).apply {
            setTextViewText(
                R.id.text_view_notification_timer,
                convertTimeStamp(timeCount)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            customNotification = NotificationCompat.Builder(this, "timer_notification")
                .setSmallIcon(R.drawable.img_right_weight)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        } else {
            customNotification = NotificationCompat.Builder(this, "timer_notification")
                .setSmallIcon(R.drawable.img_right_weight)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(convertTimeStamp(timeCount))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
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

    private fun saveTime() {
        if (timeCount % 60 == 0) {
            lifecycleScope.launch(Dispatchers.IO) {
                val currentHistory = db.historyDao().getLatestHistory()
                db.historyDao().updateHistory(
                    currentHistory.copy(
                        time = convertTimeStamp(timeCount)
                    )
                )
            }
        }
    }

    private fun updateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationLayout.setTextViewText(
                R.id.text_view_notification_timer,
                convertTimeStamp(timeCount)
            )
        } else {
            customNotification = NotificationCompat.Builder(this, "timer_notification")
                .setSmallIcon(R.drawable.img_right_weight)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(convertTimeStamp(timeCount))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        }

        notificationManager.notify(NOTIFICATION_ID, customNotification)
        sendStatus()
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
        const val STATUS = "status"
        const val TIME_COUNT_INTENT_EXTRA = "time_count"
        const val IS_TIMER_RUNNING_INTENT_EXTRA = "is_timer_running"
        const val SET_TIME_COUNT = "set_time_count"
        const val NOTIFICATION_ID = 1
    }
}