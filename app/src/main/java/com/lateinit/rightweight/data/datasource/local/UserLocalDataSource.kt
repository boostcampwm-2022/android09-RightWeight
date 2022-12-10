package com.lateinit.rightweight.data.datasource.local

import com.lateinit.rightweight.data.model.local.User
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>
}