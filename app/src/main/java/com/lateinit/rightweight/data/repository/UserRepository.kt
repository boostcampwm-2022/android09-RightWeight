package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.model.User

interface UserRepository {
    suspend fun setUser(user: User?)
    suspend fun getUser(): User
    suspend fun setLoginResponse(loginResponse: LoginResponse?)
    suspend fun getLoginResponse(): LoginResponse?
}