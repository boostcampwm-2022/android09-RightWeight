package com.lateinit.rightweight.data

import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.datasource.LoginRequestBody
import retrofit2.http.*

interface RightWeightRetrofitService {

    @POST("./accounts:signInWithIdp")
    suspend fun loginToFirebase(
        @Query("key") key: String,
        @Body body: LoginRequestBody,
    ): LoginResponse
}