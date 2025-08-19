package com.ssafy.tiggle.domain.entity

import android.util.Patterns

/**
 * 사용자 회원가입 도메인 엔티티
 * 
 * 회원가입 프로세스에서 사용되는 핵심 데이터와 유효성 검사 상태를 포함합니다.
 */
data class UserSignUp(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val school: String = "",
    val department: String = "",
    val studentId: String = "",
    
    // 유효성 검사 상태
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val nameError: String? = null,
    val schoolError: String? = null,
    val departmentError: String? = null,
    val studentIdError: String? = null
) {
    /**
     * 모든 필수 필드가 유효한지 확인
     */
    fun isValid(): Boolean {
        return email.isNotBlank() && 
               password.isNotBlank() && 
               confirmPassword.isNotBlank() && 
               name.isNotBlank() && 
               school.isNotBlank() &&
               department.isNotBlank() &&
               studentId.isNotBlank() &&
               emailError == null && 
               passwordError == null && 
               confirmPasswordError == null && 
               nameError == null && 
               schoolError == null &&
               departmentError == null &&
               studentIdError == null
    }
    
    /**
     * 필수 필드가 모두 입력되었는지 확인
     */
    fun hasAllRequiredFields(): Boolean {
        return email.isNotBlank() && 
               password.isNotBlank() && 
               confirmPassword.isNotBlank() && 
               name.isNotBlank() && 
               school.isNotBlank() &&
               department.isNotBlank() &&
               studentId.isNotBlank()
    }
    
    /**
     * 에러가 있는 필드가 있는지 확인
     */
    fun hasErrors(): Boolean {
        return emailError != null || 
               passwordError != null || 
               confirmPasswordError != null || 
               nameError != null || 
               schoolError != null ||
               departmentError != null ||
               studentIdError != null
    }
    
    // ===========================================
    // 유효성 검사 비즈니스 로직
    // ===========================================
    
    /**
     * 이메일 유효성 검사
     */
    fun validateEmail(): String? {
        return when {
            email.isBlank() -> "이메일을 입력해주세요."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "올바른 이메일 형식을 입력해주세요."
            else -> null
        }
    }
    
    /**
     * 비밀번호 유효성 검사
     */
    fun validatePassword(): String? {
        return when {
            password.isBlank() -> "비밀번호를 입력해주세요."
            password.length < 8 -> "비밀번호는 8자 이상이어야 합니다."
            !password.any { it.isUpperCase() } -> "비밀번호에 대문자를 포함해주세요."
            !password.any { it.isLowerCase() } -> "비밀번호에 소문자를 포함해주세요."
            !password.any { it.isDigit() } -> "비밀번호에 숫자를 포함해주세요."
            else -> null
        }
    }
    
    /**
     * 비밀번호 확인 유효성 검사
     */
    fun validateConfirmPassword(): String? {
        return when {
            confirmPassword.isBlank() -> "비밀번호 확인을 입력해주세요."
            password != confirmPassword -> "비밀번호가 일치하지 않습니다."
            else -> null
        }
    }
    
    /**
     * 이름 유효성 검사
     */
    fun validateName(): String? {
        return when {
            name.isBlank() -> "이름을 입력해주세요."
            name.length < 2 -> "이름은 2자 이상이어야 합니다."
            else -> null
        }
    }
    
    /**
     * 학교 유효성 검사
     */
    fun validateSchool(): String? {
        return when {
            school.isBlank() -> "학교를 선택해주세요."
            else -> null
        }
    }
    
    /**
     * 학과 유효성 검사
     */
    fun validateDepartment(): String? {
        return when {
            department.isBlank() -> "학과를 선택해주세요."
            else -> null
        }
    }
    
    /**
     * 학번 유효성 검사
     */
    fun validateStudentId(): String? {
        return when {
            studentId.isBlank() -> "학번을 입력해주세요."
            !studentId.all { it.isDigit() } -> "학번은 숫자만 입력해주세요."
            studentId.length < 4 -> "학번은 4자리 이상이어야 합니다."
            else -> null
        }
    }
    
    /**
     * 전체 유효성 검사를 수행하고 에러가 포함된 새로운 인스턴스 반환
     */
    fun withValidation(): UserSignUp {
        return this.copy(
            emailError = validateEmail(),
            passwordError = validatePassword(),
            confirmPasswordError = validateConfirmPassword(),
            nameError = validateName(),
            schoolError = validateSchool(),
            departmentError = validateDepartment(),
            studentIdError = validateStudentId()
        )
    }
    
    /**
     * 특정 필드만 유효성 검사를 수행하고 업데이트된 인스턴스 반환
     */
    fun validateField(field: ValidationField): UserSignUp {
        return when (field) {
            ValidationField.EMAIL -> copy(emailError = validateEmail())
            ValidationField.PASSWORD -> copy(passwordError = validatePassword())
            ValidationField.CONFIRM_PASSWORD -> copy(confirmPasswordError = validateConfirmPassword())
            ValidationField.NAME -> copy(nameError = validateName())
            ValidationField.SCHOOL -> copy(schoolError = validateSchool())
            ValidationField.DEPARTMENT -> copy(departmentError = validateDepartment())
            ValidationField.STUDENT_ID -> copy(studentIdError = validateStudentId())
        }
    }
}

/**
 * 유효성 검사 대상 필드
 */
enum class ValidationField {
    EMAIL,
    PASSWORD,
    CONFIRM_PASSWORD,
    NAME,
    SCHOOL,
    DEPARTMENT,
    STUDENT_ID
}
