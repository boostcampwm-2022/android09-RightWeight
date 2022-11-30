package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

data class Documents<T>(
    @SerializedName("name")
    val name: String,
    @SerializedName("fields")
    val fields: List<T>
)