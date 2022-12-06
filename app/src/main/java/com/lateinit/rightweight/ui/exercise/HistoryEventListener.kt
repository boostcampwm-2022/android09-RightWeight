package com.lateinit.rightweight.ui.exercise

import com.lateinit.rightweight.ui.model.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseUiModel

interface HistoryEventListener {

    fun addHistorySet(historyExerciseId: String)

    fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel)

    fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel)

    fun removeHistorySet(historySetId: String)

    fun removeHistoryExercise(historyExerciseId:String)
}