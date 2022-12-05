package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>
}