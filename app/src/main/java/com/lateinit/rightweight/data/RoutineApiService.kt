package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.SharedRoutineRequestBody
import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.DocumentsResponse
import com.lateinit.rightweight.data.model.RoutineCollection
import com.lateinit.rightweight.data.remote.model.SharedRoutineDayField
import com.lateinit.rightweight.data.remote.model.SharedRoutineExerciseField
import com.lateinit.rightweight.data.remote.model.SharedRoutineExerciseSetField
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): DocumentResponse<SharedRoutineField>

    @GET("routine")
    suspend fun getRoutines(): List<DocumentResponse<SharedRoutineField>>

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: SharedRoutineRequestBody
    ): List<DocumentResponse<SharedRoutineField>>?

    @GET("documents/shared_routine/{routineId}/day")
    suspend fun getSharedRoutineDays(
        @Path("routineId") routineId: String
    ): DocumentsResponse<SharedRoutineDayField>?

    @GET("documents/shared_routine/{routineId}/day/{dayId}/exercise")
    suspend fun getSharedRoutineExercises(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String
    ): DocumentsResponse<SharedRoutineExerciseField>?

    @GET("documents/shared_routine/{routineId}/day/{dayId}/exercise/{exerciseId}/exercise_set")
    suspend fun getSharedRoutineExerciseSets(
        @Path("routineId") routineId: String,
        @Path("dayId") dayId: String,
        @Path("exerciseId") exerciseId: String
    ): DocumentsResponse<SharedRoutineExerciseSetField>?
}