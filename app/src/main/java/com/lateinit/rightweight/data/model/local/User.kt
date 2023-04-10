package com.lateinit.rightweight.data.model.local

data class User(
    val userId: String,
    val routineId: String,
    val dayId: String,
    val completedDayId: String = "",
    val email: String,
    val displayName: String,
    val photoUrl: String,
    val idToken: String,
    val oauthIdToken: String
)
