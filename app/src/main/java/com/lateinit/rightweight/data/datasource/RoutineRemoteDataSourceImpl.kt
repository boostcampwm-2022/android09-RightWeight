package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.model.RootField
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val routineApiService: RoutineApiService
) : RoutineRemoteDataSource {
    override suspend fun shareRoutine(rootField: RootField) {
        routineApiService.shareRoutine(rootField)
    }
}