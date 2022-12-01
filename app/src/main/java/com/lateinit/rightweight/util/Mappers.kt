package com.lateinit.rightweight.util

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.ExerciseWithSets
import com.lateinit.rightweight.data.model.*
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import java.time.LocalDateTime
import java.util.*

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

fun Routine.toSharedRoutineField(userId: String): SharedRoutineField {
    return SharedRoutineField(
        author = StringValue(author),
        description = StringValue(description),
        modifiedDate = TimeStampValue(modifiedDate.toString() + "Z"),
        order = IntValue(order.toString()),
        title = StringValue(title),
        userId = StringValue(userId)
    )
}

fun SharedRoutineField.toRoutine(): Routine {
    return Routine(
        routineId = UUID.randomUUID().toString(),
        author = author?.value ?: "",
        description = description?.value ?: "",
        modifiedDate = (modifiedDate?.value ?: LocalDateTime.now()) as LocalDateTime,
        order = order?.value?.toInt() ?: Int.MAX_VALUE,
        title = author?.value ?: ""
    )
}

fun DayUiModel.toDayField(): DayField {
    return DayField(
        order = IntValue(order.toString()),
        routineId = StringValue(routineId)
    )
}

fun ExerciseUiModel.toExerciseField(): ExerciseField {
    return ExerciseField(
        order = IntValue(order.toString()),
        partType = StringValue(""),
        title = StringValue(title),
        dayId = StringValue(dayId)
    )
}

fun ExerciseSetUiModel.toExerciseSetField(): ExerciseSetField {
    return ExerciseSetField(
        order = IntValue(order.toString()),
        count = StringValue(count),
        weight = StringValue(weight),
        exerciseId = StringValue(exerciseId)
    )
}