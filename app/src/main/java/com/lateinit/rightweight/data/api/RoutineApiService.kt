package com.lateinit.rightweight.data.api

import com.lateinit.rightweight.data.model.remote.DetailResponse
import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.model.remote.DocumentsResponse
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoutineApiService {

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: RunQueryBody
    ): List<DocumentResponse<SharedRoutineField>>

    @GET("documents/{path}")
    suspend fun getChildrenDocument(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<DetailResponse<RemoteData>>

    @GET("documents/routine/{path}")
    suspend fun getRoutine(
        @Path("path") path: String
    ): DetailResponse<RoutineField>

    @GET("documents/{path}")
    suspend fun getDays(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<DayField>

    @GET("documents/{path}")
    suspend fun getExercises(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<ExerciseField>

    @GET("documents/{path}")
    suspend fun getExerciseSets(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<ExerciseSetField>

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

    @POST("./documents:runQuery")
    suspend fun getUserRoutine(
        @Body query: RunQueryBody
    ): List<DocumentResponse<RoutineField>>


    @POST("./documents:commit")
    suspend fun commitTransaction(
        @Body
        writes: WriteRequestBody
    )

}