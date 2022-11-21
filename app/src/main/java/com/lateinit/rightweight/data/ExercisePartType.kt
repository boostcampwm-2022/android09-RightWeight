package com.lateinit.rightweight.data

import androidx.annotation.StringRes
import com.lateinit.rightweight.R

enum class ExercisePartType(@StringRes val partName: Int) {
    CHEST(R.string.chest),
    BACK(R.string.back),
    LEG(R.string.back),
    SHOULDER(R.string.shoulder),
    BICEPS(R.string.biceps),
    TRICEPS(R.string.triceps),
    CORE(R.string.core),
    FOREARM(R.string.forearm),
    CARDIO(R.string.cardio)
}