package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.LoginResponse

interface LoginDataSource {

    suspend fun login(key: String, token: String): LoginResponse

    suspend fun deleteAccount(key: String, idToken: String)
}