package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.Set
import javax.inject.Inject

class RoutineLocalDataSource @Inject constructor(private val routineDao: RoutineDao) :
    RoutineDataSource {

    override suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<Set>
    ) {
        routineDao.insertRoutine(routine, days, exercises, sets)
    }
}