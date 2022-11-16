package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.Set
import com.lateinit.rightweight.data.datasource.RoutineDataSource
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineLocalDataSource: RoutineDataSource
) : RoutineRepository {

    override suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<Set>,
    ) {
        routineLocalDataSource.insertRoutine(routine, days, exercises, sets)
    }
}