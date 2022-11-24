package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.entity.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class RoutineLocalDataSource @Inject constructor(
    private val routineDao: RoutineDao,
    ) :
    RoutineDataSource {

    override suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    ) {
        routineDao.insertRoutine(routine, days, exercises, sets)
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
}