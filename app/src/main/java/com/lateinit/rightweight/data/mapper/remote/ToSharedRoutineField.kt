package com.lateinit.rightweight.data.mapper.remote

import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.remote.model.MapValue
import com.lateinit.rightweight.data.remote.model.MapValueRootField
import com.lateinit.rightweight.data.remote.model.SharedCount
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.TimeStampValue
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import java.time.LocalDateTime

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