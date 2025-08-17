package com.ssafy.tiggle.presentation.ui.user

import com.ssafy.tiggle.domain.entity.User

/**
 * User 화면의 UI 상태를 나타내는 데이터 클래스
 * ViewModel에서 UI로 전달되는 상태 정보
 */
data class UserUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val selectedUser: User? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * User 관련 UI 이벤트
 * 사용자가 수행할 수 있는 액션들을 정의
 */
sealed class UserUiEvent {
    object LoadUsers : UserUiEvent()
    object RefreshUsers : UserUiEvent()
    data class SelectUser(val user: User) : UserUiEvent()
    data class DeleteUser(val userId: Long) : UserUiEvent()
    object ClearError : UserUiEvent()
}
