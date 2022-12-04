package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource
) : SharedRoutineRepository {
    override suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays> {
        if(routineLocalDataSource.getDaysByRoutineId(routineId).isEmpty()){
            return routineLocalDataSource.getSharedRoutineWithDaysByRoutineId(routineId)
        }
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

}