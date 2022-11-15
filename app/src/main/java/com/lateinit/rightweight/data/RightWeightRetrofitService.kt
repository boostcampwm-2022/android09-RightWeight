package com.lateinit.rightweight.data

import com.lateinit.rightweight.R
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RightWeightRetrofitService {

    @Headers("Content-Type: application/json")
    @POST("/accounts:signInWithCustomToken?key=${R.string.default_web_client_id}")
    fun loginToFirebase(
        @Query("token") token: String,
        @Query("returnSecureToken") returnSecureToken: Boolean
    ): LoginResponse
}