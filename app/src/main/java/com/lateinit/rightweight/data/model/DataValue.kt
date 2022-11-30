package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

data class StringValue(
    @SerializedName("stringValue")
    val value: String? = null
)
data class BooleanValue(
    @SerializedName("booleanValue")
    val value: Boolean? = null
)
data class IntValue(
    @SerializedName("integerValue")
    val value: String? = null
)

data class TimeStampValue(
    @SerializedName("timestampValue")
    val value: String? = null
)