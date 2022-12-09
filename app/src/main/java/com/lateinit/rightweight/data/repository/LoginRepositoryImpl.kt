package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.datasource.LoginDataSource
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginDataSource: LoginDataSource
) : LoginRepository {

    override suspend fun login(key: String, token: String): LoginResponse {
        return loginDataSource.login(key, token)
    }

    override suspend fun deleteAccount(key: String, idToken: String) {
        loginDataSource.deleteAccount(key, idToken)
    }
}