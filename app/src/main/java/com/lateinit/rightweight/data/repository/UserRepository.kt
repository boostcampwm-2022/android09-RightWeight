package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.model.local.User
import com.lateinit.rightweight.data.model.remote.WriteModelData
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>

    suspend fun commitTransaction(writes: List<WriteModelData>)

    suspend fun backupUserInfo(user: User)
}