package com.lateinit.rightweight.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.lateinit.rightweight.data.model.local.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferencesDataStore @Inject constructor(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "RightWeight")

    val userInfo: Flow<User?> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[USER_INFO], User::class.java) ?: null
        }

    val sharedRoutinePagingFlag: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PAGING_FLAG] ?: ""
        }

    suspend fun saveUser(user: User) {
        context.dataStore.edit {
            it[USER_INFO] = Gson().toJson(user)
        }
    }
    
    suspend fun saveSharedRoutinePagingFlag(flag: String) {
        context.dataStore.edit {
            it[PAGING_FLAG] = flag
        }
    }
    
    companion object {
        private val USER_INFO = stringPreferencesKey("user")
        private val PAGING_FLAG = stringPreferencesKey("paging_flag")
    }
}