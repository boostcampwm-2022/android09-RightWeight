package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName

sealed class RemoteData

data class SharedRoutineField(
    @SerializedName("author")
    val author: StringValue? = null,

    @SerializedName("description")
    val description: StringValue? = null,

    @SerializedName("modified_date")
    val modifiedDate: TimeStampValue? = null,

    @SerializedName("order")
    val order: IntValue? = null,

    @SerializedName("title")
    val title: StringValue? = null,

    @SerializedName("userId")
    val userId: StringValue? = null,

    @SerializedName("shared_count")
    val sharedCount: IntValue? = null,
): RemoteData()