package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.piggybank.EsgCategory
import com.ssafy.tiggle.domain.usecase.piggybank.PiggyBankUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PiggyBankViewModel @Inject constructor(
    val useCases: PiggyBankUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(PiggyBankState())
    val uiState: StateFlow<PiggyBankState> = _uiState.asStateFlow()

    init {
        setPiggyBankAccount()
        setMainAccount()
        loadPiggyBankSettings()
    }

    //저금통 계좌 확인
    fun setPiggyBankAccount() {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = useCases.getPiggyBankAccountUseCase()

            result
                .onSuccess { account ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            piggyBankAccount = account,
                            hasPiggyBank = true
                        )
                    }
                }
                .onFailure { e ->
                    val isNotFound = (e is HttpException && e.code() == 404)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasPiggyBank = !isNotFound,
                            errorMessage = if (isNotFound) null
                            else e.message ?: "저금통 계좌 가져오기에 실패했습니다."
                        )
                    }
                }
        }
    }

    //주계좌 확인
    fun setMainAccount() {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = useCases.getMainAccountUseCase()

            result
                .onSuccess { account ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mainAccount = account,
                            hasLinkedAccount = true
                        )
                    }
                }
                .onFailure { e ->
                    val isNotFound = (e is HttpException && e.code() == 404)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasLinkedAccount = !isNotFound,
                            errorMessage = if (isNotFound) null
                            else e.message ?: "주계좌 가져오기에 실패했습니다."
                        )
                    }
                }
        }
    }


    fun onToggleAutoDonation(checked: Boolean) {
        if (checked) {
            // UI만 켜고 시트 띄움 (서버 호출은 확인 시에)
            _uiState.update {
                it.copy(
                    piggyBank = it.piggyBank.copy(autoDonation = true),
                    showEsgCategorySheet = true,
                    errorMessage = null
                )
            }
        } else {
            // 바로 서버 반영 (카테고리는 그대로)
            val before = _uiState.value.piggyBank
            _uiState.update { it.copy(piggyBank = it.piggyBank.copy(autoDonation = false)) }

            viewModelScope.launch {
                val result = useCases.setPiggyBankSettingUseCase(
                    autoDonation = false
                )
                result.onSuccess { updated ->
                    _uiState.update { it.copy(piggyBank = updated) }
                }.onFailure { e ->
                    // 롤백
                    _uiState.update {
                        it.copy(
                            piggyBank = before,
                            errorMessage = e.message ?: "자동 기부 해제 실패"
                        )
                    }
                }
            }
        }
    }

    /** 바텀시트에서 카테고리 버튼 눌렀을 때: PiggyBank 안의 id만 바꿔둠(임시) */
    fun onPickEsgCategory(categoryId: Int) {
        _uiState.update {
            val cur = it.piggyBank
            it.copy(
                piggyBank = cur.copy(
                    esgCategory = (cur.esgCategory ?: EsgCategory()).copy(id = categoryId)
                )
            )
        }
    }

    fun onConfirmAutoDonation() {
        val pb = _uiState.value.piggyBank
        val categoryId = pb.esgCategory?.id
        if (categoryId == null) {
            _uiState.update { it.copy(errorMessage = "카테고리를 선택해주세요.") }
            return
        }

        val before = pb
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // 1) 카테고리 먼저 확정
            val r1 = useCases.setEsgCategoryUseCase(categoryId)
            r1.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // 토글 롤백
                        piggyBank = before.copy(autoDonation = false),
                        showEsgCategorySheet = false,
                        errorMessage = e.message ?: "카테고리 설정 실패"
                    )
                }
                return@launch
            }

            // 2) 자동 기부 ON (서버에 켜기)
            val r2 = useCases.setPiggyBankSettingUseCase(autoDonation = true)
            r2.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        piggyBank = updated,
                        showEsgCategorySheet = false
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        piggyBank = before.copy(autoDonation = false), // 실패 시 OFF로 되돌림
                        showEsgCategorySheet = false,
                        errorMessage = e.message ?: "자동 기부 설정 실패"
                    )
                }
            }
        }
    }

    /** 바텀시트 닫기(취소): 서버 호출 없이 토글만 다시 꺼짐 */
    fun onDismissEsgSheet() {
        _uiState.update {
            it.copy(
                showEsgCategorySheet = false,
                piggyBank = it.piggyBank.copy(autoDonation = false)
            )
        }
    }

    fun onToggleAutoSaving(checked: Boolean) {

        val before = _uiState.value.piggyBank.autoSaving
        // 낙관적 업데이트
        _uiState.update { it.copy(piggyBank = it.piggyBank.copy(autoSaving = checked)) }

        viewModelScope.launch {
            val result = useCases.setPiggyBankSettingUseCase(autoSaving = checked)
            result
                .onSuccess { updated ->
                    _uiState.update { it.copy(piggyBank = updated) } // 서버 값으로 동기화
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            piggyBank = it.piggyBank.copy(autoSaving = before),
                            errorMessage = e.message ?: "자동 저금 설정 변경 실패"
                        )
                    }
                }
        }
    }

    fun loadPiggyBankSettings() {
        viewModelScope.launch {
            // 초기 진입 시 로딩 인디케이터를 꼭 보여주고 싶지 않다면 isLoading은 빼도 됨
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = useCases.setPiggyBankSettingUseCase() // ← 프로젝트에 맞는 이름으로 교체
            result
                .onSuccess { settings ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            piggyBank = settings,
                            hasPiggyBank = true
                        )
                    }
                }
                .onFailure { e ->
                    val isNotFound = (e is HttpException && e.code() == 404)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // 404면 설정이 아직 없다는 뜻 → 토글은 false로 유지, hasPiggyBank는 false로
                            hasPiggyBank = if (isNotFound) false else it.hasPiggyBank,
                            errorMessage = if (isNotFound) null
                            else e.message ?: "저금통 설정을 불러오지 못했습니다."
                        )
                    }
                }
        }
    }

}