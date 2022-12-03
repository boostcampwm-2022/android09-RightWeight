package com.lateinit.rightweight.data.remote.model.sharedRoutine

import com.google.gson.annotations.SerializedName
import com.lateinit.rightweight.data.remote.model.SharedRoutineField

data class SharedRoutineItem(

    @field:SerializedName("createTime")
    val createTime: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("updateTime")
    val updateTime: String? = null,

    @field:SerializedName("fields")
    val fields: SharedRoutineField? = null
)
