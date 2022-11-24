package com.lateinit.rightweight.ui.model

import com.lateinit.rightweight.data.ExercisePartType

data class ExerciseUiModel(
    val exerciseId: String,
    val dayId: String,
    var title: String,
    val order: Int,
    val part: ExercisePartType,
    val expanded: Boolean = true,
    val exerciseSets: List<ExerciseSetUiModel> = emptyList()
)
