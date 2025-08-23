package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.University
import com.ssafy.tiggle.domain.repository.UniversityRepository
import javax.inject.Inject

/**
 * 대학교 목록 조회 UseCase
 * 
 * 대학교 목록 조회 비즈니스 로직을 처리합니다.
 */
class GetUniversitiesUseCase @Inject constructor(
    private val universityRepository: UniversityRepository
) {
    /**
     * 대학교 목록 조회 실행
     * 
     * @return Result<List<University>> 성공 시 대학교 목록, 실패 시 에러
     */
    suspend operator fun invoke(): Result<List<University>> {
        return universityRepository.getUniversities()
    }
}
