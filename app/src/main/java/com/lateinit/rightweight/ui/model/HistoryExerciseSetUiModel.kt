package com.lateinit.rightweight.ui.model

data class HistoryExerciseSetUiModel(
    val setId: String,
    val exerciseId: String,
    override var weight: String,
    override var count: String,
    override val order: Int,
    val checked: Boolean
) : ParentExerciseSetUiModel(weight, count, order)
