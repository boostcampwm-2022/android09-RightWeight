package com.lateinit.rightweight.data

import com.lateinit.rightweight.data.remote.model.RootField
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {
    @POST("documents/user")
    suspend fun backupUserInfo(
        @Query("documentId") userId: String,
        @Body userInfoField: RootField
    )
}