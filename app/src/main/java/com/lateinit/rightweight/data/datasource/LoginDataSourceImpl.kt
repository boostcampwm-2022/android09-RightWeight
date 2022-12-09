package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.AuthApiService
import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.model.LoginRequestBody
import com.lateinit.rightweight.data.model.PostBody
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    val api: AuthApiService
) : LoginDataSource {

    override suspend fun login(key: String, token: String): LoginResponse {
        val postBody = PostBody(token, "google.com").toString()
        return api.loginToFirebase(
            key, LoginRequestBody(
                postBody, "http://localhost",
                returnIdpCredential = true,
                returnSecureToken = true
            )
        )
    }

    override suspend fun deleteAccount(key: String, idToken: String) {
        api.deleteAccount(key, idToken)
    }
}