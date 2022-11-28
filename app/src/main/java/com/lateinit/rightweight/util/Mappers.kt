package com.lateinit.rightweight.util

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.intermediate.ExerciseWithSets
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel

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
        title =  title,
        order =  order,
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