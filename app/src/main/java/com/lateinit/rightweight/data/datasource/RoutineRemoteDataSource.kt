package com.lateinit.rightweight.data.datasource

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
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
}