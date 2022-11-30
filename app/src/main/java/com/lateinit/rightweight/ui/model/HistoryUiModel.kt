package com.lateinit.rightweight.ui.model

import java.time.LocalDate

data class HistoryUiModel(
    val historyId: String,
    val date: LocalDate,
    val time: String,
    val routineTitle: String,
    override val order: Int,
    val completed: Boolean,
    override val exercises: List<HistoryExerciseUiModel>
) : ParentDayUiModel(order, exercises)