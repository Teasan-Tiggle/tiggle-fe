package com.ssafy.tiggle.presentation.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.GetAllUsersUseCase
import com.ssafy.tiggle.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * User 화면의 ViewModel
 * MVVM 패턴의 ViewModel로 UI 상태를 관리하고 비즈니스 로직을 처리
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    
    init {
        loadUsers()
    }
    
    /**
     * UI 이벤트를 처리하는 메소드
     */
    fun onEvent(event: UserUiEvent) {
        when (event) {
            is UserUiEvent.LoadUsers -> loadUsers()
            is UserUiEvent.RefreshUsers -> refreshUsers()
            is UserUiEvent.SelectUser -> selectUser(event.user)
            is UserUiEvent.DeleteUser -> deleteUser(event.userId)
            is UserUiEvent.ClearError -> clearError()
        }
    }
    
    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                getAllUsersUseCase()
                    .catch { exception ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "사용자 목록을 불러오는데 실패했습니다: ${exception.message}"
                            )
                        }
                    }
                    .collect { users ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                users = users,
                                errorMessage = null
                            )
                        }
                    }
            } catch (exception: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "예상치 못한 오류가 발생했습니다: ${exception.message}"
                    )
                }
            }
        }
    }
    
    private fun refreshUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            try {
                getAllUsersUseCase()
                    .catch { exception ->
                        _uiState.update { 
                            it.copy(
                                isRefreshing = false,
                                errorMessage = "새로고침에 실패했습니다: ${exception.message}"
                            )
                        }
                    }
                    .collect { users ->
                        _uiState.update { 
                            it.copy(
                                isRefreshing = false,
                                users = users,
                                errorMessage = null
                            )
                        }
                    }
            } catch (exception: Exception) {
                _uiState.update { 
                    it.copy(
                        isRefreshing = false,
                        errorMessage = "새로고침 중 오류가 발생했습니다: ${exception.message}"
                    )
                }
            }
        }
    }
    
    private fun selectUser(user: com.ssafy.tiggle.domain.entity.User) {
        _uiState.update { it.copy(selectedUser = user) }
    }
    
    private fun deleteUser(userId: Long) {
        viewModelScope.launch {
            // TODO: DeleteUserUseCase 구현 후 사용
            // 현재는 UI 상태에서만 제거
            _uiState.update { currentState ->
                currentState.copy(
                    users = currentState.users.filter { it.id != userId }
                )
            }
        }
    }
    
    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
