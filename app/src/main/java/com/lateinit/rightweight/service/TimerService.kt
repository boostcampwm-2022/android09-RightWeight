package com.lateinit.rightweight.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.lateinit.rightweight.R
import com.lateinit.rightweight.ui.home.HomeActivity
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class TimeCount(var count: Int = 0) : Parcelable {
    override fun toString(): String {
        val hours: Int = (count / 60) / 60
        val minutes: Int = count / 60
        val seconds: Int = count % 60

        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }
}

class TimerService : Service() {
    private var isTimerRunning = false

    private val timeCount = TimeCount()
    private lateinit var timer: Timer
    private lateinit var notificationManager: NotificationManager
    private lateinit var customNotification: Notification
    private lateinit var notificationLayout: RemoteViews

    companion object {
        const val MANAGE_ACTION_NAME = "timer_manage_action"
        const val MOMENT_ACTION_NAME = "timer_moment_action"
        const val STATUS_ACTION_NAME = "timer_status_action"
        const val CHANNEL_ID = "timer_notification"
        const val CHANNEL_NAME = "Timer"
        const val START = "start"
        const val PAUSE = "pause"
        const val STOP = "stop"
        const val STATUS = "status"
        const val TIME_COUNT_INTENT_EXTRA = "time_count"
        const val IS_TIMER_RUNNING_INTENT_EXTRA = "is_timer_running"
        const val SCREEN_MOVE_INTENT_EXTRA = "screen_move"
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()

        timer = Timer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra(MANAGE_ACTION_NAME)

        when (action) {
            START -> {
                startTimer()
                setNotification()
            }
            PAUSE -> {
                pauseTimer()
            }
            STATUS -> {
                sendStatus()
            }
            STOP -> {
                pauseTimer()
                stopForeground(true)
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

    private fun setNotification() {
        notificationLayout = RemoteViews(packageName, R.layout.notification_foreground)
        notificationLayout.setTextViewText(R.id.text_view_notification_timer, timeCount.toString())

        val screenMoveIntent = Intent(this, HomeActivity::class.java)
        screenMoveIntent.putExtra(
            SCREEN_MOVE_INTENT_EXTRA,
            R.id.action_navigation_home_to_navigation_exercise
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "app://page/exercise".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_MUTABLE)


        customNotification = NotificationCompat.Builder(this, "timer_notification")
            .setSmallIcon(R.drawable.img_right_weight)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, customNotification)

        if (isTimerRunning) {
            timer.cancel()
            startTimer {
                timeCount.count++
                updateNotification()
            }
        }
    }

    private fun updateNotification() {
        notificationLayout.setTextViewText(
            R.id.text_view_notification_timer,
            timeCount.toString()
        )
        startForeground(1, customNotification)
    }

    private fun startTimer() {
        stopForeground(true)

        changeTimerRunningState(true)

        startTimer {
            timeCount.count++
            val timerIntent = Intent().apply {
                action = MOMENT_ACTION_NAME
                putExtra(TIME_COUNT_INTENT_EXTRA, timeCount)
            }
            sendBroadcast(timerIntent)
        }
    }

    private fun startTimer(handler: TimerTask.() -> Unit) {
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler()
            }
        }, 0, 1000)
    }

    private fun pauseTimer() {
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