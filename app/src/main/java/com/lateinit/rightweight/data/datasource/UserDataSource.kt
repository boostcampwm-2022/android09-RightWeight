package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun setUser(user: User?)
    suspend fun getUser(): User
    fun setLoginResponse(loginResponse: LoginResponse?)
    fun getLoginResponse(): LoginResponse?
}