package com.lateinit.rightweight.data.mapper.local

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.mapper.toExercisePartType
import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.data.model.remote.DetailResponse
import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField
import com.lateinit.rightweight.data.remote.model.RoutineField
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import com.lateinit.rightweight.ui.model.shared.SharedRoutineUiModel
import com.lateinit.rightweight.util.DEFAULT_SET_COUNT
import com.lateinit.rightweight.util.DEFAULT_SET_WEIGHT
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun SharedRoutineUiModel.toRoutine(routineId: String, author: String, order: Int): Routine {
    return Routine(
        routineId = routineId,
        title = title,
        author = author,
        description = description,
        modifiedDate = LocalDateTime.now(),
        order = order
    )
}

fun DayUiModel.toDayWithNewIds(routineId: String, dayId: String): Day {
    return Day(
        dayId = dayId,
        routineId = routineId,
        order = order
    )
}

fun ExerciseUiModel.toExerciseWithNewIds(dayId: String, exerciseId: String): Exercise {
    return Exercise(
        exerciseId = exerciseId,
        dayId = dayId,
        title = title,
        order = order,
        part = part.toExercisePartType()
    )
}

fun ExerciseSetUiModel.toExerciseSetWithNewIds(exerciseId: String, setId: String): ExerciseSet {
    return ExerciseSet(
        setId = setId,
        exerciseId = exerciseId,
        weight = weight.ifEmpty { DEFAULT_SET_WEIGHT },
        count = count.ifEmpty { DEFAULT_SET_COUNT },
        order = order
    )
}



fun RoutineUiModel.toRoutine(): Routine {
    return Routine(
        routineId = routineId,
        title = title,
        author = author,
        description = description,
        modifiedDate = modifiedDate,
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
        part = part.toExercisePartType()
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

fun RoutineField.toRoutine(routineId: String): Routine {
    val refinedModifiedDateString = modifiedDate.value
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val modifiedDate = LocalDateTime.parse(refinedModifiedDateString, formatter)
    return Routine(
        routineId = routineId,
        title = title.value,
        author = author.value,
        description = description.value,
        modifiedDate = modifiedDate,
        order = order.value.toInt()
    )
}

fun DayField.toDay(dayId: String): Day {
    return Day(
        dayId = dayId,
        routineId = routineId.value,
        order = order.value.toInt()
    )
}

fun ExerciseField.toExercise(exerciseId: String): Exercise {
    return Exercise(
        exerciseId = exerciseId,
        dayId = dayId.value,
        title = title.value,
        order = order.value.toInt(),
        part = ExercisePartType.valueOf(partType.value)
    )
}

fun ExerciseSetField.toExerciseSet(setId: String): ExerciseSet {
    return ExerciseSet(
        setId = setId,
        exerciseId = exerciseId.value,
        weight = weight.value,
        count = count.value,
        order = order.value.toInt()
    )
}

fun DetailResponse<SharedRoutineField>.toSharedRoutine(): SharedRoutine {
    val routineId = name.split("/").last()
    val refinedModifiedDateString = fields.modifiedDate.value
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val modifiedDate = LocalDateTime.parse(refinedModifiedDateString, formatter)
    return SharedRoutine(
        routineId = routineId,
        title = fields.title.value,
        author = fields.author.value,
        description = fields.description.value,
        modifiedDate = modifiedDate,
        sharedCount = fields.sharedCount.value.remoteData.count.value
    )
}