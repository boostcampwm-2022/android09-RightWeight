package com.lateinit.rightweight.ui.model

import com.lateinit.rightweight.data.ExercisePartType

data class HistoryExerciseUiModel(
    val exerciseId: String,
    val historyId: String,
    override var title: String,
    val order: Int,
    override val part: ExercisePartType,
    override val expanded: Boolean = true,
    override val exerciseSets: List<HistoryExerciseSetUiModel>
) : ParentExerciseUiModel(title, part, expanded, exerciseSets)
