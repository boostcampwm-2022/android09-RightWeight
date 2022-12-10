package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lateinit.rightweight.data.model.local.ExercisePartType

@Entity(
    tableName = "shared_routine_exercise",
    foreignKeys = [
        ForeignKey(
            entity = SharedRoutineDay::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SharedRoutineExercise(
    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "day_id")
    val dayId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "part")
    val part: ExercisePartType
)