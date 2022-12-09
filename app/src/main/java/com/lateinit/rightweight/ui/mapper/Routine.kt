package com.lateinit.rightweight.ui.mapper

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.ExerciseWithSets
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION

fun Routine.toRoutineUiModel(): RoutineUiModel {
    return RoutineUiModel(
        routineId = routineId,
        title = title,
        author = author,
        description = description,
        modifiedDate = modifiedDate,
        order = order
    )
}

fun Day.toDayUiModel(index: Int, exerciseWithSets: List<ExerciseWithSets>): DayUiModel {
    return DayUiModel(
        dayId = dayId,
        routineId = routineId,
        order = order,
        selected = index == FIRST_DAY_POSITION,
        exercises = exerciseWithSets.map { it.toExerciseUiModel() }
    )
}

fun DayWithExercises.toDayUiModel(): DayUiModel {
    return DayUiModel(
        dayId = day.dayId,
        routineId = day.routineId,
        order = day.order,
        selected = day.order == FIRST_DAY_POSITION,
        exercises = exercises.map { it.toExerciseUiModel() }
    )
}

fun ExerciseWithSets.toExerciseUiModel(): ExerciseUiModel {
    return ExerciseUiModel(
        exerciseId = exercise.exerciseId,
        dayId = exercise.dayId,
        title = exercise.title,
        order = exercise.order,
        part = exercise.part.toExercisePartTypeUiModel(),
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

