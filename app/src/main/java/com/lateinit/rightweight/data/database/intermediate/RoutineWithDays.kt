package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.Routine

data class RoutineWithDays(
    val routine: Routine,

    @Relation(
        entity = Routine::class,
        parentColumn = "routine_id",
        entityColumn = "routine_id"
    )
    val days: List<DayWithExercises>
)
