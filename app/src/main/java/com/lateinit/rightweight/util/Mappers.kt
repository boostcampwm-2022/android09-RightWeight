package com.lateinit.rightweight.util

import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.ExerciseWithSets
import com.lateinit.rightweight.data.database.intermediate.HistoryExerciseWithHistorySets
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineExerciseWithExerciseSets
import com.lateinit.rightweight.data.model.remote.DetailResponse
import com.lateinit.rightweight.data.model.remote.SharedRoutineSortType
import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField
import com.lateinit.rightweight.data.remote.model.HistoryExerciseField
import com.lateinit.rightweight.data.remote.model.HistoryExerciseSetField
import com.lateinit.rightweight.data.remote.model.HistoryField
import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.remote.model.MapValue
import com.lateinit.rightweight.data.remote.model.MapValueRootField
import com.lateinit.rightweight.data.remote.model.RoutineField
import com.lateinit.rightweight.data.remote.model.SharedCount
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.TimeStampValue
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExercisePartTypeUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.ui.model.RoutineUiModel
import com.lateinit.rightweight.ui.model.SharedRoutineSortTypeUiModel
import com.lateinit.rightweight.ui.model.SharedRoutineUiModel
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

fun HistoryWithHistoryExercises.toHistoryUiModel(): HistoryUiModel {
    return HistoryUiModel(
        historyId = history.historyId,
        date = history.date,
        time = history.time,
        routineTitle = history.routineTitle,
        order = history.dayOrder,
        completed = history.completed,
        routineId = history.routineId,
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

fun DetailResponse<SharedRoutineField>.toSharedRoutine(): SharedRoutine {
    val splitedName = name.split("/")
    val refinedModifiedDateString = fields.modifiedDate.value?.replace("T", " ")?.replace("Z", "")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val modifiedDate = LocalDateTime.parse(refinedModifiedDateString, formatter)
    return SharedRoutine(
        routineId = splitedName.last(),
        title = fields.title.value.toString(),
        author = fields.author.value.toString(),
        description = fields.description.value.toString(),
        modifiedDate = modifiedDate,
        sharedCount = fields.sharedCount.value?.remoteData?.count?.value.toString()
    )
}

fun RoutineUiModel.toSharedRoutineField(userId: String): SharedRoutineField {
    return SharedRoutineField(
        author = StringValue(author),
        description = StringValue(description),
        modifiedDate = TimeStampValue(modifiedDate.toString() + "Z"),
        order = IntValue(order.toString()),
        title = StringValue(title),
        userId = StringValue(userId),
        sharedCount = MapValue(
            MapValueRootField(
                SharedCount(
                    time = TimeStampValue(LocalDateTime.now().toString() + "Z"),
                    count = IntValue("0")
                )
            )
        )
    )
}

fun RoutineUiModel.toRoutineField(userId: String): RoutineField {
    return RoutineField(
        author = StringValue(author),
        description = StringValue(description),
        modifiedDate = TimeStampValue(modifiedDate.toString() + "Z"),
        order = IntValue(order.toString()),
        title = StringValue(title),
        userId = StringValue(userId),
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
        partType = StringValue(part.name),
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

fun HistoryUiModel.toHistoryField(): HistoryField {
    return HistoryField(
        date = TimeStampValue(date.toString() + "T00:00:00Z"),
        time = StringValue(time),
        routineTitle = StringValue(routineTitle),
        order = IntValue(order.toString()),
        routineId = StringValue(routineId)
    )
}

fun HistoryExerciseUiModel.toHistoryExerciseField(): HistoryExerciseField {
    return HistoryExerciseField(
        historyId = StringValue(historyId),
        title = StringValue(title),
        order = IntValue(order.toString()),
        part = StringValue(part.name),
    )
}

fun HistoryExerciseSetUiModel.toHistoryExerciseSetField(): HistoryExerciseSetField {
    return HistoryExerciseSetField(
        exerciseId = StringValue(exerciseId),
        weight = StringValue(weight),
        count = StringValue(count),
        order = IntValue(order.toString()),
    )
}

fun DetailResponse<DayField>.toSharedRoutineDay(): SharedRoutineDay {
    val splitedName = name.split("/")
    return SharedRoutineDay(
        routineId = fields.routineId.value.toString(),
        dayId = splitedName.last(),
        order = fields.order.value.toString().toInt()
    )
}

fun DetailResponse<ExerciseField>.toSharedRoutineExercise(): SharedRoutineExercise {
    val splitedName = name.split("/")
    return SharedRoutineExercise(
        dayId = fields.dayId.value.toString(),
        exerciseId = splitedName.last(),
        title = fields.title.value.toString(),
        order = fields.order.value.toString().toInt(),
        part = ExercisePartType.valueOf(fields.partType.value.toString())
    )
}

fun DetailResponse<ExerciseSetField>.toSharedRoutineExerciseSet(): SharedRoutineExerciseSet {
    val splitedName = name.split("/")
    return SharedRoutineExerciseSet(
        exerciseId = fields.exerciseId.value.toString(),
        setId = splitedName.last(),
        weight = fields.weight.value.toString(),
        count = fields.count.value.toString(),
        order = fields.order.value.toString().toInt()
    )
}

fun SharedRoutineDay.toDayUiModel(
    exercises: List<SharedRoutineExerciseWithExerciseSets>
): DayUiModel {
    return DayUiModel(
        dayId = dayId,
        routineId = routineId,
        order = order,
        selected = false,
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

fun HistoryUiModel.toHistory(): History {
    return History(
        historyId = historyId,
        date = date,
        time = time,
        routineTitle = routineTitle,
        dayOrder = order,
        completed = completed,
        routineId = routineId
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

fun ExercisePartType.toExercisePartTypeUiModel(): ExercisePartTypeUiModel {
    return when (this) {
        ExercisePartType.CHEST -> ExercisePartTypeUiModel.CHEST
        ExercisePartType.BACK -> ExercisePartTypeUiModel.BACK
        ExercisePartType.LEG -> ExercisePartTypeUiModel.LEG
        ExercisePartType.SHOULDER -> ExercisePartTypeUiModel.SHOULDER
        ExercisePartType.BICEPS -> ExercisePartTypeUiModel.BICEPS
        ExercisePartType.TRICEPS -> ExercisePartTypeUiModel.TRICEPS
        ExercisePartType.CORE -> ExercisePartTypeUiModel.CORE
        ExercisePartType.FOREARM -> ExercisePartTypeUiModel.FOREARM
        ExercisePartType.CARDIO -> ExercisePartTypeUiModel.CARDIO
    }
}

fun ExercisePartTypeUiModel.toExercisePartType(): ExercisePartType {
    return when (this) {
        ExercisePartTypeUiModel.CHEST -> ExercisePartType.CHEST
        ExercisePartTypeUiModel.BACK -> ExercisePartType.BACK
        ExercisePartTypeUiModel.LEG -> ExercisePartType.LEG
        ExercisePartTypeUiModel.SHOULDER -> ExercisePartType.SHOULDER
        ExercisePartTypeUiModel.BICEPS -> ExercisePartType.BICEPS
        ExercisePartTypeUiModel.TRICEPS -> ExercisePartType.TRICEPS
        ExercisePartTypeUiModel.CORE -> ExercisePartType.CORE
        ExercisePartTypeUiModel.FOREARM -> ExercisePartType.FOREARM
        ExercisePartTypeUiModel.CARDIO -> ExercisePartType.CARDIO
    }
}

fun SharedRoutineSortTypeUiModel.toSharedRoutineSortType(): SharedRoutineSortType {
    return when (this) {
        SharedRoutineSortTypeUiModel.MODIFIED_DATE_FIRST -> SharedRoutineSortType.MODIFIED_DATE_FIRST
        SharedRoutineSortTypeUiModel.SHARED_COUNT_FIRST -> SharedRoutineSortType.SHARED_COUNT_FIRST
    }
}