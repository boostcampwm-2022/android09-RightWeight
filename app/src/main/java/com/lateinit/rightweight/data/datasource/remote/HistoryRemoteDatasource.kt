package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import java.time.LocalDate

interface HistoryRemoteDatasource {
    suspend fun getLatestHistoryDate(userId: String): LocalDate

    suspend fun commitTransaction(writes: List<WriteModelData>)

    suspend fun getHistories(path: String): List<History>

    suspend fun getHistoryExercises(path: String): List<HistoryExercise>

    suspend fun getHistoryExerciseSets(path: String): List<HistorySet>
}