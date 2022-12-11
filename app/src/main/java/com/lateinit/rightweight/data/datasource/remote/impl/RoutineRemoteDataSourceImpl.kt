package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.RoutineApiService
import com.lateinit.rightweight.data.datasource.remote.RoutineRemoteDataSource
import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.model.remote.FiledReferenceData
import com.lateinit.rightweight.data.model.remote.FilterData
import com.lateinit.rightweight.data.model.remote.FilterOperator
import com.lateinit.rightweight.data.model.remote.FromData
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.StructuredQueryData
import com.lateinit.rightweight.data.model.remote.WhereData
import com.lateinit.rightweight.data.remote.model.RoutineField
import com.lateinit.rightweight.data.remote.model.StringValue
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val api: RoutineApiService
) : RoutineRemoteDataSource {
    override suspend fun getRoutineByUserId(userId: String): List<DocumentResponse<RoutineField>> {
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

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocumentName(path)
        return documentNameList.documents?.map {
            it.name.split("/").last()
        } ?: emptyList()
    }
}