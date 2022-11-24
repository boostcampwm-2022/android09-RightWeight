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
    var weight: String,
    @ColumnInfo(name = "count")
    var count: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "checked")
    var checked: Boolean
)
