package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRepository {
    suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun shareRoutine(userId: String, routineId: String, routine: Routine)
    suspend fun shareDay(routineId: String, dayId: String, dayField: DayField)
    suspend fun shareExercise(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseField: ExerciseField
    )

    suspend fun shareExerciseSet(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseSetId: String,
        exerciseSetField: ExerciseSetField
    )
    suspend fun getChildrenDocumentName(path: String): List<String>
    suspend fun deleteDocument(path: String)
}