package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lateinit.rightweight.data.model.local.ExercisePartType

@Entity(
    tableName = "history_exercise",
    foreignKeys = [
        ForeignKey(
            entity = History::class,
            parentColumns = ["history_id"],
            childColumns = ["history_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HistoryExercise(
    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "history_id")
    val historyId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "part")
    val part: ExercisePartType
)