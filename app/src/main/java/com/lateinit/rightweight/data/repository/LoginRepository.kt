package com.lateinit.rightweight.data.repository

interface LoginRepository {

    suspend fun loginToFirebase(key: String, token: String)
}