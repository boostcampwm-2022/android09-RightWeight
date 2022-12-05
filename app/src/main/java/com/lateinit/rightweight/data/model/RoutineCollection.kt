package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

data class RoutineCollection(
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("order")
    val order: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("modified_date")
    val modifiedDate: String
)