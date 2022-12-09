package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.LoginResponse

interface LoginRepository {

    suspend fun login(key: String, token: String): LoginResponse

    suspend fun deleteAccount(key: String, idToken: String)
}