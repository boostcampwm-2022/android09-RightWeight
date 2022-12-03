package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "shared_routine_day",
    foreignKeys = [
        ForeignKey(
            entity = SharedRoutine::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_id"],
        )
    ]
)
data class SharedRoutineDay(
    @PrimaryKey
    @ColumnInfo(name = "day_id")
    val dayId: String,
    @ColumnInfo(name = "routine_id")
    val routineId: String,
    @ColumnInfo(name = "order")
    val order: Int
)