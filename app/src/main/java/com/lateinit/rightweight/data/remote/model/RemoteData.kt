package com.lateinit.rightweight.data.remote.model

import com.google.gson.annotations.SerializedName

sealed class RemoteData

data class SharedRoutineField(
    @field:SerializedName("author")
    val author: StringValue? = null,

    @field:SerializedName("description")
    val description: StringValue? = null,

    @field:SerializedName("modified_date")
    val modifiedDate: TimeStampValue? = null,

    @field:SerializedName("order")
    val order: IntValue? = null,

    @field:SerializedName("title")
    val title: StringValue? = null,

    @field:SerializedName("userId")
    val userId: StringValue? = null,

    @field:SerializedName("shared_count")
    val sharedCount: IntValue? = null,
): RemoteData()