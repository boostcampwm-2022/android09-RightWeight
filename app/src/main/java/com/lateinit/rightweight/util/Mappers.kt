package com.lateinit.rightweight.util

import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.*
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.ExerciseWithSets
import com.lateinit.rightweight.data.database.intermediate.HistoryExerciseWithHistorySets
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineExerciseWithExerciseSets
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
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val modifiedDate = LocalDateTime.parse(refinedModifiedDateString, formatter)
    return SharedRoutine(
        routineId = splitedName.last(),
        title = fields.title?.value.toString(),
        author = fields.author?.value.toString(),
        description = fields.description?.value.toString(),
        modifiedDate = modifiedDate
    )
}

fun Routine.toSharedRoutineField(userId: String): SharedRoutineField {
    return SharedRoutineField(
        author = StringValue(author),
        description = StringValue(description),
        modifiedDate = TimeStampValue(modifiedDate.toString() + "Z"),
        order = IntValue(order.toString()),
        title = StringValue(title),
        userId = StringValue(userId),
        sharedCount = MapValue(
            RootField(
                SharedCount(
                time = TimeStampValue(LocalDateTime.now().toString() + "Z"),
                count = IntValue("0")
            ))
        )
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

fun DetailResponse<DayField>.toSharedRoutineDay(): SharedRoutineDay {
    val splitedName = name.split("/")
    return SharedRoutineDay(
        routineId = fields.routineId?.value.toString(),
        dayId = splitedName.last(),
        order = fields.order?.value.toString().toInt()
    )
}

fun DetailResponse<ExerciseField>.toSharedRoutineExercise(): SharedRoutineExercise {
    val splitedName = name.split("/")
    return SharedRoutineExercise(
        dayId = fields.dayId?.value.toString(),
        exerciseId = splitedName.last(),
        title = fields.title?.value.toString(),
        order = fields.order?.value.toString().toInt(),
        part = ExercisePartType.CHEST
        //part =  ExercisePartType.valueOf(fields.partType?.value.toString())
    )
}

fun DetailResponse<ExerciseSetField>.toSharedRoutineExerciseSet(): SharedRoutineExerciseSet {
    val splitedName = name.split("/")
    return SharedRoutineExerciseSet(
        exerciseId = fields.exerciseId?.value.toString(),
        setId = splitedName.last(),
        weight = fields.weight?.value.toString(),
        count = fields.count?.value.toString(),
        order = fields.order?.value.toString().toInt()
    )
}

fun SharedRoutineDay.toDayUiModel(
    index: Int,
    exercises: List<SharedRoutineExerciseWithExerciseSets>
): DayUiModel {
    return DayUiModel(
        dayId = dayId,
        routineId = routineId,
        order = order,
        selected = index == FIRST_DAY_POSITION,
        exercises = exercises.map { it.toExerciseUiModel() }
    )
}

fun SharedRoutineExerciseWithExerciseSets.toExerciseUiModel(): ExerciseUiModel {
    return ExerciseUiModel(
        exerciseId = exercise.exerciseId,
        dayId = exercise.dayId,
        title = exercise.title,
        order = exercise.order,
        part = exercise.part,
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
        part = part
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