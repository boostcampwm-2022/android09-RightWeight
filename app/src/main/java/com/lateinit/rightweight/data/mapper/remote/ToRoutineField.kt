package com.lateinit.rightweight.data.mapper.remote

import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField
import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.remote.model.RoutineField
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.TimeStampValue
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel

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