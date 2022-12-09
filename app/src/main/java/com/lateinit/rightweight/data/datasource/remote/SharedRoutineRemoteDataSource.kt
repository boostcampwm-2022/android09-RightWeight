package com.lateinit.rightweight.data.datasource.remote

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.model.remote.SharedRoutineSortType
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRemoteDataSource {

    suspend fun getSharedRoutine(routineId: String): SharedRoutineField?

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

    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>

    suspend fun setSharedRoutineSortType(sortType: SharedRoutineSortType)

    suspend fun getChildrenDocumentName(path: String): List<String>

    suspend fun commitTransaction(writes: List<WriteModelData>)
}