package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): RoutineCollection

    @GET("routine")
    suspend fun getRoutines(): Documents<RoutineCollection>

    @POST("shared_routine")
    suspend fun shareRoutine(
        @Query("documentId") routineId: String,
        @Body rootField: RootField
    ): Documents<SharedRoutineField>

    @POST("shared_routine/{routineId}/day")
    suspend fun shareRoutineDay(
        @Path("routineId") routineId: String,
        @Query("documentId") dayId: String,
        @Body rootField: RootField
    ): Documents<DayField>

    @POST("shared_routine/{routineId}/day/{dayId}/exercise")
    suspend fun shareRoutineExercise(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Query("documentId") exerciseId: String,
        @Body rootField: RootField
    ): Documents<ExerciseField>

    @POST("shared_routine/{routineId}/day/{dayId}/exercise/{exerciseId}/exercise_set")
    suspend fun shareRoutineExerciseSet(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Path("exerciseId") exerciseId: String,
        @Query("documentId") exerciseSetId: String,
        @Body rootField: RootField
    ): Documents<ExerciseSetField>

}