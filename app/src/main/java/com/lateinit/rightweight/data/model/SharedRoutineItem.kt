package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

data class SharedRoutineItem(
    @SerializedName("createTime")
    val createTime: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("updateTime")
    val updateTime: String? = null,

    @SerializedName("fields")
    val fields: SharedRoutineField? = null
)
