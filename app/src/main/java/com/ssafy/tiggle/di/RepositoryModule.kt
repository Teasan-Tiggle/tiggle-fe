package com.ssafy.tiggle.di

import com.ssafy.tiggle.data.repository.AuthRepositoryImpl
import com.ssafy.tiggle.data.repository.DonationRepositoryImpl
import com.ssafy.tiggle.data.repository.DutchPayRepositoryImpl
import com.ssafy.tiggle.data.repository.FcmRepositoryImpl
import com.ssafy.tiggle.data.repository.GrowthRepositoryImpl
import com.ssafy.tiggle.data.repository.PiggyBankRepositoryImpl
import com.ssafy.tiggle.data.repository.UniversityRepositoryImpl
import com.ssafy.tiggle.data.repository.UserRepositoryImpl
import com.ssafy.tiggle.domain.repository.AuthRepository
import com.ssafy.tiggle.domain.repository.DonationRepository
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import com.ssafy.tiggle.domain.repository.FcmRepository
import com.ssafy.tiggle.domain.repository.GrowthRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

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

    @Binds
    @Singleton
    abstract fun bindDutchPayRepository(
        dutchPayRepositoryImpl: DutchPayRepositoryImpl
    ): DutchPayRepository

    @Binds
    @Singleton
    abstract fun bindDonationRepository(
        donationRepositoryImpl: DonationRepositoryImpl
    ): DonationRepository

    @Binds
    @Singleton
    abstract fun bindGrowthRepository(
        growthRepositoryImpl: GrowthRepositoryImpl
    ): GrowthRepository
}
