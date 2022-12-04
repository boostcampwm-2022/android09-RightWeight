package com.lateinit.rightweight.data.repository

import android.util.Log
import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineDayWithExercises
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource
) : SharedRoutineRepository {
    override suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays> {
        routineRemoteDataSource.getSharedRoutineDays(routineId).collect() { sharedRoutineDays ->
            sharedRoutineDays.forEach() { sharedRoutineDay ->
                routineRemoteDataSource.getSharedRoutineExercises(routineId, sharedRoutineDay.dayId)
                    .collect() { sharedRoutineExercises ->
                        sharedRoutineExercises.forEach() { sharedRoutineExercise ->
                            routineRemoteDataSource.getSharedRoutineExerciseSets(
                                routineId,
                                sharedRoutineExercise.dayId,
                                sharedRoutineExercise.exerciseId
                            ).collect() { sharedRoutineExerciseSets ->
                                routineLocalDataSource.insertSharedRoutineDetail(sharedRoutineDays, sharedRoutineExercises, sharedRoutineExerciseSets)
                            }
                        }
                    }
            }
        }
        return routineLocalDataSource.getSharedRoutineWithDaysByRoutineId(routineId)
    }

//    override suspend fun getSharedRoutineDays(routineId: String): Flow<List<SharedRoutineDay>> {
//        return routineRemoteDataSource.getSharedRoutineDays(routineId)
//    }

//    override suspend fun getSharedRoutineExercises(
//        routineId: String,
//        dayId: String
//    ): Flow<List<SharedRoutineExercise>> {
//        return routineRemoteDataSource.getSharedRoutineExercises(routineId, dayId)
//    }
//
//    override suspend fun getSharedRoutineExerciseSets(
//        routineId: String,
//        dayId: String,
//        exerciseId: String
//    ): Flow<List<SharedRoutineExerciseSet>> {
//        return routineRemoteDataSource.getSharedRoutineExerciseSets(routineId, dayId, exerciseId)
//    }
}