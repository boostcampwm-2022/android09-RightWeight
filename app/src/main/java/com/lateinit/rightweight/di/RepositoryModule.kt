package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.datasource.*
import com.lateinit.rightweight.data.datasource.local.HistoryLocalDataSource
import com.lateinit.rightweight.data.datasource.local.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.local.SharedRoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.local.UserLocalDataSource
import com.lateinit.rightweight.data.datasource.remote.HistoryRemoteDatasource
import com.lateinit.rightweight.data.datasource.remote.LoginDataSource
import com.lateinit.rightweight.data.datasource.remote.RoutineRemoteDataSource
import com.lateinit.rightweight.data.datasource.remote.SharedRoutineRemoteDataSource
import com.lateinit.rightweight.data.datasource.remote.UserRemoteDataSource
import com.lateinit.rightweight.data.repository.*
import com.lateinit.rightweight.data.repository.impl.HistoryRepositoryImpl
import com.lateinit.rightweight.data.repository.impl.LoginRepositoryImpl
import com.lateinit.rightweight.data.repository.impl.RoutineRepositoryImpl
import com.lateinit.rightweight.data.repository.impl.SharedRoutineRepositoryImpl
import com.lateinit.rightweight.data.repository.impl.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginDataSource: LoginDataSource
    ): LoginRepository {
        return LoginRepositoryImpl(loginDataSource)
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(
        routineLocalDataSource: RoutineLocalDataSource,
        routineRemoteDataSource: RoutineRemoteDataSource
    ): RoutineRepository {
        return RoutineRepositoryImpl(routineLocalDataSource, routineRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userLocalDataSource: UserLocalDataSource,
        userRemoteDataSource: UserRemoteDataSource
    ): UserRepository {
        return UserRepositoryImpl(userLocalDataSource, userRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        historyLocalDataSource: HistoryLocalDataSource,
        historyRemoteDatasource: HistoryRemoteDatasource
    ): HistoryRepository {
        return HistoryRepositoryImpl(historyLocalDataSource, historyRemoteDatasource)
    }

    @Provides
    @Singleton
    fun provideSharedRoutineRepository(
        sharedRoutineRemoteDataSource: SharedRoutineRemoteDataSource,
        sharedRoutineLocalDataSource: SharedRoutineLocalDataSource
    ): SharedRoutineRepository {
        return SharedRoutineRepositoryImpl(
            sharedRoutineRemoteDataSource,
            sharedRoutineLocalDataSource
        )
    }
}