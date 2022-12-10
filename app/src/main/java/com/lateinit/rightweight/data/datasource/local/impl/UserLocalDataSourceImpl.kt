package com.lateinit.rightweight.data.datasource.local.impl

import com.lateinit.rightweight.data.dataStore.AppPreferencesDataStore
import com.lateinit.rightweight.data.datasource.local.UserLocalDataSource
import com.lateinit.rightweight.data.model.local.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val appPreferencesDataStore: AppPreferencesDataStore
) : UserLocalDataSource {

    override suspend fun saveUser(user: User) {
        appPreferencesDataStore.saveUser(user)
    }

    override fun getUser(): Flow<User?> {
        return appPreferencesDataStore.userInfo
    }

    override suspend fun removeUserInfo() {
        appPreferencesDataStore.saveUser(User("", "", "", "", "", "", "", ""))

    }
}