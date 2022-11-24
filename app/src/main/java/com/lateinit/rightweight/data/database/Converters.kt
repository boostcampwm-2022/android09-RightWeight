package com.lateinit.rightweight.data.database

import androidx.room.TypeConverter
import java.time.*

class Converters {

    @TypeConverter
    fun toLocalDateTime(timestamp: Long): LocalDateTime {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    @TypeConverter
    fun toTimestamp(date: LocalDateTime): Long {
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun toTimestamp(date: LocalDate): Long {
        return date.atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    @TypeConverter
    fun toLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}