package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.remote.model.RoutineField

interface UserRemoteDataSource {
    suspend fun backupUserInfo(userId: String, routineId: String, dayId: String)

    suspend fun getUserRoutineInRemote(userId: String): List<DocumentResponse<RoutineField>>

    suspend fun getChildrenDocumentName(path: String): List<String>

    suspend fun commitTransaction(writes: List<WriteModelData>)
}