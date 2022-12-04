package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.database.entity.*
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineLocalDataSourceImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val sharedRoutineDao: SharedRoutineDao
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

    override suspend fun getDaysByRoutineId(routineId: String): List<Day> {
        return routineDao.getDaysByRoutineId(routineId)
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

    override suspend fun getRoutines(): List<Routine> {
        return routineDao.getRoutines()
    }

    override suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays {
        return routineDao.getRoutineWithDaysByRoutineId(routineId)
    }

    override suspend fun getDayWithExercisesByDayId(dayId: String): DayWithExercises {
        return routineDao.getDayWithExercisesByDayId(dayId)
    }

    override suspend fun removeRoutineById(routineId: String) {
        return routineDao.removeRoutineById(routineId)
    }

    override suspend fun insertSharedRoutineDetail(
        days: List<SharedRoutineDay>,
        exercises: List<SharedRoutineExercise>,
        sets: List<SharedRoutineExerciseSet>
    ) {
       sharedRoutineDao.insertSharedRoutineDetail(days, exercises, sets)
    }

    override suspend fun getSharedRoutineWithDaysByRoutineId(routineId: String): Flow<SharedRoutineWithDays> {
        return sharedRoutineDao.getSharedRoutineWithDaysByRoutineId(routineId)
    }
}