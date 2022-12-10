package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.UserApiService
import com.lateinit.rightweight.data.datasource.remote.HistoryRemoteDatasource
import javax.inject.Inject

class HistoryRemoteDataSourceImpl @Inject constructor(
    private val api: UserApiService
): HistoryRemoteDatasource {
}