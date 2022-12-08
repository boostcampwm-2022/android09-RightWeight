package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.model.DetailResponse
import com.lateinit.rightweight.data.model.DocumentResponse
import com.lateinit.rightweight.data.model.DocumentsResponse
import com.lateinit.rightweight.data.model.RunQueryBody
import com.lateinit.rightweight.data.model.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.HistoryField
import com.lateinit.rightweight.data.remote.model.RemoteData
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.data.remote.model.RoutineField
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @PATCH("documents/user/{userId}")
    suspend fun backupUserInfo(
        @Path("userId") userId: String,
        @Body userInfoField: RootField
    )

    @POST("./documents:commit")
    suspend fun commitTransaction(
        @Body
        writes: WriteRequestBody
    )

    @GET("documents/routine/{path}")
    suspend fun getChildrenDocumentName(
        @Path(value = "path", encoded = true)
        path: String
    ): DocumentsResponse<DetailResponse<RemoteData>>

    @POST("./documents:runQuery")
    suspend fun getUserRoutine(
        @Body query: RunQueryBody
    ): List<DocumentResponse<RoutineField>>

    @POST("./documents/{userId}:runQuery")
    suspend fun getLastHistory(
        @Path("userId") userId: String,
        @Body query: RunQueryBody
    ): List<DocumentResponse<HistoryField>>
}