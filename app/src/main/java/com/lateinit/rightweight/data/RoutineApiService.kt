package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.SharedRoutineRequestBody
import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.RoutineCollection
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): DocumentResponse<SharedRoutineField>

    @GET("routine")
    suspend fun getRoutines(): List<DocumentResponse<SharedRoutineField>>

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: SharedRoutineRequestBody
    ): List<DocumentResponse<SharedRoutineField>>
    suspend fun getRoutines(): DocumentResponse<RoutineCollection>

    @POST("shared_routine")
    suspend fun shareRoutine(
        @Query("documentId") routineId: String,
        @Body rootField: RootField
    ): DocumentResponse<SharedRoutineField>

    @POST("shared_routine/{routineId}/day")
    suspend fun shareRoutineDay(
        @Path("routineId") routineId: String,
        @Query("documentId") dayId: String,
        @Body rootField: RootField
    ): DocumentResponse<DayField>

    @POST("shared_routine/{routineId}/day/{dayId}/exercise")
    suspend fun shareRoutineExercise(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Query("documentId") exerciseId: String,
        @Body rootField: RootField
    ): DocumentResponse<ExerciseField>

    @POST("shared_routine/{routineId}/day/{dayId}/exercise/{exerciseId}/exercise_set")
    suspend fun shareRoutineExerciseSet(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Path("exerciseId") exerciseId: String,
        @Query("documentId") exerciseSetId: String,
        @Body rootField: RootField
    ): DocumentResponse<ExerciseSetField>

}