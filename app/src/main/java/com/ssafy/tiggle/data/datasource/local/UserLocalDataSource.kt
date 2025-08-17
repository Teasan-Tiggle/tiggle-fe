package com.ssafy.tiggle.data.datasource.local

import com.ssafy.tiggle.data.model.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User 로컬 데이터 소스
 * 메모리 기반 캐시를 사용하는 간단한 구현
 * 실제 프로젝트에서는 Room Database 사용 권장
 */
@Singleton
class UserLocalDataSource @Inject constructor() {
    
    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    
    fun getAllUsers(): Flow<List<UserDto>> {
        return _users.asStateFlow()
    }
    
    suspend fun getUserById(id: Long): UserDto? {
        return _users.value.find { it.id == id }
    }
    
    suspend fun saveUsers(users: List<UserDto>) {
        _users.value = users
    }
    
    suspend fun saveUser(user: UserDto) {
        val currentUsers = _users.value.toMutableList()
        val existingIndex = currentUsers.indexOfFirst { it.id == user.id }
        
        if (existingIndex != -1) {
            currentUsers[existingIndex] = user
        } else {
            currentUsers.add(user)
        }
        
        _users.value = currentUsers
    }
    
    suspend fun deleteUser(id: Long) {
        _users.value = _users.value.filter { it.id != id }
    }
    
    suspend fun clearAllUsers() {
        _users.value = emptyList()
    }
}
