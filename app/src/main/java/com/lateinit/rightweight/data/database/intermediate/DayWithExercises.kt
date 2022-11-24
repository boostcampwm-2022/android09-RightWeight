package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise

data class DayWithExercises(
    @Embedded val day: Day,

    @Relation(
        entity = Exercise::class,
        parentColumn = "day_id",
        entityColumn = "day_id"
    )
    val exercises: List<ExerciseWithSets>
)