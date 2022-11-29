package com.lateinit.rightweight.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "routine")
data class Routine(
    @PrimaryKey
    @ColumnInfo(name = "routine_id")
    val routineId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "modified_date")
    val modifiedDate: LocalDateTime,
    @ColumnInfo(name = "order")
    val order: Int
)
