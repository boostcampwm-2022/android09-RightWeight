package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.datasource.*
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
    fun getLoginRepository(
        loginDataSource: LoginDataSource
    ): LoginRepository {
        return LoginRepositoryImpl(loginDataSource)
    }

    @Provides
    @Singleton
    fun getRoutineRepository(
        routineLocalDataSource: RoutineLocalDataSource

    ): RoutineRepository {
        return RoutineRepositoryImpl(routineLocalDataSource)
    }

    @Provides
    @Singleton
    fun getUserRepository(
        userLocalDataSource: UserLocalDataSource,
        userRemoteDataSource: UserRemoteDataSource
    ): UserRepository {
        return UserRepositoryImpl(userLocalDataSource, userRemoteDataSource)
    }

    @Provides
    @Singleton
    fun getHistoryRepository(
        historyLocalDataSource: HistoryLocalDataSource
    ): HistoryRepository {
        return HistoryRepositoryImpl(historyLocalDataSource)
    }

    @Provides
    @Singleton
    fun getSharedRoutineRepository(
        routineRemoteDataSource: RoutineRemoteDataSource,
        sharedRoutineLocalDataSource: SharedRoutineLocalDataSource
    ): SharedRoutineRepository {
        return SharedRoutineRepositoryImpl(routineRemoteDataSource, sharedRoutineLocalDataSource)
    }
}