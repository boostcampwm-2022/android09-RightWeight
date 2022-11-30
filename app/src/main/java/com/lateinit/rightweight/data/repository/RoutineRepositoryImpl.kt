package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import com.lateinit.rightweight.data.model.RootField
import com.lateinit.rightweight.util.toSharedRoutineField
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineLocalDataSource: RoutineLocalDataSource,
    private val routineRemoteDataSource: RoutineRemoteDataSource
) : RoutineRepository {

    override suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>,
    ) {
        routineLocalDataSource.insertRoutine(routine, days, exercises, sets)
    }

    override suspend fun updateRoutines(routines: List<Routine>) {
        routineLocalDataSource.updateRoutines(routines)
    }

    override suspend fun getHigherRoutineOrder(): Int? {
        return routineLocalDataSource.getHigherRoutineOrder()
    }


    override suspend fun getRoutineById(routineId: String): Routine {
        return routineLocalDataSource.getRoutineById(routineId)
    }

    override suspend fun getDaysByRoutineId(routineId: String): List<Day> {
        return routineLocalDataSource.getDaysByRoutineId(routineId)

    }

    override suspend fun getDayById(dayId: String): Day {
        return routineLocalDataSource.getDayById(dayId)
    }

    override suspend fun getExercisesByDayId(dayId: String): List<Exercise> {
        return routineLocalDataSource.getExercisesByDayId(dayId)
    }

    override suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet> {
        return routineLocalDataSource.getSetsByExerciseId(exerciseId)
    }

    override suspend fun getRoutines(): List<Routine> {
        return routineLocalDataSource.getRoutines()
    }

    override suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays{
        return routineLocalDataSource.getRoutineWithDaysByRoutineId(routineId)
    }

    override suspend fun getDayWithExercisesByDayId(dayId: String): DayWithExercises {
        return routineLocalDataSource.getDayWithExercisesByDayId(dayId)
    }

    override suspend fun removeRoutineById(routineId: String) {
        routineLocalDataSource.removeRoutineById(routineId)
    }

    override suspend fun shareRoutine(userId: String, routine: Routine) {
        routineRemoteDataSource.shareRoutine(
            RootField(
                routine.toSharedRoutineField(userId)
            )
        )
    }
}