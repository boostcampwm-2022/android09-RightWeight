package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet

data class ExerciseWithSets(
    @Embedded val exercise: Exercise,

    @Relation(
        entity = ExerciseSet::class,
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val sets: List<ExerciseSet>
)

