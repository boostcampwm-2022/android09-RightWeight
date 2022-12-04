package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.*

data class SharedRoutineDayWithExercises(
    @Embedded val day: SharedRoutineDay,

    @Relation(
        entity = SharedRoutineExercise::class,
        parentColumn = "day_id",
        entityColumn = "day_id"
    )
    val exercises: List<SharedRoutineExerciseWithExerciseSets>
)