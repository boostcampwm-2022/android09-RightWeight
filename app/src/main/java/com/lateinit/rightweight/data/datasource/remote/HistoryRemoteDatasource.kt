package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.model.remote.WriteModelData
import java.time.LocalDate

interface HistoryRemoteDatasource {
    suspend fun getLatestHistoryDate(userId: String): LocalDate

    suspend fun commitTransaction(writes: List<WriteModelData>)
}