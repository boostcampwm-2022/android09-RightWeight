package com.lateinit.rightweight.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import java.time.LocalDate

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(
        history: History,
        exercises: List<HistoryExercise>,
        sets: List<HistorySet>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorySet(
        set: HistorySet
    )

    @Query("SELECT * FROM history WHERE date = :localDate")
    suspend fun loadHistoryByDate(localDate: LocalDate) : List<History>

    @Query("SELECT * FROM history_exercise WHERE history_id = :historyId")
    suspend fun getHistoryExercisesByHistoryId(historyId: String): List<HistoryExercise>

    @Query("SELECT * FROM history_set WHERE exercise_id = :exerciseId ORDER BY `order`")
    suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): List<HistorySet>

    @Query("DELETE FROM history_set WHERE set_id = :historySetId")
    suspend fun removeHistorySet(historySetId: String)


}