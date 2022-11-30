package com.lateinit.rightweight.ui.model

import com.lateinit.rightweight.data.ExercisePartType

data class ExerciseUiModel(
    val exerciseId: String,
    val dayId: String,
    override var title: String,
    val order: Int,
    override val part: ExercisePartType,
    override val expanded: Boolean = true,
    override val exerciseSets: List<ExerciseSetUiModel> = emptyList()
) : ParentExerciseUiModel(title, part, expanded, exerciseSets)
