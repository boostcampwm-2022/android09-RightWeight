package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.model.Documents
import com.lateinit.rightweight.data.model.RoutineCollection
import retrofit2.http.GET

interface DatabaseService {

    @GET("routine/routineID3")
    suspend fun getRoutineById(): RoutineCollection

    @GET("routine")
    suspend fun getRoutines(): Documents<RoutineCollection>
}