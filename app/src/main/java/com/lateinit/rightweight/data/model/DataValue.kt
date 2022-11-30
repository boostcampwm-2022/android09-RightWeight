package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

sealed class DataValue

data class StringValue(
    @SerializedName("stringValue")
    val value: String? = null
):DataValue()

data class BooleanValue(
    @SerializedName("booleanValue")
    val value: Boolean? = null
):DataValue()

data class IntValue(
    @SerializedName("integerValue")
    val value: String? = null
):DataValue()

data class TimeStampValue(
    @SerializedName("timestampValue")
    val value: String? = null
)