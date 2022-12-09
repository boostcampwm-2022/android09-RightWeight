package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.model.remote.LoginRequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("./accounts:signInWithIdp")
    suspend fun loginToFirebase(
        @Query("key") key: String,
        @Body body: LoginRequestBody,
    ): LoginResponse

    @FormUrlEncoded
    @POST("./accounts:delete")
    suspend fun deleteAccount(
        @Query("key") key: String,
        @Field("idToken") idToken: String
    )
}