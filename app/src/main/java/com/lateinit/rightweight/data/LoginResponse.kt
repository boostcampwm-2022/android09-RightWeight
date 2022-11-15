package com.lateinit.rightweight.data

data class LoginResponse(
    val idToken: String?,
    val refreshToken: String?,
    val expiresIn: String?,
)