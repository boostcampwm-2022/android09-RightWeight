package com.lateinit.rightweight.data.datasource

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import kotlinx.coroutines.flow.Flow

interface RoutineRemoteDataSource {
    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun getSharedRoutineDays(routineId: String): Flow<List<SharedRoutineDay>>
    suspend fun getSharedRoutineExercises(routineId: String, dayId: String): Flow<List<SharedRoutineExercise>>
    suspend fun getSharedRoutineExerciseSets(routineId: String, dayId: String, exerciseId: String): Flow<List<SharedRoutineExerciseSet>>
}