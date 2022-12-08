package com.lateinit.rightweight.ui.model

abstract class ParentExerciseUiModel(
    open var title: String,
    open val part: ExercisePartTypeUiModel,
    open val expanded: Boolean,
    open val exerciseSets: List<ParentExerciseSetUiModel>
)