package com.lateinit.rightweight.data.model.remote

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("id_token")
    val idToken: String
)