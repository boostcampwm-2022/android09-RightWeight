package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.RootField

interface RoutineRemoteDataSource {
    suspend fun shareRoutine(routineId: String, rootField: RootField)
    suspend fun shareDay(routineId: String, dayId: String, rootField: RootField)
}