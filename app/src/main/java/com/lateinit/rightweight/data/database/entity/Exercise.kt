package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.lateinit.rightweight.data.model.local.ExercisePartType

@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = Day::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"],
            onDelete = CASCADE
        )
    ]
)
data class Exercise(
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
