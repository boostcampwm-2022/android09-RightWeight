package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.local.User
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.ui.model.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRepository {

    suspend fun saveUser(user: User)

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    suspend fun getUserRoutineIds(userId: String): List<String>

    suspend fun getLatestHistoryDate(userId: String): LocalDate?

    suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryUiModel>

    suspend fun getChildrenDocumentName(path: String): List<String>

    fun getUser(): Flow<User?>

    suspend fun commitTransaction(writes: List<WriteModelData>)

    suspend fun backupUserInfo(user: User)
}