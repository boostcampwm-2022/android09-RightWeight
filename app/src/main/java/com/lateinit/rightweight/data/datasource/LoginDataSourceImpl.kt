package com.lateinit.rightweight.data.datasource

import android.util.Log
import com.google.gson.Gson
import com.lateinit.rightweight.data.RightWeightRetrofitService
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    val api: RightWeightRetrofitService
) : LoginDataSource {

    override suspend fun loginToFirebase(key: String, token: String){

        val postBody = PostBody(token, "google.com").toString()

        val loginResponse = api.loginToFirebase(key, LoginRequestBody(postBody, "http://localhost", true, true))
        Log.d("loginResponse", loginResponse.toString())
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
){
    override fun toString(): String {
        return "id_token=$id_token&providerId=$providerId"
    }
}