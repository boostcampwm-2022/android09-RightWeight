package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.model.Documents
import com.lateinit.rightweight.data.model.RootField
import com.lateinit.rightweight.data.model.RoutineCollection
import com.lateinit.rightweight.data.model.SharedRoutineField
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    ): Response<Documents<SharedRoutineField>>
}