package com.lateinit.rightweight.util

fun convertTimeStamp(count: Int): String {
    val hours: Int = (count / 60) / 60
    val minutes: Int = (count / 60) % 60
    val seconds: Int = count % 60

    return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
}