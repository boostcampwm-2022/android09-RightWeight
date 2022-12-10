package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.UserApiService
import com.lateinit.rightweight.data.datasource.remote.HistoryRemoteDatasource
import com.lateinit.rightweight.data.model.remote.Direction
import com.lateinit.rightweight.data.model.remote.FiledReferenceData
import com.lateinit.rightweight.data.model.remote.FromData
import com.lateinit.rightweight.data.model.remote.OrderByData
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.StructuredQueryData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HistoryRemoteDataSourceImpl @Inject constructor(
    private val api: UserApiService
) : HistoryRemoteDatasource {
    override suspend fun getLatestHistoryDate(userId: String): LocalDate {
        val documentResponseList = api.getLastHistoryDate(
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val lastDateTime = documentResponseList.first()
            .document?.fields?.date?.value ?: return DEFAULT_LOCAL_DATE
        return LocalDate.parse(lastDateTime, formatter)
    }

    companion object {
        val DEFAULT_LOCAL_DATE: LocalDate = LocalDate.parse("1990-01-01")
    }
}