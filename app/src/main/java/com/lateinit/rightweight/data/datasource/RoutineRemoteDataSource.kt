package com.lateinit.rightweight.data.datasource

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow

interface RoutineRemoteDataSource {
    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun getChildrenDocumentName(path: String): List<String>
    suspend fun getSharedRoutine(routineId: String): Boolean
    suspend fun getSharedRoutineDays(routineId: String): List<SharedRoutineDay>
    suspend fun getSharedRoutineExercises(
        routineId: String,
        dayId: String
    ): List<SharedRoutineExercise>

    suspend fun getSharedRoutineExerciseSets(
        routineId: String,
        dayId: String,
        exerciseId: String
    ): List<SharedRoutineExerciseSet>

    suspend fun commitTransaction(writes: List<WriteModelData>)
}