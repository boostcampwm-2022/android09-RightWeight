package com.lateinit.rightweight.ui.mapper

import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryExerciseWithHistorySets
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel

fun HistoryWithHistoryExercises.toHistoryUiModel(): HistoryUiModel {
    return HistoryUiModel(
        historyId = history.historyId,
        date = history.date,
        time = history.time,
        routineTitle = history.routineTitle,
        order = history.dayOrder,
        completed = history.completed,
        exercises = historyExercises.map { it.toHistoryExerciseUiModel() }
    )
}

fun HistoryExerciseWithHistorySets.toHistoryExerciseUiModel(): HistoryExerciseUiModel {
    return HistoryExerciseUiModel(
        exerciseId = historyExercise.exerciseId,
        historyId = historyExercise.historyId,
        title = historyExercise.title,
        order = historyExercise.order,
        part = historyExercise.part.toExercisePartTypeUiModel(),
        exerciseSets = historySets.map { it.toHistoryExerciseSetUiModel() }
    )
}

fun HistorySet.toHistoryExerciseSetUiModel(): HistoryExerciseSetUiModel {
    return HistoryExerciseSetUiModel(
        setId = setId,
        exerciseId = exerciseId,
        weight = weight,
        count = count,
        order = order,
        checked = checked
    )
}
