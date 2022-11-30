package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

data class RootField(
    @SerializedName("fields")
    val remoteData: RemoteData? = null
)