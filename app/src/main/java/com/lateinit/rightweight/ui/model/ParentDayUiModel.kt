package com.lateinit.rightweight.ui.model

abstract class ParentDayUiModel(
    open val order: Int,
    open val exercises: List<ParentExerciseUiModel>
)
