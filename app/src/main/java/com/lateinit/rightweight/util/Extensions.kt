package com.lateinit.rightweight.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import kotlinx.coroutines.launch

fun ExercisePartType.getPartNameRes(): Int {
    return when (this) {
        ExercisePartType.CHEST -> R.string.chest
        ExercisePartType.BACK -> R.string.back
        ExercisePartType.LEG -> R.string.leg
        ExercisePartType.SHOULDER -> R.string.shoulder
        ExercisePartType.BICEPS -> R.string.biceps
        ExercisePartType.TRICEPS -> R.string.triceps
        ExercisePartType.CORE -> R.string.core
        ExercisePartType.FOREARM -> R.string.forearm
        ExercisePartType.CARDIO -> R.string.cardio
    }
}

fun LifecycleOwner.collectOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend () -> Unit
) {
    this.lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}