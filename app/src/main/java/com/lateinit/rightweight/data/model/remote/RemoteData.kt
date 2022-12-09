package com.lateinit.rightweight.data.remote.model

import com.google.gson.annotations.SerializedName

sealed class RemoteData

data class SharedRoutineField(
    @SerializedName("author")
    val author: StringValue,

    @SerializedName("description")
    val description: StringValue,

    @SerializedName("modified_date")
    val modifiedDate: TimeStampValue,

    @SerializedName("order")
    val order: IntValue,

    @SerializedName("title")
    val title: StringValue,

    @SerializedName("userId")
    val userId: StringValue,

    @SerializedName("shared_count")
    val sharedCount: MapValue,
) : RemoteData()

data class SharedCount(
    @SerializedName("time")
    val time: TimeStampValue,

    @SerializedName("count")
    val count: IntValue,
) : RemoteData()

data class RoutineField(
    @SerializedName("author")
    val author: StringValue,

    @SerializedName("description")
    val description: StringValue,

    @SerializedName("modified_date")
    val modifiedDate: TimeStampValue,

    @SerializedName("order")
    val order: IntValue,

    @SerializedName("title")
    val title: StringValue,

    @SerializedName("userId")
    val userId: StringValue,
) : RemoteData()

data class DayField(
    @SerializedName("order")
    val order: IntValue,

    @SerializedName("routine_id")
    val routineId: StringValue,
) : RemoteData()

data class ExerciseField(
    @SerializedName("order")
    val order: IntValue,

    @SerializedName("part_type")
    val partType: StringValue,

    @SerializedName("title")
    val title: StringValue,

    @SerializedName("day_id")
    val dayId: StringValue,
) : RemoteData()

data class ExerciseSetField(
    @SerializedName("order")
    val order: IntValue,

    @SerializedName("count")
    val count: StringValue,

    @SerializedName("weight")
    val weight: StringValue,

    @SerializedName("exercise_id")
    val exerciseId: StringValue,
) : RemoteData()

data class UserInfoField(
    @SerializedName("selected_routine_id")
    val routineId: StringValue,
    @SerializedName("selected_day_id")
    val dayId: StringValue
) : RemoteData()

data class HistoryField(
    @SerializedName("date")
    val date: TimeStampValue,
    @SerializedName("time")
    val time: StringValue,
    @SerializedName("routine_title")
    val routineTitle: StringValue,
    @SerializedName("order")
    val order: IntValue,
    @SerializedName("routine_id")
    val routineId: StringValue,
) : RemoteData()

data class HistoryExerciseField(
    @SerializedName("history_id")
    val historyId: StringValue,
    @SerializedName("title")
    val title: StringValue,
    @SerializedName("order")
    val order: IntValue,
    @SerializedName("part")
    val part: StringValue,
) : RemoteData()

data class HistoryExerciseSetField(
    @SerializedName("exercise_id")
    val exerciseId: StringValue,
    @SerializedName("weight")
    var weight: StringValue,
    @SerializedName("count")
    var count: StringValue,
    @SerializedName("order")
    val order: IntValue,
) : RemoteData()