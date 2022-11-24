package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Routine

data class RoutineWithDays(
    @Embedded val routine: Routine,

    @Relation(
        entity = Day::class,
        parentColumn = "routine_id",
        entityColumn = "routine_id"
    )
    val days: List<DayWithExercises>
)
