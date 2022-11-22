package com.lateinit.rightweight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.lateinit.rightweight.R
import java.util.*

class TimerService: Service() {
    private var isTimerRunning = false
    private var timeCount = 0
    lateinit var timer: Timer
    lateinit var foregroundUpdateTimer: Timer
    lateinit var notificationManager: NotificationManager
    lateinit var customNotification: Notification
    lateinit var notificationLayout: RemoteViews

    companion object{
        const val MANAGE_ACTION_NAME = "timer_manage_action"
        const val MOMENT_ACTION_NAME = "timer_moment_action"
        const val STATUS_ACTION_NAME = "timer_status_action"
        const val CHANNEL_ID = "timer_notification"
        const val CHANNEL_NAME = "Timer"
        const val START = "start"
        const val PAUSE = "pause"
        const val STATUS = "status"
        const val STOP = "stop"
        const val TIME_COUNT_INTENT_EXTRA = "time_count"
        const val IS_TIMER_RUNNING_INTENT_EXTRA = "is_timer_running"
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        setNotification()

        val action = intent?.getStringExtra(MANAGE_ACTION_NAME)!!

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
            STOP ->{
                pauseTimer()
                stopNotification()
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
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setNotification(){
        notificationLayout = RemoteViews(packageName, R.layout.notification_foreground)
        notificationLayout.setTextViewText(R.id.text_view_notification_timer, timeCount.toString())

        customNotification = NotificationCompat.Builder(this, "timer_notification")
            .setSmallIcon(R.drawable.img_right_weight)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .build()

        startForeground(1, customNotification)

        foregroundUpdateTimer = Timer()

        foregroundUpdateTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateNotification()
            }
        }, 0, 1000)
    }

    private fun stopNotification(){
        foregroundUpdateTimer.cancel()
        stopForeground(true)
    }

    private fun updateNotification(){
        notificationLayout.setTextViewText(R.id.text_view_notification_timer, getTimeString(timeCount))
        startForeground(1, customNotification)
    }

    private fun startTimer() {
        isTimerRunning = true
        sendStatus()

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeCount++
                val timerIntent = Intent()
                timerIntent.action = MOMENT_ACTION_NAME
                timerIntent.putExtra(TIME_COUNT_INTENT_EXTRA, timeCount)
                sendBroadcast(timerIntent)
            }
        }, 0, 1000)
    }

    private fun pauseTimer() {
        timer.cancel()
        isTimerRunning = false
        sendStatus()
    }

    private fun sendStatus() {
        val statusIntent = Intent()
        statusIntent.action = STATUS_ACTION_NAME
        statusIntent.putExtra(IS_TIMER_RUNNING_INTENT_EXTRA, isTimerRunning)
        statusIntent.putExtra(TIME_COUNT_INTENT_EXTRA, timeCount)
        sendBroadcast(statusIntent)
    }

    fun getTimeString(timeCount: Int): String{
        val hours: Int = (timeCount / 60) / 60
        val minutes: Int = timeCount / 60
        val seconds: Int = timeCount % 60
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

}