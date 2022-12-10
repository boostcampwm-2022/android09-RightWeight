package com.lateinit.rightweight.data.remote.model

import com.google.gson.annotations.SerializedName

sealed class ValueData

data class StringValue(
    @SerializedName("stringValue")
    val value: String
): ValueData()

data class IntValue(
    @SerializedName("integerValue")
    val value: String
): ValueData()

data class TimeStampValue(
    @SerializedName("timestampValue")
    val value: String
): ValueData()

data class MapValue(
    @SerializedName("mapValue")
    val value: MapValueRootField
): ValueData()