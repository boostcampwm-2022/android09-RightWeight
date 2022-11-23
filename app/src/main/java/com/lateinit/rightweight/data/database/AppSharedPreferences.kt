package com.lateinit.rightweight.data.database

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.model.User

class AppSharedPreferences(context: Context) {

    private val userKey = "userInfo"
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun setUser(user: User) {
        sharedPreferences.edit().putString(userKey, Gson().toJson(user)).commit()
    }

    fun getUser(): User {
        return Gson().fromJson(
            sharedPreferences.getString(userKey, null),
            User::class.java
        ) ?: User(null, null, null)
    }
}