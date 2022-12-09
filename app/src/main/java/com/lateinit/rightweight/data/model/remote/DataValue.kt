package com.lateinit.rightweight.data.remote.model

import com.google.gson.annotations.SerializedName

sealed class ValueData

data class StringValue(
    @SerializedName("stringValue")
    val value: String? = null
): ValueData()

data class IntValue(
    @SerializedName("integerValue")
    val value: String? = null
): ValueData()

data class TimeStampValue(
    @SerializedName("timestampValue")
    val value: String? = null
): ValueData()

data class MapValue(
    @SerializedName("mapValue")
    val value: MapValueRootField? = null
): ValueData()