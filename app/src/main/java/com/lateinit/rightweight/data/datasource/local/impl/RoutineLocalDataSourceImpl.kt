package com.lateinit.rightweight.data.datasource.local.impl

import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.datasource.local.RoutineLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineLocalDataSourceImpl @Inject constructor(
    private val routineDao: RoutineDao
) : RoutineLocalDataSource {

    override suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    ) {
        routineDao.insertRoutine(routine, days, exercises, sets)
    }

    override suspend fun updateRoutines(routines: List<Routine>) {
        routineDao.updateRoutines(routines)
    }

    override suspend fun getHigherRoutineOrder(): Int? {
        return routineDao.getHigherRoutineOrder()
    }

    override suspend fun getRoutineById(routineId: String): Routine {
        return routineDao.getRoutineById(routineId)
    }

    override suspend fun getDayById(dayId: String): Day {
        return routineDao.getDayById(dayId)
    }

    override suspend fun getExercisesByDayId(dayId: String): List<Exercise> {
        return routineDao.getExercisesByDayId(dayId)
    }

    override suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet> {
        return routineDao.getSetsByExerciseId(exerciseId)
    }

    override fun getAllRoutines(): Flow<List<Routine>> {
        return routineDao.getAllRoutines()
    }

    override suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays {
        return routineDao.getRoutineWithDaysByRoutineId(routineId)
    }

    override fun getRoutineWithDaysFlowByRoutineId(routineId: String): Flow<RoutineWithDays> {
        return routineDao.getRoutineWithDaysFlowByRoutineId(routineId)
    }

    override fun getDayWithExercisesByDayId(dayId: String): Flow<DayWithExercises> {
        return routineDao.getDayWithExercisesByDayId(dayId)
    }

    override suspend fun removeRoutineById(routineId: String) {
        return routineDao.removeRoutineById(routineId)
    }
}