package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.Set

interface RoutineRepository {

    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<Set>
    )
}