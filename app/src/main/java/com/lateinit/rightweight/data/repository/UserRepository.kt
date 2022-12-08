package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.model.WriteModelData
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun saveUser(user: User)

    fun getUser(): Flow<User?>

    suspend fun backupUserInfo(user: User)

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    suspend fun getUserRoutineInRemote(userId: String): List<String>

    suspend fun getChildrenDocumentName(path: String): List<String>

    suspend fun commitTransaction(writes: List<WriteModelData>)
}