package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.model.local.User
import com.lateinit.rightweight.data.remote.model.UserInfoField
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>

    suspend fun removeUserInfo()

    suspend fun restoreUserInfo(userId: String): UserInfoField?

    suspend fun backupUserInfo(user: User)
}