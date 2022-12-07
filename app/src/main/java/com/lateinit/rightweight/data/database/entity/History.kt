package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_id"]
        )
    ]
)
data class History(
    @PrimaryKey
    @ColumnInfo(name = "history_id")
    val historyId: String,
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "routine_title")
    val routineTitle: String,
    @ColumnInfo(name = "day_order")
    val dayOrder: Int,
    @ColumnInfo(name = "completed")
    val completed: Boolean,
    @ColumnInfo(name = "routine_id")
    val routineId: String
)