package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource
): SharedRoutineRepository {
    override suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getSharedRoutineDays(routineId: String): Flow<List<SharedRoutineDay>> {
        return routineRemoteDataSource.getSharedRoutineDays(routineId)
    }

    override suspend fun getSharedRoutineExercises(
        routineId: String,
        dayId: String
    ): Flow<List<SharedRoutineExercise>> {
        return routineRemoteDataSource.getSharedRoutineExercises(routineId, dayId)
    }

    override suspend fun getSharedRoutineExerciseSets(
        routineId: String,
        dayId: String,
        exerciseId: String
    ): Flow<List<SharedRoutineExerciseSet>> {
        return routineRemoteDataSource.getSharedRoutineExerciseSets(routineId, dayId, exerciseId)
    }
}