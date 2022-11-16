package com.lateinit.rightweight.di

import com.lateinit.rightweight.data.RightWeightRetrofitService
import com.lateinit.rightweight.data.datasource.LoginDataSource
import com.lateinit.rightweight.data.datasource.LoginDataSourceImpl
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.LoginRepositoryImpl
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
        api: RightWeightRetrofitService
    ): LoginDataSource {
        return LoginDataSourceImpl(api)
    }
}