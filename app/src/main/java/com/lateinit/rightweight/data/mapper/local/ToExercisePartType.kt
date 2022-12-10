package com.lateinit.rightweight.data.mapper

import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel

fun ExercisePartTypeUiModel.toExercisePartType(): ExercisePartType {
    return when (this) {
        ExercisePartTypeUiModel.CHEST -> ExercisePartType.CHEST
        ExercisePartTypeUiModel.BACK -> ExercisePartType.BACK
        ExercisePartTypeUiModel.LEG -> ExercisePartType.LEG
        ExercisePartTypeUiModel.SHOULDER -> ExercisePartType.SHOULDER
        ExercisePartTypeUiModel.BICEPS -> ExercisePartType.BICEPS
        ExercisePartTypeUiModel.TRICEPS -> ExercisePartType.TRICEPS
        ExercisePartTypeUiModel.CORE -> ExercisePartType.CORE
        ExercisePartTypeUiModel.FOREARM -> ExercisePartType.FOREARM
        ExercisePartTypeUiModel.CARDIO -> ExercisePartType.CARDIO
    }
}