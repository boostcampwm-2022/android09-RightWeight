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
    val sharedCount: MapValue? = null,
): RemoteData()

data class SharedCount(
    @field:SerializedName("time")
    val time: TimeStampValue? = null,

    @field:SerializedName("count")
    val count: IntValue? = null,
): RemoteData()

data class RoutineField(
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
): RemoteData()

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

data class UserInfoField(
    @SerializedName("selected_routine_id")
    val routineId: StringValue? = null,
    @SerializedName("selected_day_id")
    val dayId: StringValue? = null
) : RemoteData()

data class SharedCountField(
    @SerializedName("shared_count")
    val sharedCount: MapValue? = null,
) : RemoteData()