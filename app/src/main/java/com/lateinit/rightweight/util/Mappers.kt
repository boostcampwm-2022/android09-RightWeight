package com.lateinit.rightweight.util

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel

fun Day.toDayUiModel(): DayUiModel {
    return DayUiModel(
        dayId = dayId,
        routineId = routineId,
        order = order,
        exercises = exercises.map { it.toExerciseUiModel() },
    )
}


fun Exercise.toExerciseUiModel(): ExerciseUiModel {
    return ExerciseUiModel(
        exerciseId = exerciseId,
        dayId = dayId,
        title = title,
        order = order,
        part = part,
        exerciseSets = exerciseSets.map{ it.toExerciseSetUiModel() }
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