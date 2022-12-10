package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.UserApiService
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.model.remote.DetailResponse
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.model.remote.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.UserInfoField
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val api: UserApiService
) : UserRemoteDataSource {
    override suspend fun backupUserInfo(userId: String, routineId: String, dayId: String) {
        api.backupUserInfo(
            userId, RootField(
                UserInfoField(
                    routineId = StringValue(routineId),
                    dayId = StringValue(dayId)
                )
            )
        )
    }


    override suspend fun restoreUserInfo(userId: String): DetailResponse<UserInfoField> {
        return api.restoreUserInfo(userId)
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocumentName(path)
        return documentNameList.documents?.map {
            it.name.split("/").last()
        } ?: emptyList()
    }


    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        api.commitTransaction(WriteRequestBody(writes))
    }
}