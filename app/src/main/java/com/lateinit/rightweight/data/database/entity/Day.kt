package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "day",
    foreignKeys = [
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_id"],
        )
    ]
)
data class Day(
    @PrimaryKey
    @ColumnInfo(name = "day_id")
    val dayId: String,
    @ColumnInfo(name = "routine_id")
    val routineId: String,
    @ColumnInfo(name = "order")
    val order: Int
)