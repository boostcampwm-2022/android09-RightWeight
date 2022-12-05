package com.lateinit.rightweight.data.remote.model

import com.google.gson.annotations.SerializedName

data class StringValue(
    @field:SerializedName("stringValue")
    val value: String? = null
)
data class BooleanValue(
    @field:SerializedName("booleanValue")
    val value: Boolean? = null
)
data class IntValue(
    @field:SerializedName("integerValue")
    val value: String? = null
)

data class TimeStampValue(
    @field:SerializedName("timestampValue")
    val value: String? = null
)

data class MapValue(
    @field:SerializedName("mapValue")
    val value: MapValueRootField? = null
)