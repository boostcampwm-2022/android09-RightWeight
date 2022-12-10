package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.remote.model.HistoryField

interface UserRemoteDataSource {
    suspend fun backupUserInfo(userId: String, routineId: String, dayId: String)

    suspend fun getLastHistoryInServer(userId: String): List<DocumentResponse<HistoryField>>

    suspend fun getChildrenDocumentName(path: String): List<String>

    suspend fun commitTransaction(writes: List<WriteModelData>)
}