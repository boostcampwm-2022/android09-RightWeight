package com.lateinit.rightweight.data.datasource.remote.impl

import com.lateinit.rightweight.data.api.RoutineApiService
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.datasource.remote.RoutineRemoteDataSource
import com.lateinit.rightweight.data.mapper.local.toDay
import com.lateinit.rightweight.data.mapper.local.toExercise
import com.lateinit.rightweight.data.mapper.local.toExerciseSet
import com.lateinit.rightweight.data.mapper.local.toRoutine
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

    override suspend fun getRoutine(routineId: String, order: Int): Routine {
        return api.getRoutineDocument(routineId).fields.toRoutine(routineId, order)
    }

    override suspend fun getRoutineDays(routineId: String): List<Day>? {
        val path = "routine/${routineId}/day"
        val days = api.getDays(path)
        return days.documents?.map {
            val dayId = it.name.split("/").last()
            it.fields.toDay(dayId)
        }
    }

    override suspend fun getRoutineExercises(path: String): List<Exercise>? {
        val exercises = api.getExercises(path)
        return exercises.documents?.map {
            val exerciseId = it.name.split("/").last()
            it.fields.toExercise(exerciseId)
        }
    }

    override suspend fun getRoutineExerciseSets(path: String): List<ExerciseSet>? {
        val exerciseSets = api.getExerciseSets(path)
        return exerciseSets.documents?.map {
            val exerciseSetId = it.name.split("/").last()
            it.fields.toExerciseSet(exerciseSetId)
        }
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocument(path)
        return documentNameList.documents?.map {
            it.name.split("/").last()
        } ?: emptyList()
    }
}