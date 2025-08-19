package com.ssafy.tiggle.di

import com.ssafy.tiggle.data.repository.UserRepositoryImpl
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

}
