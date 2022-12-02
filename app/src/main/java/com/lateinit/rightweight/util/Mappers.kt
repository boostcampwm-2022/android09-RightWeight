package com.lateinit.rightweight.util

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.ExerciseWithSets
import com.lateinit.rightweight.data.database.intermediate.HistoryExerciseWithHistorySets
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.model.DetailResponse
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Day.toDayUiModel(index: Int, exerciseWithSets: List<ExerciseWithSets>): DayUiModel {
    return DayUiModel(
        dayId = dayId,
        routineId = routineId,
        order = order,
        selected = index == FIRST_DAY_POSITION,
        exercises = exerciseWithSets.map { it.toExerciseUiModel() }
    )
}

fun ExerciseWithSets.toExerciseUiModel(): ExerciseUiModel {
    return ExerciseUiModel(
        exerciseId = exercise.exerciseId,
        dayId = exercise.dayId,
        title = exercise.title,
        order = exercise.order,
        part = exercise.part,
        exerciseSets = sets.map { it.toExerciseSetUiModel() }
    )
}

fun ExerciseSet.toExerciseSetUiModel(): ExerciseSetUiModel {
    return ExerciseSetUiModel(
        setId = setId,
        exerciseId = exerciseId,
        weight = weight,
        count = count,
        order = order
    )
}

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
        part = historyExercise.part,
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

fun DayUiModel.toDay(): Day {
    return Day(
        dayId = dayId,
        routineId = routineId,
        order = order
    )
}

fun ExerciseUiModel.toExercise(): Exercise {
    return Exercise(
        exerciseId = exerciseId,
        dayId = dayId,
        title = title,
        order = order,
        part = part
    )
}

fun ExerciseSetUiModel.toExerciseSet(): ExerciseSet {
    return ExerciseSet(
        setId = setId,
        exerciseId = exerciseId,
        weight = weight.ifEmpty { DEFAULT_SET_WEIGHT },
        count = count.ifEmpty { DEFAULT_SET_COUNT },
        order = order
    )
}

fun DetailResponse<SharedRoutineField>.toSharedRoutine(): SharedRoutine {
    val splitedName = name.split("/")
    val refinedModifiedDateString = fields.modifiedDate?.value?.replace("T", " ")?.replace("Z", "")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    val modifiedDate = LocalDateTime.parse(refinedModifiedDateString, formatter)
    return SharedRoutine(
        routineId = splitedName.last(),
        title = fields.title?.value.toString(),
        author = fields.author?.value.toString(),
        description = fields.description?.value.toString(),
        modifiedDate = modifiedDate
    )
}