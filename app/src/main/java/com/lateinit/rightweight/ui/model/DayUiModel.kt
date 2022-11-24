package com.lateinit.rightweight.ui.model

data class DayUiModel(
    val dayId: String,
    val routineId: String,
    val order: Int,
    val selected: Boolean,
    val exercises: List<ExerciseUiModel> = emptyList()
)