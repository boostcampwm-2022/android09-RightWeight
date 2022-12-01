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
) : RemoteData()

data class DayField(
    @SerializedName("order")
    val order: IntValue? = null,

    @SerializedName("routine_id")
    val routineId: StringValue? = null,
) : RemoteData()

data class ExerciseField(
    @SerializedName("order")
    val order: IntValue? = null,

    @SerializedName("part_type")
    val partType: StringValue? = null,

    @SerializedName("title")
    val title: StringValue? = null,

    @SerializedName("day_id")
    val dayId: StringValue? = null,
) : RemoteData()

data class ExerciseSetField(
    @SerializedName("order")
    val order: IntValue? = null,

    @SerializedName("count")
    val count: StringValue? = null,

    @SerializedName("weight")
    val weight: StringValue? = null,

    @SerializedName("exercise_id")
    val exerciseId: StringValue? = null,
) : RemoteData()