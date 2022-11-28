package com.lateinit.rightweight.ui.model

data class ExerciseSetUiModel(
    val setId: String,
    val exerciseId: String,
    var weight: String = "",
    var count: String = "",
    val order: Int
)
