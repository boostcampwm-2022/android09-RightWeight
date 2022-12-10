package com.lateinit.rightweight.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import kotlinx.coroutines.flow.Flow
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
    suspend fun insertHistorySet(set: HistorySet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryExercise(historyExercise: HistoryExercise)

    @Query("SELECT * FROM history WHERE date = :localDate")
    fun getHistoryByDate(localDate: LocalDate) : Flow<History>

    @Query("SELECT * FROM history ORDER BY date DESC LIMIT 1")
    fun getLatestHistory() : History

    @Transaction
    @Query("SELECT * FROM history WHERE date > :startDate")
    suspend fun getHistoryAfterDate(
        startDate: LocalDate,
    ): List<HistoryWithHistoryExercises>

    @Transaction
    @Query("SELECT * FROM history WHERE date = :localDate")
    fun getHistoryWithHistoryExercisesByDate(localDate: LocalDate) : Flow<HistoryWithHistoryExercises?>

    @Transaction
    @Query("SELECT * FROM history WHERE date BETWEEN :startDate AND :endDate")
    fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>>

    @Query("SELECT COALESCE(MAX(`order`), 0) FROM history_exercise")
    suspend fun getMaxHistoryExerciseOrder(): Int

    @Query("SELECT COALESCE(MAX(`order`), 0) FROM history_set")
    suspend fun getMaxHistorySetOrder(): Int

    @Update
    suspend fun updateHistory(history: History)

    @Update
    suspend fun updateHistorySet(set: HistorySet)

    @Update
    suspend fun updateHistoryExercise(historyExercise: HistoryExercise)

    @Query("DELETE FROM history_set WHERE set_id = :historySetId")
    suspend fun removeHistorySet(historySetId: String)

    @Query("DELETE FROM history_exercise WHERE exercise_id = :historyExerciseId")
    suspend fun removeHistoryExercise(historyExerciseId: String)
    
    @Query("DELETE FROM history")
    suspend fun removeAllHistories()
}