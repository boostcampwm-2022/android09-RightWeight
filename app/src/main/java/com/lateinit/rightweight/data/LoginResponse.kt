package com.lateinit.rightweight.data

data class LoginResponse(
    val federatedId: String,
    val providerId: String,
    val localId: String,
    val emailVerified: String,
    val email: String,
    val oauthIdToken: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val displayName: String,
    val idToken: String,
    val photoUrl: String,
    val refreshToken: String,
    val expiresIn: String,
    val rawUserInfo: String
)