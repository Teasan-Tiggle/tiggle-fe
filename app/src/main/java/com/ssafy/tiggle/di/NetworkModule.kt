package com.ssafy.tiggle.di

import com.ssafy.tiggle.data.datasource.local.AuthDataSource
import com.ssafy.tiggle.data.datasource.remote.AuthApiService
import com.ssafy.tiggle.data.datasource.remote.AuthInterceptor
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
import javax.inject.Named
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
    fun providePrettyHttpLoggingInterceptor(): PrettyHttpLoggingInterceptor =
        PrettyHttpLoggingInterceptor()

    /** ① 인증 없음: 로그인/재발급 등 */
    @Provides
    @Singleton
    @Named("noAuthClient")
    fun provideNoAuthOkHttp(
        pretty: PrettyHttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(pretty)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /** ② no-auth Retrofit (auth 전용) */
    @Provides
    @Singleton
    @Named("noAuthRetrofit")
    fun provideNoAuthRetrofit(
        @Named("noAuthClient") client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** ③ 기본 AuthApiService (UserRepositoryImpl에서 사용됨) */
    @Provides
    @Singleton
    fun provideDefaultAuthApiService(
        @Named("noAuthRetrofit") retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    /** ④ refresh 전용 AuthApiService (재발급에서만 사용) */
    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshAuthApiService(
        @Named("noAuthRetrofit") retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    /** ⑤ AuthInterceptor */
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authDataSource: AuthDataSource,
        @Named("refresh") authApi: AuthApiService
    ): AuthInterceptor = AuthInterceptor(authDataSource, authApi)

    /** ⑥ 인증 있는 Client */
    @Provides
    @Singleton
    @Named("authClient")
    fun provideAuthOkHttp(
        pretty: PrettyHttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)   // ⬅️ 토큰 붙임
        .addInterceptor(pretty)            // ⬅️ 로그
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /** ⑦ 일반 Retrofit (인증 필요한 API) */
    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("authClient") client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** ⑧ 서비스들 */
    @Provides
    @Singleton
    fun provideUniversityApiService(retrofit: Retrofit): UniversityApiService =
        retrofit.create(UniversityApiService::class.java)

}
