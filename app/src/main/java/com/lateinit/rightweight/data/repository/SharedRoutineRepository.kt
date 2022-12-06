package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRepository {
    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun getChildrenDocumentName(path: String): List<String>
    fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays>
    suspend fun requestSharedRoutineDetail(routineId: String)
    suspend fun commitTransaction(writes: List<WriteModelData>)
    suspend fun checkRoutineInRemote(routineId: String): Boolean
}