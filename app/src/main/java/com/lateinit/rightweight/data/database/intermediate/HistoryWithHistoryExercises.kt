package com.lateinit.rightweight.data.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise

data class HistoryWithHistoryExercises(
    @Embedded val history: History,

    @Relation(
        entity = HistoryExercise::class,
        parentColumn = "history_id",
        entityColumn = "history_id"
    )
    val historyExercises: List<HistoryExerciseWithHistorySets>
)
