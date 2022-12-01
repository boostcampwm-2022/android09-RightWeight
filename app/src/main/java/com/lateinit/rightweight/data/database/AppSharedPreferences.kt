package com.lateinit.rightweight.data.database

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.model.User

class AppSharedPreferences(context: Context) {

    private val userKey = "userInfo"
    private val loginResponseKey = "loginResponse"
    private val sharedRoutinePagingFlagKey = "sharedRoutinePagingFlag"
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun setUser(user: User?) {
        sharedPreferences.edit().putString(userKey, Gson().toJson(user)).apply()
    }

    fun getUser(): User {
        return Gson().fromJson(
            sharedPreferences.getString(userKey, null),
            User::class.java
        ) ?: User(null, null, null)
    }

    fun setLoginResponse(loginResponse: LoginResponse?) {
        sharedPreferences.edit().putString(loginResponseKey, Gson().toJson(loginResponse)).apply()
    }

    fun getLoginResponse(): LoginResponse? {
        return Gson().fromJson(
            sharedPreferences.getString(loginResponseKey, null),
            LoginResponse::class.java
        )
    }

    fun setSharedRoutinePagingFlag(flag: String){
        sharedPreferences.edit().putString(sharedRoutinePagingFlagKey, flag).apply()
    }

    fun getSharedRoutinePagingFlag(): String{
        return Gson().fromJson(
            sharedPreferences.getString(sharedRoutinePagingFlagKey, ""),
            String::class.java
        )
    }
}