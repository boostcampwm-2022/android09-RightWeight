package com.lateinit.rightweight.ui.model

import java.time.LocalDateTime

abstract class ParentRoutineUiModel(
    open val routineId: String,
    open val title: String,
    open val author: String,
    open val description: String,
    open val modifiedDate: LocalDateTime,
)