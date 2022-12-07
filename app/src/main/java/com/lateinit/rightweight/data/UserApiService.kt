package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.model.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.RootField
import retrofit2.http.Body
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
}