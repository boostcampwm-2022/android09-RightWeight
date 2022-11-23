package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.model.User

interface UserDataSource {
    suspend fun setUser(user: User)
    suspend fun getUser(): User
}