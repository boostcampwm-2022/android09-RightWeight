package com.lateinit.rightweight.data.repository.impl

import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.datasource.UserLocalDataSource
import com.lateinit.rightweight.data.datasource.UserRemoteDataSource
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.util.toHistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun saveUser(user: User) {
        userLocalDataSource.saveUser(user)
    }

    override suspend fun getAllRoutineWithDays(): List<RoutineWithDays> {
        return userLocalDataSource.getAllRoutineWithDays()
    }

    override suspend fun getUserRoutineIds(userId: String): List<String> {
        val documentsResponseList = userRemoteDataSource.getUserRoutineInRemote(userId)
        val documents = documentsResponseList.map { it.document }
        return documents
            .filterNotNull()
            .map {
                it.name.split("/").last()
            }
    }

    override suspend fun getLatestHistoryDate(userId: String): LocalDate? {
        val documentsResponseList = userRemoteDataSource.getLastHistoryInServer(userId)
        val lastDateTime =
            documentsResponseList.first()
                .document?.fields?.date?.value
                ?.replace("Z", "")
                ?: return null
        return LocalDateTime.parse(lastDateTime).toLocalDate()
    }

    override suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryUiModel> {
        return userLocalDataSource.getHistoryAfterDate(startDate).map { it.toHistoryUiModel() }
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return userRemoteDataSource.getChildrenDocumentName(path)
    }

    override fun getUser(): Flow<User?> {
        return userLocalDataSource.getUser()
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        userRemoteDataSource.commitTransaction(writes)
    }

    override suspend fun backupUserInfo(user: User) {
        userRemoteDataSource.backupUserInfo(user.userId, user.routineId, user.dayId)
    }
}