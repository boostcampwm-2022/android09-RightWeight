package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet

data class HistoryExerciseWithHistorySets(
    @Embedded val historyExercise: HistoryExercise,

    @Relation(
        entity = HistorySet::class,
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val historySets: List<HistorySet>
)
