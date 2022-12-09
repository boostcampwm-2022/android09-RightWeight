package com.lateinit.rightweight.data.model


data class LoginRequestBody(
    val postBody: String,
    val requestUri: String,
    val returnIdpCredential: Boolean,
    val returnSecureToken: Boolean
)

data class PostBody(
    val id_token: String,
    val providerId: String,
) {
    override fun toString(): String {
        return "id_token=$id_token&providerId=$providerId"
    }
}