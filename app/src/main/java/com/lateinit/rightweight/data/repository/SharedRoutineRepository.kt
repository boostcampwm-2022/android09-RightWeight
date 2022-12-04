package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRepository {
    suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays>
    suspend fun requestSharedRoutineDetail(routineId: String)
}