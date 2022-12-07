package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>

    suspend fun backupUserInfo(user: User)

    suspend fun commitTransaction(writes: List<WriteModelData>)
}