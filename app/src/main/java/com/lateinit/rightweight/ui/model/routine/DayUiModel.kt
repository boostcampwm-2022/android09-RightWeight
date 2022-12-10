package com.lateinit.rightweight.ui.model.routine

import com.lateinit.rightweight.ui.model.ParentDayUiModel

data class DayUiModel(
    val dayId: String,
    val routineId: String,
    override val order: Int,
    val selected: Boolean,
    override val exercises: List<ExerciseUiModel> = emptyList()
) : ParentDayUiModel(order, exercises)