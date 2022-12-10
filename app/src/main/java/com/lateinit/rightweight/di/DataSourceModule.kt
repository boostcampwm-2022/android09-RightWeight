package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.api.AuthApiService
import com.lateinit.rightweight.data.api.RoutineApiService
import com.lateinit.rightweight.data.api.UserApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.dataStore.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.database.dao.UserDao
import com.lateinit.rightweight.data.datasource.*
import com.lateinit.rightweight.data.datasource.local.impl.HistoryLocalLocalDataSourceImpl
import com.lateinit.rightweight.data.datasource.local.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.local.impl.RoutineLocalDataSourceImpl
import com.lateinit.rightweight.data.datasource.local.UserLocalDataSource
import com.lateinit.rightweight.data.datasource.local.impl.UserLocalDataSourceImpl
import com.lateinit.rightweight.data.datasource.remote.LoginDataSource
import com.lateinit.rightweight.data.datasource.remote.impl.LoginDataSourceImpl
import com.lateinit.rightweight.data.datasource.local.SharedRoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.local.impl.SharedRoutineLocalDataSourceImpl
import com.lateinit.rightweight.data.datasource.remote.SharedRoutineRemoteDataSource
import com.lateinit.rightweight.data.datasource.remote.impl.SharedRoutineRemoteDataSourceImpl
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.datasource.remote.impl.UserRemoteDataSourceImpl
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
    fun provideLoginDataSource(
        api: AuthApiService
    ): LoginDataSource {
        return LoginDataSourceImpl(api)
    }

    @Provides
    @Singleton
    fun provideRoutineLocalDataSource(
        routineDao: RoutineDao
    ): RoutineLocalDataSource {
        return RoutineLocalDataSourceImpl(routineDao)
    }

    @Provides
    @Singleton
    fun provideSharedRoutineLocalDataSource(
        sharedRoutineDao: SharedRoutineDao
    ): SharedRoutineLocalDataSource {
        return SharedRoutineLocalDataSourceImpl(sharedRoutineDao)
    }

    @Provides
    @Singleton
    fun provideRoutineRemoteDataSource(
        appPreferencesDataStore: AppPreferencesDataStore,
        db: AppDatabase,
        api: RoutineApiService
    ): SharedRoutineRemoteDataSource {
        return SharedRoutineRemoteDataSourceImpl(appPreferencesDataStore, db, api)
    }

    @Provides
    @Singleton
    fun provideUserLocalDataSource(userDao: UserDao, appPreferencesDataStore: AppPreferencesDataStore): UserLocalDataSource {
        return UserLocalDataSourceImpl(userDao, appPreferencesDataStore)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(userApiService: UserApiService): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(userApiService)
    }

    @Provides
    @Singleton
    fun provideHistoryLocalDataSource(historyDao: HistoryDao): HistoryLocalLocalDataSourceImpl {
        return HistoryLocalLocalDataSourceImpl(historyDao)
    }
}