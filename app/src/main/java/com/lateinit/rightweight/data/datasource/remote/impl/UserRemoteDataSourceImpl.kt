package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.UserApiService
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.UserInfoField
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val api: UserApiService
) : UserRemoteDataSource {
    override suspend fun backupUserInfo(userId: String, routineId: String, dayId: String, completedDayId: String) {
        api.backupUserInfo(
            userId, RootField(
                UserInfoField(
                    routineId = StringValue(routineId),
                    dayId = StringValue(dayId),
                    completedDayId = StringValue(completedDayId)
                )
            )
        )
    }


    override suspend fun restoreUserInfo(userId: String): UserInfoField? {
        val response = api.restoreUserInfo(userId)
        return if (response.isSuccessful) {
            response.body()?.fields
        } else null
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocumentName(path)
        return documentNameList.documents?.map {
            it.name.split("/").last()
        } ?: emptyList()
    }

}