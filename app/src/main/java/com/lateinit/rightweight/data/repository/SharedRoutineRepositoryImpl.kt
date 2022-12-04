package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource
): SharedRoutineRepository {
    override suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return routineRemoteDataSource.getChildrenDocumentName(path)
    }

    override suspend fun deleteDocument(path: String) {
        routineRemoteDataSource.deleteDocument(path)
    }
}