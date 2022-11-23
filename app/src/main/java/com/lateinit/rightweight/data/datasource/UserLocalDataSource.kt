package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.database.AppSharedPreferences
import com.lateinit.rightweight.data.model.User
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(private val appSharedPreferences: AppSharedPreferences) :
    UserDataSource {
    override suspend fun setUser(user: User?) {
        appSharedPreferences.setUser(user)
    }

    override suspend fun getUser(): User {
        return appSharedPreferences.getUser()
    }

    override fun setLoginResponse(loginResponse: LoginResponse?) {
        appSharedPreferences.setLoginResponse(loginResponse)
    }

    override fun getLoginResponse(): LoginResponse? {
        return getLoginResponse()
    }
}