package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.AuthApiService
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.UserApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.database.dao.UserDao
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
        routineDao: RoutineDao
    ): RoutineLocalDataSource {
        return RoutineLocalDataSourceImpl(routineDao)
    }

    @Provides
    @Singleton
    fun getSharedRoutineLocalDataSource(
        sharedRoutineDao: SharedRoutineDao
    ): SharedRoutineLocalDataSource {
        return SharedRoutineLocalDataSourceImpl(sharedRoutineDao)
    }

    @Provides
    @Singleton
    fun getRoutineRemoteDataSource(
        db: AppDatabase,
        api: RoutineApiService,
        appPreferencesDataStore: AppPreferencesDataStore
    ): RoutineRemoteDataSource {
        return RoutineRemoteDataSourceImpl(db, api, appPreferencesDataStore)
    }

    @Provides
    @Singleton
    fun getUserLocalDataSource(userDao: UserDao, appPreferencesDataStore: AppPreferencesDataStore): UserLocalDataSource {
        return UserLocalDataSourceImpl(userDao, appPreferencesDataStore)
    }

    @Provides
    @Singleton
    fun getUserRemoteDataSource(userApiService: UserApiService): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(userApiService)
    }

    @Provides
    @Singleton
    fun getHistoryLocalDataSource(historyDao: HistoryDao): HistoryLocalDataSource {
        return HistoryLocalDataSource(historyDao)
    }
}