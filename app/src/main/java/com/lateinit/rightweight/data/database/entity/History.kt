package com.lateinit.rightweight.data.database.entity

import androidx.room.*
import java.time.LocalDate

@Entity(
    tableName = "history",
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
    val completed: Boolean
)