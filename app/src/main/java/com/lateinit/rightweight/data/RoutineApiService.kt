package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.SharedRoutineRequestBody
import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.RoutineCollection
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): RoutineCollection

    @GET("routine")
    suspend fun getRoutines(): List<DocumentResponse<RoutineCollection>>

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body order: SharedRoutineRequestBody
    ): List<DocumentResponse<SharedRoutineField>>
}