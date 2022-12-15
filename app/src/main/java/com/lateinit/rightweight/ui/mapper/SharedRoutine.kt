package com.lateinit.rightweight.ui.mapper

import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineExerciseWithExerciseSets
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
import com.lateinit.rightweight.ui.model.shared.SharedRoutineUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION

fun SharedRoutine.toSharedRoutineUiModel(): SharedRoutineUiModel {
    return SharedRoutineUiModel(
        routineId = routineId,
        title = title,
        author = author,
        description = description,
        modifiedDate = modifiedDate,
        sharedCount = sharedCount
    )
}

fun SharedRoutineDay.toDayUiModel(
    exercises: List<SharedRoutineExerciseWithExerciseSets>
): DayUiModel {
    return DayUiModel(
        dayId = dayId,
        routineId = routineId,
        order = order,
        selected = order == FIRST_DAY_POSITION,
        exercises = exercises.map { it.toExerciseUiModel() }
    )
}

fun SharedRoutineExerciseWithExerciseSets.toExerciseUiModel(): ExerciseUiModel {
    return ExerciseUiModel(
        exerciseId = exercise.exerciseId,
        dayId = exercise.dayId,
        title = exercise.title,
        order = exercise.order,
        part = exercise.part.toExercisePartTypeUiModel(),
        exerciseSets = sets.map { it.toExerciseSetUiModel() }
    )
}

fun SharedRoutineExerciseSet.toExerciseSetUiModel(): ExerciseSetUiModel {
    return ExerciseSetUiModel(
        setId = setId,
        exerciseId = exerciseId,
        weight = weight,
        count = count,
        order = order
    )
}