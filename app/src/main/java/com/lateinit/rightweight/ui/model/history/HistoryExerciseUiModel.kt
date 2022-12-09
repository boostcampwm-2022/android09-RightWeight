package com.lateinit.rightweight.ui.model.history

import com.lateinit.rightweight.ui.model.ParentExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel

data class HistoryExerciseUiModel(
    val exerciseId: String,
    val historyId: String,
    override var title: String,
    val order: Int,
    override val part: ExercisePartTypeUiModel,
    override val expanded: Boolean = true,
    override val exerciseSets: List<HistoryExerciseSetUiModel>
) : ParentExerciseUiModel(title, part, expanded, exerciseSets)
