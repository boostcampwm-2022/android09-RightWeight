package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.database.mediator.Order
import com.lateinit.rightweight.data.model.Documents
import com.lateinit.rightweight.data.model.RoutineCollection
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RoutineApiService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): RoutineCollection

    @GET("routine")
    suspend fun getRoutines(): Documents<RoutineCollection>

    @POST("./documents:runQuery")
    suspend fun getSharedRoutines(
        @Body orderJson: String
    ): Documents<RoutineCollection>
}