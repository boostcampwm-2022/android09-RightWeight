package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.model.RootField
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val routineApiService: RoutineApiService
) : RoutineRemoteDataSource {
    override suspend fun shareRoutine(routineId: String, rootField: RootField) {
        routineApiService.shareRoutine(routineId, rootField)
    }

    override suspend fun shareDay(routineId: String, dayId: String, rootField: RootField) {
        routineApiService.shareRoutineDay(routineId, dayId, rootField)
    }
}