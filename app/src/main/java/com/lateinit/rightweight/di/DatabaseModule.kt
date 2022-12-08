package com.lateinit.rightweight.di

import android.content.Context
import androidx.room.Room
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
    }

    @Provides
    @Singleton
    fun getRoutineDao(appDatabase: AppDatabase): RoutineDao {
        return appDatabase.routineDao()
    }

    @Provides
    @Singleton
    fun getHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    @Singleton
    fun getSharedRoutineDao(appDatabase: AppDatabase): SharedRoutineDao {
        return appDatabase.sharedRoutineDao()
    }

    @Provides
    @Singleton
    fun getUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun getAppPreferencesDataStore(@ApplicationContext context: Context): AppPreferencesDataStore {
        return AppPreferencesDataStore(context)
    }

    companion object {
        private const val DB_NAME = "right_weight.db"
    }
}