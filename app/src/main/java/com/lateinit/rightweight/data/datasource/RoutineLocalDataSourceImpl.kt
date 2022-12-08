package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

    override suspend fun getDayById(dayId: String): Day {
        return routineDao.getDayById(dayId)
    }

    override suspend fun getExercisesByDayId(dayId: String): List<Exercise> {
        return routineDao.getExercisesByDayId(dayId)
    }

    override suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet> {
        return routineDao.getSetsByExerciseId(exerciseId)
    }

    override fun getRoutines(): Flow<List<Routine>> {
        return routineDao.getRoutines()
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

    override suspend fun insertSharedRoutineDetail(
        days: List<SharedRoutineDay>,
        exercises: List<SharedRoutineExercise>,
        sets: List<SharedRoutineExerciseSet>
    ) {
       sharedRoutineDao.insertSharedRoutineDetail(days, exercises, sets)
    }

    override fun getSharedRoutineWithDaysByRoutineId(routineId: String): Flow<SharedRoutineWithDays> {
        return sharedRoutineDao.getSharedRoutineWithDaysByRoutineId(routineId)
    }

    override fun getSelectedRoutine(routineId: String?): Flow<Routine> {
        return if (routineId == null) {
            emptyFlow()
        } else {
            routineDao.getSelectedRoutine(routineId)
        }
    }
}