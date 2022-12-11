package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.UserApiService
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.datasource.remote.HistoryRemoteDatasource
import com.lateinit.rightweight.data.mapper.toHistory
import com.lateinit.rightweight.data.mapper.toHistoryExercise
import com.lateinit.rightweight.data.mapper.toHistorySet
import com.lateinit.rightweight.data.model.remote.Direction
import com.lateinit.rightweight.data.model.remote.FiledReferenceData
import com.lateinit.rightweight.data.model.remote.FromData
import com.lateinit.rightweight.data.model.remote.OrderByData
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.StructuredQueryData
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.model.remote.WriteRequestBody
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val lastDateTime = documentResponseList.first()
            .document?.fields?.date?.value ?: return DEFAULT_LOCAL_DATE
        return LocalDate.parse(lastDateTime, formatter)
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        api.commitTransaction(WriteRequestBody(writes))
    }

    override suspend fun getHistories(path: String): List<History>{
        val histories = api.getHistories(path)
        return histories.documents?.map {
            val historyId = it.name.split("/").last()
            it.fields.toHistory(historyId)
        } ?: emptyList()
    }

    override suspend fun getHistoryExercises(path: String): List<HistoryExercise> {
        val exercises = api.getHistoryExercises(path)
        return exercises.documents?.map {
            val exerciseId = it.name.split("/").last()
            it.fields.toHistoryExercise(exerciseId)
        } ?: emptyList()
    }

    override suspend fun getHistoryExerciseSets(path: String): List<HistorySet> {
        val exerciseSets = api.getHistoryExerciseSets(path)
        return exerciseSets.documents?.map {
            val exerciseSetId = it.name.split("/").last()
            it.fields.toHistorySet(exerciseSetId)
        } ?: emptyList()
    }

    companion object {
        val DEFAULT_LOCAL_DATE: LocalDate = LocalDate.parse("1990-01-01")
    }
}