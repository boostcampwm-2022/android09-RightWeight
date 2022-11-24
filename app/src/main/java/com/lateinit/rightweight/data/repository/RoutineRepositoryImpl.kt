package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.datasource.RoutineDataSource
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineLocalDataSource: RoutineDataSource
) : RoutineRepository {

    override suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>,
    ) {
        routineLocalDataSource.insertRoutine(routine, days, exercises, sets)
    }

    override suspend fun insertRoutineList(routines: List<Routine>) {
        routineLocalDataSource.insertRoutineList(routines)
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

    override suspend fun getRoutines(): List<Routine> {
        return routineLocalDataSource.getRoutines()
    }

    override suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays{
        return routineLocalDataSource.getRoutineWithDaysByRoutineId(routineId)
    }
}