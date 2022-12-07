package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.datasource.UserDataSource
import com.lateinit.rightweight.data.datasource.UserRemoteDataSource
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun saveUser(user: User) {
        userDataSource.saveUser(user)
    }

    override fun getUser(): Flow<User?> {
        return userDataSource.getUser()
    }

    override suspend fun backupUserInfo(user: User) {
        userRemoteDataSource.backupUserInfo(user.userId, user.routineId, user.dayId)
    }
    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        userRemoteDataSource.commitTransaction(writes)
    }
}