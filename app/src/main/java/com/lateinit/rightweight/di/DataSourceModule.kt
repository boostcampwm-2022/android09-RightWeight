package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.AuthApiService
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppSharedPreferences
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.datasource.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Provides
    @Singleton
    fun getLoginDataSource(
        api: AuthApiService
    ): LoginDataSource {
        return LoginDataSourceImpl(api)
    }

    @Provides
    @Singleton
    fun getRoutineLocalDataSource(
        routineDao: RoutineDao,
        sharedRoutineDao: SharedRoutineDao
    ): RoutineLocalDataSource {
        return RoutineLocalDataSourceImpl(routineDao, sharedRoutineDao)
    }

    @Provides
    @Singleton
    fun getRoutineRemoteDataSource(
        db: AppDatabase,
        api: RoutineApiService,
        appSharedPreferences: AppSharedPreferences
    ): RoutineRemoteDataSource {
        return RoutineRemoteDataSourceImpl(db, api, appSharedPreferences)
    }

    @Provides
    @Singleton
    fun getUserDataSource(appSharedPreferences: AppSharedPreferences): UserDataSource {
        return UserLocalDataSource(appSharedPreferences)
    }

    @Provides
    @Singleton
    fun getHistoryLocalDataSource(historyDao: HistoryDao): HistoryLocalDataSource {
        return HistoryLocalDataSource(historyDao)
    }
}