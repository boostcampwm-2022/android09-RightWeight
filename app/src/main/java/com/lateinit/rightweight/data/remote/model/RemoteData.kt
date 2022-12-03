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

data class SharedRoutineDayField(
    @field:SerializedName("routine_id")
    val routine_id: StringValue? = null,
    @field:SerializedName("order")
    val order: IntValue? = null,
): RemoteData()

data class SharedRoutineExerciseField(
    @field:SerializedName("part_type")
    val partType: StringValue? = null,
    @field:SerializedName("day_id")
    val dayId: StringValue? = null,
    @field:SerializedName("order")
    val order: IntValue? = null,
    @field:SerializedName("title")
    val title: StringValue? = null,
): RemoteData()

data class SharedRoutineExerciseSetField(
    @field:SerializedName("exercise_id")
    val exerciseId: StringValue? = null,
    @field:SerializedName("order")
    val order: IntValue? = null,
    @field:SerializedName("weight")
    val weight: StringValue? = null,
    @field:SerializedName("count")
    val count: StringValue? = null,
): RemoteData()