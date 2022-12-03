package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import kotlinx.coroutines.flow.Flow

interface SharedRoutineRepository {
    suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
    suspend fun getChildrenDocumentName(path: String): List<String>
}