package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.Day

data class DayWithExercises(
    val day: Day,

    @Relation(
        entity = Day::class,
        parentColumn = "day_id",
        entityColumn = "day_id"
    )
    val exercises: List<ExerciseWithSets>
)