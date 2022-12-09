package com.lateinit.rightweight.data.datasource.local

import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.local.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserLocalDataSource {

    suspend fun saveUser(user: User)

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryWithHistoryExercises>

    fun getUser(): Flow<User?>
}