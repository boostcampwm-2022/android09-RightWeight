package com.lateinit.rightweight.ui.model

data class ExerciseSetUiModel(
    val setId: String,
    val exerciseId: String,
    override var weight: String = "",
    override var count: String = "",
    override val order: Int
) : ParentExerciseSetUiModel(weight, count, order)
