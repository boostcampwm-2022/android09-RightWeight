package com.lateinit.rightweight.ui.exercise

import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel

interface HistoryEventListener {

    fun addHistorySet(historyExerciseId: String)

    fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel)

    fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel)

    fun removeHistorySet(historySetId: String)

    fun removeHistoryExercise(historyExerciseId:String)
}