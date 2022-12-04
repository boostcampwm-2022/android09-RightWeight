package com.lateinit.rightweight.data.repository

import android.util.Log
import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource
) : SharedRoutineRepository {
    override suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays> {
        return routineLocalDataSource.getSharedRoutineWithDaysByRoutineId(routineId)
    }

    override suspend fun requestSharedRoutineDetail(routineId: String) {
        val sharedRoutineDays = mutableListOf<SharedRoutineDay>()
        val sharedRoutineExercises = mutableListOf<SharedRoutineExercise>()
        val sharedRoutineExerciseSets = mutableListOf<SharedRoutineExerciseSet>()
        routineRemoteDataSource.getSharedRoutineDays(routineId).forEach() { sharedRoutineDay ->
            sharedRoutineDays.add(sharedRoutineDay)
            routineRemoteDataSource.getSharedRoutineExercises(routineId, sharedRoutineDay.dayId)
                .forEach() { sharedRoutineExercise ->
                    sharedRoutineExercises.add(sharedRoutineExercise)
                    routineRemoteDataSource.getSharedRoutineExerciseSets(
                        routineId,
                        sharedRoutineExercise.dayId,
                        sharedRoutineExercise.exerciseId
                    ).forEach(){ sharedRoutineExerciseSet ->
                        sharedRoutineExerciseSets.add(sharedRoutineExerciseSet)
                    }
                }
        }
        routineLocalDataSource.insertSharedRoutineDetail(sharedRoutineDays, sharedRoutineExercises, sharedRoutineExerciseSets)
    }

}