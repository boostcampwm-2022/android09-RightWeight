package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.UserApiService
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.model.remote.Direction
import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.model.remote.FiledReferenceData
import com.lateinit.rightweight.data.model.remote.FilterData
import com.lateinit.rightweight.data.model.remote.FilterOperator
import com.lateinit.rightweight.data.model.remote.FromData
import com.lateinit.rightweight.data.model.remote.OrderByData
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.StructuredQueryData
import com.lateinit.rightweight.data.model.remote.WhereData
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.model.remote.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.HistoryField
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.data.remote.model.RoutineField
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

    override suspend fun getUserRoutineInRemote(userId: String): List<DocumentResponse<RoutineField>> {
        return api.getUserRoutine(
            RunQueryBody(
                StructuredQueryData(
                    from = FromData("routine"),
                    where = WhereData(
                        FilterData(
                            FiledReferenceData("userId"),
                            FilterOperator.EQUAL.toString(),
                            StringValue(userId)
                        )
                    )
                )
            )
        )
    }

    override suspend fun getLastHistoryInServer(userId: String): List<DocumentResponse<HistoryField>> {
        return api.getLastHistory(
            userId,
            RunQueryBody(
                StructuredQueryData(
                    from = FromData("history"),
                    orderBy = listOf(
                        OrderByData(
                            FiledReferenceData("date"),
                            Direction.DESCENDING.toString()
                        )
                    ),
                    limit = 1
                )
            )
        )
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