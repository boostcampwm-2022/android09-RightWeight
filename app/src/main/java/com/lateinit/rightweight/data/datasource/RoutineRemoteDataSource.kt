package com.lateinit.rightweight.data.datasource

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import kotlinx.coroutines.flow.Flow

interface RoutineRemoteDataSource {
    fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>>
}