package com.lateinit.rightweight.ui.mapper

import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel

fun ExercisePartType.toExercisePartTypeUiModel(): ExercisePartTypeUiModel {
    return when (this) {
        ExercisePartType.CHEST -> ExercisePartTypeUiModel.CHEST
        ExercisePartType.BACK -> ExercisePartTypeUiModel.BACK
        ExercisePartType.LEG -> ExercisePartTypeUiModel.LEG
        ExercisePartType.SHOULDER -> ExercisePartTypeUiModel.SHOULDER
        ExercisePartType.BICEPS -> ExercisePartTypeUiModel.BICEPS
        ExercisePartType.TRICEPS -> ExercisePartTypeUiModel.TRICEPS
        ExercisePartType.CORE -> ExercisePartTypeUiModel.CORE
        ExercisePartType.FOREARM -> ExercisePartTypeUiModel.FOREARM
        ExercisePartType.CARDIO -> ExercisePartTypeUiModel.CARDIO
    }
}