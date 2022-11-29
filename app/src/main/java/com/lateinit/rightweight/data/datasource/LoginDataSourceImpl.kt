package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.AuthService
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    val api: AuthService
) : LoginDataSource {

    override suspend fun loginToFirebase(key: String, token: String): LoginResponse {
        val postBody = PostBody(token, "google.com").toString()
        val loginResponse =
            api.loginToFirebase(key, LoginRequestBody(postBody, "http://localhost", true, true))
        return loginResponse
    }
}

data class LoginRequestBody(
    val postBody: String,
    val requestUri: String,
    val returnIdpCredential: Boolean,
    val returnSecureToken: Boolean
)

data class PostBody(
    val id_token: String,
    val providerId: String,
) {
    override fun toString(): String {
        return "id_token=$id_token&providerId=$providerId"
    }
}