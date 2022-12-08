package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.SharedRoutineRequestBody
import com.lateinit.rightweight.data.model.DetailResponse
import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.DocumentsResponse
import com.lateinit.rightweight.data.model.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface RoutineApiService {

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: SharedRoutineRequestBody
    ): List<DocumentResponse<SharedRoutineField>>

    @GET("documents/shared_routine/{path}")
    suspend fun getChildrenDocumentName(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<DetailResponse<RemoteData>>

    @GET("documents/shared_routine/{routineId}")
    suspend fun getSharedRoutine(
        @Path("routineId") routineId: String
    ): Response<SharedRoutineField>

    @GET("documents/shared_routine/{routineId}/day")
    suspend fun getSharedRoutineDays(
        @Path("routineId") routineId: String
    ): DocumentsResponse<DayField>?

    @GET("documents/shared_routine/{routineId}/day/{dayId}/exercise")
    suspend fun getSharedRoutineExercises(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String
    ): DocumentsResponse<ExerciseField>?

    @GET("documents/shared_routine/{routineId}/day/{dayId}/exercise/{exerciseId}/exercise_set")
    suspend fun getSharedRoutineExerciseSets(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Path("exerciseId") exerciseId: String
    ): DocumentsResponse<ExerciseSetField>?

    @POST("./documents:commit")
    suspend fun commitTransaction(
        @Body
        writes: WriteRequestBody
    )

}