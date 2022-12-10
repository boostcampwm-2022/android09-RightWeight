package com.lateinit.rightweight.data.repository.impl

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.datasource.local.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.remote.RoutineRemoteDataSource
import com.lateinit.rightweight.data.mapper.toRoutine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineLocalDataSource: RoutineLocalDataSource,
    private val routineRemoteDataSource: RoutineRemoteDataSource
) : RoutineRepository {

    override suspend fun insertRoutine(
        routine: RoutineUiModel,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>,
    ) {
        routineLocalDataSource.insertRoutine(routine.toRoutine(), days, exercises, sets)
    }

    override suspend fun getHigherRoutineOrder(): Int? {
        return routineLocalDataSource.getHigherRoutineOrder()
    }

    override suspend fun getRoutineById(routineId: String): Routine {
        return routineLocalDataSource.getRoutineById(routineId)
    }

    override suspend fun getDayById(dayId: String): Day {
        return routineLocalDataSource.getDayById(dayId)
    }

    override suspend fun getDaysByRoutineId(routineId: String): List<Day> {
        return routineLocalDataSource.getDaysByRoutineId(routineId)
    }

    override suspend fun getExercisesByDayId(dayId: String): List<Exercise> {
        return routineLocalDataSource.getExercisesByDayId(dayId)
    }

    override suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet> {
        return routineLocalDataSource.getSetsByExerciseId(exerciseId)
    }

    override suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays {
        return routineLocalDataSource.getRoutineWithDaysByRoutineId(routineId)
    }

    override suspend fun getUserRoutineIds(userId: String): List<String> {
        val documentsResponseList = routineRemoteDataSource.getRoutineByUserId(userId)
        val documents = documentsResponseList.map { it.document }
        return documents
            .filterNotNull()
            .map {
                it.name.split("/").last()
            }
    }

    override suspend fun getAllRoutineWithDays(): List<RoutineWithDays> {
        return routineLocalDataSource.getAllRoutineWithDays()
    }

    override fun getAllRoutines(): Flow<List<Routine>> {
        return routineLocalDataSource.getAllRoutines()
    }

    override fun getDayWithExercisesByDayId(dayId: String): Flow<DayWithExercises> {
        return routineLocalDataSource.getDayWithExercisesByDayId(dayId)
    }

    override suspend fun updateRoutines(routines: List<RoutineUiModel>) {
        routineLocalDataSource.updateRoutines(routines.map { it.toRoutine() })
    }

    override suspend fun removeRoutineById(routineId: String) {
        routineLocalDataSource.removeRoutineById(routineId)
    }

    override suspend fun removeAllRoutines() {
        routineLocalDataSource.removeAllRoutines()
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return routineRemoteDataSource.getChildrenDocumentName(path)
    }
}