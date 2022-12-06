package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.datasource.LoginRequestBody
import com.lateinit.rightweight.data.model.DeleteAccountResponse
import retrofit2.Response
import retrofit2.http.*

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
    ): Response<DeleteAccountResponse>
}