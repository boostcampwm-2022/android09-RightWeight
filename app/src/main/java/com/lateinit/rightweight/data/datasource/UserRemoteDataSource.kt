package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.WriteModelData

interface UserRemoteDataSource {
    suspend fun backupUserInfo(userId: String, routineId: String?, dayId: String?)
    suspend fun commitTransaction(writes: List<WriteModelData>)
}