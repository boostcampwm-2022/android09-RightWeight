package com.lateinit.rightweight.data.datasource

interface LoginDataSource {

    suspend fun loginToFirebase(key: String, token: String)
}