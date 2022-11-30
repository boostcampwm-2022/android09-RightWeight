package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.RootField

interface RoutineRemoteDataSource {
    suspend fun shareRoutine(rootField: RootField)
}