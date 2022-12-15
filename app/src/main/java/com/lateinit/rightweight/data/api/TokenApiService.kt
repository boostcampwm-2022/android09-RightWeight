package com.lateinit.rightweight.data.api

import com.lateinit.rightweight.data.model.remote.RefreshTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface TokenApiService {
    @FormUrlEncoded
    @POST("token")
    suspend fun refreshIdToken(
        @Query("key") key: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): RefreshTokenResponse
}