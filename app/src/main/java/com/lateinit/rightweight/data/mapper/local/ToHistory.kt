package com.lateinit.rightweight.data.mapper

import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import com.lateinit.rightweight.util.DEFAULT_SET_COUNT
import com.lateinit.rightweight.util.DEFAULT_SET_WEIGHT

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