package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(private val appSharedPreferences: AppSharedPreferences) :
    UserDataSource {
    override suspend fun setUser(user: User?) {
        appSharedPreferences.setUser(user)
    }

    override suspend fun getUser(): User {
        return appSharedPreferences.getUser()
    }

    override fun getUser(): Flow<User?> {
        return appPreferencesDataStore.userInfoFlow
    }
}