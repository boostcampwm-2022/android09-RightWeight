package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.remote.model.UserInfoField

interface UserRemoteDataSource {
    suspend fun backupUserInfo(userId: String, routineId: String, dayId: String)

    suspend fun getChildrenDocumentName(path: String): List<String>

    suspend fun restoreUserInfo(userId: String): UserInfoField?
}