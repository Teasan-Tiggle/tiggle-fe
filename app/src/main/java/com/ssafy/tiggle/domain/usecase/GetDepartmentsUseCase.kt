package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.Department
import com.ssafy.tiggle.domain.repository.UniversityRepository
import javax.inject.Inject

/**
 * 학과 목록 조회 UseCase
 * 
 * 학과 목록 조회 비즈니스 로직을 처리합니다.
 */
class GetDepartmentsUseCase @Inject constructor(
    private val universityRepository: UniversityRepository
) {
    /**
     * 학과 목록 조회 실행
     * 
     * @param universityId 대학교 ID
     * @return Result<List<Department>> 성공 시 학과 목록, 실패 시 에러
     */
    suspend operator fun invoke(universityId: Long): Result<List<Department>> {
        return universityRepository.getDepartments(universityId)
    }
}
