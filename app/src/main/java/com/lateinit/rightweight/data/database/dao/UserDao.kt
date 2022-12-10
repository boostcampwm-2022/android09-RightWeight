package com.lateinit.rightweight.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import java.time.LocalDate

@Dao
interface UserDao {

    @Transaction
    @Query("SELECT * FROM routine")
    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    @Transaction
    @Query("SELECT * FROM history WHERE date > :startDate")
    suspend fun getHistoryAfterDate(
        startDate: LocalDate,
    ): List<HistoryWithHistoryExercises>
}