package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.SharedRoutineRequestBody
import com.lateinit.rightweight.data.model.*
import com.lateinit.rightweight.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): DocumentResponse<SharedRoutineField>

    @GET("routine")
    suspend fun getRoutines(): List<DocumentResponse<SharedRoutineField>>

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: SharedRoutineRequestBody
    ): List<DocumentResponse<SharedRoutineField>>

    @GET("documents/shared_routine/{path}")
    suspend fun getChildrenDocumentName(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<DetailResponse<RemoteData>>

    @DELETE("documents/shared_routine/{path}")
    suspend fun deleteDocument(
        @Path(value = "path", encoded = true)
        path: String
    ): List<DocumentResponse<SharedRoutineField>>?

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