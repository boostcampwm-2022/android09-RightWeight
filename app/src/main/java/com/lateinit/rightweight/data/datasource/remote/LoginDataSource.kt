package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.model.remote.LoginResponse
import com.lateinit.rightweight.data.model.remote.RefreshTokenResponse

interface LoginDataSource {

    suspend fun login(key: String, token: String): LoginResponse

    suspend fun deleteAccount(key: String, idToken: String)

    suspend fun refreshIdToken(key: String, refreshToken: String): RefreshTokenResponse
}