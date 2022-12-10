package com.lateinit.rightweight.data.datasource.remote

import java.time.LocalDate

interface HistoryRemoteDatasource {
    suspend fun getLatestHistoryDate(userId: String): LocalDate
}