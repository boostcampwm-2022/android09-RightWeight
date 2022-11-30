package com.lateinit.rightweight.ui.model

import com.lateinit.rightweight.data.ExercisePartType

abstract class ParentExerciseUiModel(
    open val title: String,
    open val part: ExercisePartType,
    open val expanded: Boolean,
    open val exerciseSets: List<ParentExerciseSetUiModel>
)