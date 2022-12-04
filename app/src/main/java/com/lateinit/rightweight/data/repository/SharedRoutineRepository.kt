package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRepository {
    suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
//    suspend fun getSharedRoutineDays(routineId: String): Flow<List<SharedRoutineDay>>
//    suspend fun getSharedRoutineExercises(routineId: String, dayId: String): Flow<List<SharedRoutineExercise>>
//    suspend fun getSharedRoutineExerciseSets(routineId: String, dayId: String, exerciseId: String): Flow<List<SharedRoutineExerciseSet>>

    suspend fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays>
}