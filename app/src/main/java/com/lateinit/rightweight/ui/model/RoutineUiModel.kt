package com.lateinit.rightweight.ui.model

import java.time.LocalDateTime

data class RoutineUiModel(
    override val routineId: String,
    override val title: String,
    override val author: String,
    override val description: String,
    override val modifiedDate: LocalDateTime,
    val order: Int
): ParentRoutineUiModel(routineId, title, author, description, modifiedDate)