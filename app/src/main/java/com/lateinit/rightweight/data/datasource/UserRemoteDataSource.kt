package com.lateinit.rightweight.data.datasource

interface UserRemoteDataSource {
    suspend fun backupUserInfo(userId: String, routineId: String?, dayId: String?)
}