package com.lateinit.rightweight.data.api

import com.lateinit.rightweight.data.model.remote.DetailResponse
import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.model.remote.DocumentsResponse
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.HistoryField
import com.lateinit.rightweight.data.remote.model.RemoteData
import com.lateinit.rightweight.data.remote.model.RootField
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


    @POST("documents/user/{userId}/:runQuery")
    suspend fun getLastHistoryDate(
        @Path(value = "userId", encoded = true) userId: String,
        @Body query: RunQueryBody
    ): List<DocumentResponse<HistoryField>>
}