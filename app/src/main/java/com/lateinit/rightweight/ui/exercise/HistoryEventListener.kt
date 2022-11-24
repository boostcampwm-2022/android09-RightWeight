package com.lateinit.rightweight.ui.exercise

import com.lateinit.rightweight.data.database.entity.HistorySet

interface HistoryEventListener {

    fun applyHistorySets(historyExerciseId: String, adapter:HistorySetAdapter)
    fun saveHistorySet(historySet: HistorySet)
    fun removeHistorySet(historySetId: String)
    fun renewTodayHistory()
    fun addHistorySet(historyExerciseId: String)
}