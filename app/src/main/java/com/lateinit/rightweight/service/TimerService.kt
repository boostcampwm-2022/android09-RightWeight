package com.lateinit.rightweight.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.lateinit.rightweight.R
import com.lateinit.rightweight.ui.home.HomeActivity
import java.util.*


class TimerService : Service() {
    private var isTimerRunning = false
    private var timeCount = 0 // 원자적으로 동작할 지 의문
    private lateinit var timer: Timer
//    lateinit var foregroundUpdateTimer: Timer
//    lateinit var notificationManager: NotificationManager
//    lateinit var customNotification: Notification
//    lateinit var notificationLayout: RemoteViews

    companion object {
        const val MANAGE_ACTION_NAME = "timer_manage_action"
        const val MOMENT_ACTION_NAME = "timer_moment_action"
        const val STATUS_ACTION_NAME = "timer_status_action"
        const val CHANNEL_ID = "timer_notification"
        const val CHANNEL_NAME = "Timer"
        const val START = "start"
        const val PAUSE = "pause"
        const val STOP = "stop"
        const val START_NOTIFICATION = "start_notification"
        const val STOP_NOTIFICATION = "stop_notification"
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
//        createChannel()
//        setNotification()
//        timer = Timer() // home fragment에서 stop 하려면 넣어야함
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimerService", "$this")
        val action = intent?.getStringExtra(MANAGE_ACTION_NAME)

        when (action) {
            START -> {
                startTimer()
            }
            PAUSE -> {
                pauseTimer()
            }
            STATUS -> {
                sendStatus()
            }
            STOP -> {
                pauseTimer()
//                foregroundUpdateTimer.cancel()
                stopForeground(true)
            }
//            START_NOTIFICATION ->{
//                setNotification()
//            }
//            STOP_NOTIFICATION ->{
//                foregroundUpdateTimer.cancel()
//                stopForeground(true)
//            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


//    private fun createChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationChannel.setSound(null, null)
//            notificationChannel.setShowBadge(true)
//
//            notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }
//
//    private fun setNotification() {
//        notificationLayout = RemoteViews(packageName, R.layout.notification_foreground)
//        notificationLayout.setTextViewText(R.id.text_view_notification_timer, timeCount.toString())
//
//        val screenMoveIntent = Intent(this, HomeActivity::class.java)
//        screenMoveIntent.putExtra(
//            SCREEN_MOVE_INTENT_EXTRA,
//            R.id.action_navigation_home_to_navigation_exercise
//        )
//        val pendingIntent = PendingIntent.getActivity(this, 0, screenMoveIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
//
//        customNotification = NotificationCompat.Builder(this, "timer_notification")
//            .setSmallIcon(R.drawable.img_right_weight)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayout)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        startForeground(1, customNotification)
//
//        foregroundUpdateTimer = Timer()
//
//        foregroundUpdateTimer.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                updateNotification()
//                Log.d("timerIsAlive", "foregroundUpdate +${this}")
//            }
//        }, 0, 1000)
//    }
//
//    private fun updateNotification() {
//        notificationLayout.setTextViewText(
//            R.id.text_view_notification_timer,
//            getTimeString(timeCount)
//        )
//        startForeground(1, customNotification)
//    }

    private fun startTimer() {
        isTimerRunning = true
        sendStatus()

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeCount++
                val timerIntent = Intent().apply {
                    action = MOMENT_ACTION_NAME
                    putExtra(TIME_COUNT_INTENT_EXTRA, timeCount)
                }
                sendBroadcast(timerIntent)
                Log.d("TimerService", "timeCount++ $timeCount")
            }
        }, 0, 1000)
    }

    private fun pauseTimer() {
        timer.cancel()
        isTimerRunning = false
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

    fun getTimeString(timeCount: Int): String {
        val hours: Int = (timeCount / 60) / 60
        val minutes: Int = timeCount / 60
        val seconds: Int = timeCount % 60
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

}