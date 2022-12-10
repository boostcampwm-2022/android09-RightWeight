package com.lateinit.rightweight.ui.model.routine

import com.lateinit.rightweight.ui.model.ParentExerciseUiModel

data class ExerciseUiModel(
    val exerciseId: String,
    val dayId: String,
    override var title: String,
    val order: Int,
    override val part: ExercisePartTypeUiModel = ExercisePartTypeUiModel.CHEST,
    override val expanded: Boolean = true,
    override val exerciseSets: List<ExerciseSetUiModel> = emptyList()
) : ParentExerciseUiModel(title, part, expanded, exerciseSets)
