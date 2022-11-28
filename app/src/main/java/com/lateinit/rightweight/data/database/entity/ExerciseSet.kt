package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_set",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exercise_id"],
            onDelete = CASCADE
        )
    ]
)
data class ExerciseSet(
    @PrimaryKey
    @ColumnInfo(name = "set_id")
    val setId: String,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "weight")
    val weight: String,
    @ColumnInfo(name = "count")
    val count: String,
    @ColumnInfo(name = "order")
    val order: Int
)