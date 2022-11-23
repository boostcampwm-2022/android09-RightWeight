package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.datasource.LoginDataSource
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.UserDataSource
import com.lateinit.rightweight.data.repository.*
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
        userDataSource: UserDataSource
    ): UserRepository {
        return UserRepositoryImpl(userDataSource)
    }
}