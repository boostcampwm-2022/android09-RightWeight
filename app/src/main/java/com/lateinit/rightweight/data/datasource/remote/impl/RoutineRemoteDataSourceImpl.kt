package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.RoutineApiService
import com.lateinit.rightweight.data.datasource.remote.RoutineRemoteDataSource
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val api: RoutineApiService
) : RoutineRemoteDataSource {

}