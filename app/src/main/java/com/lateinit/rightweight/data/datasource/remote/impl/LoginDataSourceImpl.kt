package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.AuthApiService
import com.lateinit.rightweight.data.api.TokenApiService
import com.lateinit.rightweight.data.datasource.remote.LoginDataSource
import com.lateinit.rightweight.data.model.remote.LoginRequestBody
import com.lateinit.rightweight.data.model.remote.LoginResponse
import com.lateinit.rightweight.data.model.remote.PostBody
import com.lateinit.rightweight.data.model.remote.RefreshTokenResponse
import com.lateinit.rightweight.ui.MainViewModel
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val tokenApi: TokenApiService
) : LoginDataSource {

    override suspend fun login(key: String, token: String): LoginResponse {
        val postBody = PostBody(token, "google.com").toString()
        return authApi.loginToFirebase(
            key, LoginRequestBody(
                postBody, "http://localhost",
                returnIdpCredential = true,
                returnSecureToken = true
            )
        )
    }

    override suspend fun deleteAccount(key: String, idToken: String) {
        val deleteAccount = authApi.deleteAccount(key, idToken)
        if (deleteAccount.isSuccessful.not()) {
            throw MainViewModel.IdTokenExpiredException("IdToken expired.")
        }
    }

    override suspend fun refreshIdToken(key: String, refreshToken: String): RefreshTokenResponse {
        return tokenApi.refreshIdToken(key, refreshToken)
    }
}

