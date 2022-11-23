package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_set",
    foreignKeys = [
        ForeignKey(
            entity = HistoryExercise::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HistorySet(
    @PrimaryKey
    @ColumnInfo(name = "set_id")
    val setId: String,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "weight")
    val weight: Int,
    @ColumnInfo(name = "number")
    val number: Int,
    @ColumnInfo(name = "order")
    val order: String,
    @ColumnInfo(name = "checked")
    val checked: Boolean
)
