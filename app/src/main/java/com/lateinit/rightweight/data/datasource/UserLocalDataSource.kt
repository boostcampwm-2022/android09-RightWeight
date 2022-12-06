package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val appPreferencesDataStore: AppPreferencesDataStore
) : UserDataSource {

    override suspend fun saveUser(user: User) {
        appPreferencesDataStore.saveUser(user)
    }

    override fun getUser(): Flow<User?> {
        return appPreferencesDataStore.userInfo
    }
}