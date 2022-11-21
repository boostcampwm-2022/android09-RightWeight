package com.lateinit.rightweight.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import java.util.*

class TimerService: Service() {
    private var isTimerRunning = false
    private var timeCount = 0
    lateinit var timer: Timer


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()

        val action = intent?.getStringExtra("timer_manage_action")!!

        when (action) {
            "start" -> startTimer()
            "status" -> sendStatus()
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "timer_notification",
                "Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun startTimer() {
        isTimerRunning = true

        sendStatus()

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeCount++
                val timerIntent = Intent()
                timerIntent.action = "timer_moment"
                timerIntent.putExtra("time_count", timeCount)
                sendBroadcast(timerIntent)
            }
        }, 0, 1000)
    }

    private fun sendStatus() {
        val statusIntent = Intent()
        statusIntent.action = "timer_status"
        statusIntent.putExtra("is_timer_running", isTimerRunning)
        statusIntent.putExtra("time_count", timeCount)
        sendBroadcast(statusIntent)
    }

}