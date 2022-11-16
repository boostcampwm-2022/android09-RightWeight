package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.datasource.LoginDataSource
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    val loginDataSource: LoginDataSource
) : LoginRepository {

    override suspend fun loginToFirebase(key: String, token: String) {
        loginDataSource.loginToFirebase(key, token)
    }
}