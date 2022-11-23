package com.lateinit.rightweight.data.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet

interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(
        history: History,
        exercises: List<HistoryExercise>,
        sets: List<HistorySet>
    )
}