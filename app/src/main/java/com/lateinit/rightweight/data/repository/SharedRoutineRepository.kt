package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRepository {
    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
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
    fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays>
    suspend fun requestSharedRoutineDetail(routineId: String)
    suspend fun commitTransaction(writes: List<WriteModelData>)
}