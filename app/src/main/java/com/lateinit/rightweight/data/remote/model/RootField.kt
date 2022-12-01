package com.lateinit.rightweight.data.remote.model

import com.google.gson.annotations.SerializedName

data class RootField(
    @field:SerializedName("fields")
    val remoteData: RemoteData? = null
)
