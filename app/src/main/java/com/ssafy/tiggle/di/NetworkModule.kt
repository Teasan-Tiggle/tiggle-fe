package com.ssafy.tiggle.di

import com.ssafy.tiggle.core.network.AuthInterceptor
import com.ssafy.tiggle.core.network.LoggingCookieJar
import com.ssafy.tiggle.core.network.PrettyHttpLoggingInterceptor
import com.ssafy.tiggle.data.datasource.local.AuthDataSource
import com.ssafy.tiggle.data.datasource.remote.AuthApiService
import com.ssafy.tiggle.data.datasource.remote.DonationApiService
import com.ssafy.tiggle.data.datasource.remote.DutchPayApiService
import com.ssafy.tiggle.data.datasource.remote.FcmApiService
import com.ssafy.tiggle.data.datasource.remote.GrowthApiService
import com.ssafy.tiggle.data.datasource.remote.PiggyBankApiService
import com.ssafy.tiggle.data.datasource.remote.UniversityApiService
import com.ssafy.tiggle.data.datasource.remote.UserApiService
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

    @Provides
    @Singleton
    fun provideCookieJar(): LoggingCookieJar = LoggingCookieJar()

    /** 인증 없음: 로그인/재발급 등 */
    @Provides
    @Singleton
    @Named("noAuthClient")
    fun provideNoAuthOkHttp(
        pretty: PrettyHttpLoggingInterceptor,
        cookieJar: LoggingCookieJar
    ): OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(pretty)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /** no-auth Retrofit (auth 전용) */
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

    /** AuthApiService (UserRepositoryImpl에서 사용됨) */
    @Provides
    @Singleton
    fun provideDefaultAuthApiService(
        @Named("noAuthRetrofit") retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    /** refresh 전용 AuthApiService (재발급에서만 사용) */
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
        authInterceptor: AuthInterceptor,
        cookieJar: LoggingCookieJar
    ): OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(authInterceptor)   // ⬅️ 토큰 붙임
        .addInterceptor(pretty)            // ⬅️ 로그
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /** 일반 Retrofit (인증 필요한 API) */
    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("authClient") client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** 서비스들 */
    @Provides
    @Singleton
    fun provideUniversityApiService(retrofit: Retrofit): UniversityApiService =
        retrofit.create(UniversityApiService::class.java)

    @Provides
    @Singleton
    fun providePiggyBankApiService(retrofit: Retrofit): PiggyBankApiService =
        retrofit.create(PiggyBankApiService::class.java)

    @Provides
    @Singleton
    fun provideFcmApiService(retrofit: Retrofit): FcmApiService =
        retrofit.create(FcmApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideDutchPayApiService(retrofit: Retrofit): DutchPayApiService =
        retrofit.create(DutchPayApiService::class.java)

    @Provides
    @Singleton
    fun provideDonationApiService(retrofit: Retrofit): DonationApiService =
        retrofit.create(DonationApiService::class.java)

    @Provides
    @Singleton
    fun provideGrowthService(retrofit: Retrofit): GrowthApiService =
        retrofit.create(GrowthApiService::class.java)
}
