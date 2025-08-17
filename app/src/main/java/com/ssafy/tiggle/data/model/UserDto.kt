package com.ssafy.tiggle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.tiggle.domain.entity.User

/**
 * API 응답용 User 데이터 모델
 * 네트워크 레이어에서 사용되는 DTO (Data Transfer Object)
 */
data class UserDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("profile_image_url")
    val profileImageUrl: String? = null,
    
    @SerializedName("created_at")
    val createdAt: Long,
    
    @SerializedName("updated_at")
    val updatedAt: Long
)

/**
 * UserDto를 Domain Entity로 변환
 */
fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Domain Entity를 UserDto로 변환
 */
fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
