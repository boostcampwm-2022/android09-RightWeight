package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay

data class SharedRoutineWithDays(
    @Embedded val routine: SharedRoutine,

    @Relation(
        entity = SharedRoutineDay::class,
        parentColumn = "routine_id",
        entityColumn = "routine_id"
    )
    val days: List<SharedRoutineDayWithExercises>
)
