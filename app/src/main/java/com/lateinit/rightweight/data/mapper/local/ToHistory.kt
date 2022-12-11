package com.lateinit.rightweight.data.mapper

import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.data.remote.model.HistoryExerciseField
import com.lateinit.rightweight.data.remote.model.HistoryExerciseSetField
import com.lateinit.rightweight.data.remote.model.HistoryField
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import com.lateinit.rightweight.util.DEFAULT_SET_COUNT
import com.lateinit.rightweight.util.DEFAULT_SET_WEIGHT
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun HistoryUiModel.toHistory(): History {
    return History(
        historyId = historyId,
        date = date,
        time = time,
        routineTitle = routineTitle,
        dayOrder = order,
        completed = completed
    )
}

fun HistoryExerciseUiModel.toHistoryExercise(): HistoryExercise {
    return HistoryExercise(
        exerciseId = exerciseId,
        historyId = historyId,
        title = title,
        order = order,
        part = part.toExercisePartType()
    )
}

fun HistoryExerciseSetUiModel.toHistorySet(): HistorySet {
    return HistorySet(
        setId = setId,
        exerciseId = exerciseId,
        weight = weight.ifEmpty { DEFAULT_SET_WEIGHT },
        count = count.ifEmpty { DEFAULT_SET_COUNT },
        order = order,
        checked = checked
    )
}

fun HistoryField.toHistory(historyId: String): History {
    val refinedDateString = date.value
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val date = LocalDate.parse(refinedDateString, formatter)

    return History(
        historyId = historyId,
        date = date,
        time = time.value,
        routineTitle = routineTitle.value,
        dayOrder = order.value.toInt(),
        completed = true,
        routineId = routineId.value
    )
}

fun HistoryExerciseField.toHistoryExercise(exerciseId: String): HistoryExercise {
    return HistoryExercise(
        exerciseId = exerciseId,
        historyId = historyId.value,
        title = title.value,
        order = order.value.toInt(),
        part = ExercisePartType.valueOf(part.value)
    )
}

fun HistoryExerciseSetField.toHistorySet(exerciseSetId: String): HistorySet {
    return HistorySet(
        setId = exerciseSetId,
        exerciseId = exerciseId.value,
        weight = weight.value,
        count = count.value,
        order = order.value.toInt(),
        checked = true
    )
}