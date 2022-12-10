package com.lateinit.rightweight.data.repository.impl

import com.lateinit.rightweight.data.datasource.local.UserLocalDataSource
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.model.local.User
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        userRemoteDataSource.commitTransaction(writes)
    }

    override suspend fun backupUserInfo(user: User) {
        userRemoteDataSource.backupUserInfo(user.userId, user.routineId, user.dayId)
    }
}