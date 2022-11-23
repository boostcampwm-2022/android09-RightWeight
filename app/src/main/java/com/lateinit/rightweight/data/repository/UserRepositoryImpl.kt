package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.datasource.UserDataSource
import com.lateinit.rightweight.data.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun setUser(user: User) {
        userDataSource.setUser(user)
    }

    override suspend fun getUser(): User {
        return userDataSource.getUser()
    }
}