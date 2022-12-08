package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.LoginResponse

interface LoginDataSource {

    suspend fun loginToFirebase(key: String, token: String): LoginResponse
    suspend fun deleteAccount(key: String, idToken: String)
}