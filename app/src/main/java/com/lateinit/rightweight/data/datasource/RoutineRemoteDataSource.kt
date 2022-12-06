package com.lateinit.rightweight.data.datasource

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow

import com.lateinit.rightweight.data.remote.model.RootField

interface RoutineRemoteDataSource {
    suspend fun shareRoutine(routineId: String, rootField: RootField)
    suspend fun shareDay(routineId: String, dayId: String, rootField: RootField)
    suspend fun shareExercise(
        routineId: String,
        dayId: String,
        exerciseId: String,
        rootField: RootField
    )

    suspend fun shareExerciseSet(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseSetId: String,
        rootField: RootField
    )
    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun getChildrenDocumentName(path: String):List<String>
    suspend fun deleteDocument(path: String)
    suspend fun getSharedRoutineDays(routineId: String): List<SharedRoutineDay>
    suspend fun getSharedRoutineExercises(routineId: String, dayId: String): List<SharedRoutineExercise>
    suspend fun getSharedRoutineExerciseSets(routineId: String, dayId: String, exerciseId: String): List<SharedRoutineExerciseSet>
    suspend fun commitTransaction(writes: List<WriteModelData>)
}