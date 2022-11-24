package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet

data class ExerciseWithSets(
    val exercise: Exercise,

    @Relation(
        entity = Exercise::class,
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val sets: List<ExerciseSet>
)

