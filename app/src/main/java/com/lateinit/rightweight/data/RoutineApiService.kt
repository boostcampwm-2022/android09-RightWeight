package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.SharedRoutineRequestBody
import com.lateinit.rightweight.data.model.DetailResponse
import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.DocumentsResponse
import com.lateinit.rightweight.data.remote.model.*
import retrofit2.http.*

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): DocumentResponse<SharedRoutineField>

    @GET("routine")
    suspend fun getRoutines(): List<DocumentResponse<SharedRoutineField>>

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: SharedRoutineRequestBody
    ): List<DocumentResponse<SharedRoutineField>>

    @POST("documents/shared_routine")
    suspend fun shareRoutine(
        @Query("documentId") routineId: String,
        @Body rootField: RootField
    ): DocumentResponse<SharedRoutineField>

    @POST("documents/shared_routine/{routineId}/day")
    suspend fun shareRoutineDay(
        @Path("routineId") routineId: String,
        @Query("documentId") dayId: String,
        @Body rootField: RootField
    ): DocumentResponse<DayField>

    @POST("documents/shared_routine/{routineId}/day/{dayId}/exercise")
    suspend fun shareRoutineExercise(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Query("documentId") exerciseId: String,
        @Body rootField: RootField
    ): DocumentResponse<ExerciseField>

    @POST("documents/shared_routine/{routineId}/day/{dayId}/exercise/{exerciseId}/exercise_set")
    suspend fun shareRoutineExerciseSet(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Path("exerciseId") exerciseId: String,
        @Query("documentId") exerciseSetId: String,
        @Body rootField: RootField
    ): DocumentResponse<ExerciseSetField>

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

    @PATCH("documents/shared_routine/{routineId}")
    suspend fun updateSharedRoutineField(
        @Path("routineId") routineId: String,
        @Query("updateMask.fieldPaths") fieldPath: String,
        @Body rootField: RootField
    )
}