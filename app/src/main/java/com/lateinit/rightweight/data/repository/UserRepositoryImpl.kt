package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.datasource.UserLocalDataSource
import com.lateinit.rightweight.data.datasource.UserRemoteDataSource
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.ui.model.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun saveUser(user: User) {
        userLocalDataSource.saveUser(user)
    }

    override fun getUser(): Flow<User?> {
        return userLocalDataSource.getUser()
    }

    override suspend fun backupUserInfo(user: User) {
        userRemoteDataSource.backupUserInfo(user.userId, user.routineId, user.dayId)
    }

    override suspend fun getAllRoutineWithDays(): List<RoutineWithDays> {
        return userLocalDataSource.getAllRoutineWithDays()
    }

    override suspend fun getUserRoutineInRemote(userId: String): List<String> {
        val documents = userRemoteDataSource.getUserRoutineInRemote(userId)
        return documents.map { documentResponse ->
            val documentName = documentResponse.document?.name ?: ""
            documentName.split("/").last()
        }
    }

    override suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryUiModel> {
        return userLocalDataSource.getHistoryAfterDate(startDate)
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return userRemoteDataSource.getChildrenDocumentName(path)
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        userRemoteDataSource.commitTransaction(writes)
    }
}