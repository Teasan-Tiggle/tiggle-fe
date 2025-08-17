package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.local.UserLocalDataSource
import com.ssafy.tiggle.data.datasource.remote.UserRemoteDataSource
import com.ssafy.tiggle.data.model.toDomain
import com.ssafy.tiggle.data.model.toDto
import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UserRepository 구현체
 * 로컬 캐시와 원격 API를 조합하여 데이터를 관리
 */
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) : UserRepository {
    
    override fun getAllUsers(): Flow<List<User>> {
        // 먼저 로컬 캐시에서 데이터를 가져오고,
        // 백그라운드에서 원격 데이터를 동기화
        refreshUsersFromRemote()
        
        return localDataSource.getAllUsers()
            .map { userDtos -> userDtos.map { it.toDomain() } }
    }
    
    override suspend fun getUserById(id: Long): User? {
        // 로컬에서 먼저 찾기
        val localUser = localDataSource.getUserById(id)
        if (localUser != null) {
            return localUser.toDomain()
        }
        
        // 로컬에 없으면 원격에서 가져오기
        return remoteDataSource.getUserById(id)
            .getOrNull()
            ?.let { userDto ->
                localDataSource.saveUser(userDto)
                userDto.toDomain()
            }
    }
    
    override suspend fun saveUser(user: User): Result<User> {
        return remoteDataSource.createUser(user.toDto())
            .mapCatching { userDto ->
                localDataSource.saveUser(userDto)
                userDto.toDomain()
            }
    }
    
    override suspend fun updateUser(user: User): Result<User> {
        return remoteDataSource.updateUser(user.toDto())
            .mapCatching { userDto ->
                localDataSource.saveUser(userDto)
                userDto.toDomain()
            }
    }
    
    override suspend fun deleteUser(id: Long): Result<Unit> {
        return remoteDataSource.deleteUser(id)
            .onSuccess {
                localDataSource.deleteUser(id)
            }
    }
    
    private fun refreshUsersFromRemote() {
        // 코루틴으로 백그라운드에서 실행
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.getAllUsers()
                .onSuccess { userDtos ->
                    localDataSource.saveUsers(userDtos)
                }
                .onFailure { exception ->
                    // 에러 로깅 또는 처리
                    println("원격 데이터 동기화 실패: ${exception.message}")
                }
        }
    }
}
