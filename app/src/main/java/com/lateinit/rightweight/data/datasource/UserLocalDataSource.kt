package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserLocalDataSource {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryWithHistoryExercises>
}