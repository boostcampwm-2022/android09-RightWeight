package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.datasource.LoginDataSource
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.LoginRepositoryImpl
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.RoutineRepositoryImpl
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
}