package com.lateinit.rightweight.data.mapper.remote

import com.lateinit.rightweight.data.remote.model.HistoryExerciseField
import com.lateinit.rightweight.data.remote.model.HistoryExerciseSetField
import com.lateinit.rightweight.data.remote.model.HistoryField
import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.TimeStampValue
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel

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