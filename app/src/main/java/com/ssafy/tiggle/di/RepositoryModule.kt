package com.ssafy.tiggle.di

import com.ssafy.tiggle.data.repository.FcmRepositoryImpl
import com.ssafy.tiggle.data.repository.PiggyBankRepositoryImpl
import com.ssafy.tiggle.data.repository.UniversityRepositoryImpl
import com.ssafy.tiggle.data.repository.UserRepositoryImpl
import com.ssafy.tiggle.domain.repository.FcmRepository
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import com.ssafy.tiggle.domain.repository.UniversityRepository
import com.ssafy.tiggle.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 관련 의존성을 제공하는 Hilt 모듈
 * 인터페이스와 구현체를 바인딩
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindUniversityRepository(
        universityRepositoryImpl: UniversityRepositoryImpl
    ): UniversityRepository

    @Binds
    @Singleton
    abstract fun bindPiggyBankRepository(
        piggyBankRepositoryImpl: PiggyBankRepositoryImpl
    ): PiggyBankRepository

    @Binds
    @Singleton
    abstract fun bindFcmRepository(
        fcmRepositoryImpl: FcmRepositoryImpl
    ): FcmRepository
}
