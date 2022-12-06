package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.datasource.UserDataSource
import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
) : UserRepository {
    override suspend fun saveUser(user: User) {
        userDataSource.saveUser(user)
    }

    override fun getUser(): Flow<User?> {
        return userDataSource.getUser()
    }
}