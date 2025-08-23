package com.ssafy.tiggle.di

import com.ssafy.tiggle.data.datasource.remote.AuthApiService
import com.ssafy.tiggle.data.datasource.remote.PrettyHttpLoggingInterceptor
import com.ssafy.tiggle.data.datasource.remote.UniversityApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 네트워크 관련 의존성을 제공하는 Hilt 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://43.203.36.96/api/"

    @Provides
    @Singleton
    fun providePrettyHttpLoggingInterceptor(): PrettyHttpLoggingInterceptor {
        return PrettyHttpLoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        prettyLoggingInterceptor: PrettyHttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(prettyLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUniversityApiService(retrofit: Retrofit): UniversityApiService {
        return retrofit.create(UniversityApiService::class.java)
    }

}
