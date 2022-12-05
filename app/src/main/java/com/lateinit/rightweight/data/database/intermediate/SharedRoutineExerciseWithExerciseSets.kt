package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet

data class SharedRoutineExerciseWithExerciseSets(
    @Embedded val exercise: SharedRoutineExercise,

    @Relation(
        entity = SharedRoutineExerciseSet::class,
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val sets: List<SharedRoutineExerciseSet>
)