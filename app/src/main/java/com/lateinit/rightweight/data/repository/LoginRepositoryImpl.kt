package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.RightWeightRetrofitService
import com.lateinit.rightweight.data.datasource.LoginDataSource
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    val loginDataSource: LoginDataSource
) : LoginRepository {

    override suspend fun loginToFirebase(key: String, token: String): LoginResponse {
        return loginDataSource.loginToFirebase(key, token)
    }
}