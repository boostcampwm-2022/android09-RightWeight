package com.lateinit.rightweight.ui.exercise

import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet

interface HistoryEventListener {

    fun applyHistorySets(historyExerciseId: String, adapter:HistorySetAdapter)
    fun updateHistorySet(historySet: HistorySet)
    fun updateHistoryExercise(historyExercise: HistoryExercise)
    fun removeHistorySet(historySetId: String)
    fun removeHistoryExercise(historyExerciseId:String)
    fun renewTodayHistory()
    fun addHistorySet(historyExerciseId: String)
}