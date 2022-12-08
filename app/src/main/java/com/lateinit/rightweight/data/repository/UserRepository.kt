package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.ui.model.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRepository {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>

    suspend fun backupUserInfo(user: User)

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    suspend fun getUserRoutineInRemote(userId: String): List<String>

    suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryUiModel>

    suspend fun getChildrenDocumentName(path: String): List<String>

    suspend fun commitTransaction(writes: List<WriteModelData>)
}