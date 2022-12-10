package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.remote.model.RoutineField

interface RoutineRemoteDataSource {
    suspend fun getRoutineByUserId(userId: String): List<DocumentResponse<RoutineField>>

    suspend fun getChildrenDocumentName(path: String): List<String>
}