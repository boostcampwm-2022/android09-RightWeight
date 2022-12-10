package com.lateinit.rightweight.data.repository.impl

import com.lateinit.rightweight.data.datasource.local.UserLocalDataSource
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.model.local.User
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.mapper.toHistoryUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
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