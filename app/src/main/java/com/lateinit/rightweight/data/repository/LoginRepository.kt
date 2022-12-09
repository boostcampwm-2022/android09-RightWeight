package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.model.remote.LoginResponse

interface LoginRepository {

    suspend fun login(key: String, token: String): LoginResponse

    suspend fun deleteAccount(key: String, idToken: String)
}