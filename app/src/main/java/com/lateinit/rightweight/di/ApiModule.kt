package com.lateinit.rightweight.di

import com.lateinit.rightweight.BuildConfig
import com.lateinit.rightweight.data.AuthService
import com.lateinit.rightweight.data.DatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideAuthService(@Named("Auth") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabaseService(@Named("Database") retrofit: Retrofit): DatabaseService {
        return retrofit.create(DatabaseService::class.java)
    }

    @Provides
    @Singleton
    @Named("Auth")
    fun provideAuthRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://identitytoolkit.googleapis.com/v1/")
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named("Database")
    fun provideDatabaseRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/projects/right-weight/databases/(default)/documents/")
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideGsonConvertFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun buildOkHttpClient(
        customInterceptor: Interceptor
    ): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(customInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun customInterceptor(): Interceptor = Interceptor {
        chain -> chain.run {
            proceed(
                request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
            )
        }
    }
}