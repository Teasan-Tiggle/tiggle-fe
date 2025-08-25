package com.ssafy.tiggle.presentation.ui.auth.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.domain.entity.auth.UserSignUp
import com.ssafy.tiggle.domain.entity.auth.Department
import com.ssafy.tiggle.domain.entity.auth.University
import com.ssafy.tiggle.presentation.ui.components.TiggleAllAgreeCheckboxItem
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleCheckboxItem
import com.ssafy.tiggle.presentation.ui.components.TiggleDropdown
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleTextField

/**
 * 회원가입 메인 화면
 * 단계별로 다른 콘텐츠를 보여줌
 */
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    onSignUpComplete: () -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 회원가입 실패 시 Toast 표시 및 로그인 화면으로 돌아가기
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            if (uiState.currentStep == SignUpStep.SCHOOL && !uiState.isLoading) {
                // 회원가입 API 호출 실패 시에만 Toast 표시
                Toast.makeText(context, "회원가입 실패: $error", Toast.LENGTH_LONG).show()
                // 잠시 후 로그인 화면으로 돌아가기
                kotlinx.coroutines.delay(2000)
                onBackClick()
            }
        }
    }

    when (uiState.currentStep) {
        SignUpStep.TERMS -> {
            TermsAgreementScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onTermsChange = viewModel::updateTermsAgreement,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        SignUpStep.EMAIL -> {
            EmailInputScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onEmailChange = viewModel::updateEmail,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        SignUpStep.PASSWORD -> {
            PasswordInputScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onPasswordChange = viewModel::updatePassword,
                onConfirmPasswordChange = viewModel::updateConfirmPassword,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        SignUpStep.NAME -> {
            NameInputScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onNameChange = viewModel::updateName,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        SignUpStep.PHONE -> {
            PhoneInputScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onPhoneChange = viewModel::updatePhone,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        SignUpStep.SCHOOL -> {
            SchoolInformationScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onSchoolChange = viewModel::updateSchool,
                onDepartmentChange = viewModel::updateDepartment,
                onStudentIdChange = viewModel::updateStudentId,
                onLoadUniversities = viewModel::loadUniversities,
                onNextClick = {
                    viewModel.completeSignUp()
                }
            )
        }

        SignUpStep.COMPLETE -> {
            SignUpCompleteScreen(
                onComplete = {
                    viewModel.resetSignUpState()
                    onSignUpComplete()
                }
            )
        }
    }
}

/**
 * 1단계: 약관 동의 화면
 */
@Composable
private fun TermsAgreementScreen(
    uiState: SignUpUiState,
    onBackClick: () -> Unit,
    onTermsChange: (TermsType, Boolean) -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "동의하고 가입하기",
                onClick = onNextClick,
                enabled = uiState.termsData.allRequired,
                variant = if (uiState.termsData.allRequired) TiggleButtonVariant.Primary else TiggleButtonVariant.Disabled
            )
        }
    ) {
        Column {
            Text(
                text = "티글 서비스 이용 약관에\n동의해주세요.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "새로 바뀐 약관을 자세히 읽어보고\n아래의 모든 필수 항목에 동의해주세요.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 전체 동의
            TiggleAllAgreeCheckboxItem(
                text = "모든 약관에 동의합니다.",
                isChecked = uiState.termsData.allTerms,
                onCheckedChange = { onTermsChange(TermsType.ALL, it) },
                onDetailClick = { /* TODO: Show service terms detail */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 개별 약관들
            TiggleCheckboxItem(
                text = "서비스 이용약관 (필수)",
                isChecked = uiState.termsData.serviceTerms,
                onCheckedChange = { onTermsChange(TermsType.SERVICE, it) },
                onDetailClick = { /* TODO: Show service terms detail */ }

            )

            TiggleCheckboxItem(
                text = "개인정보 수집 이용 동의 (필수)",
                isChecked = uiState.termsData.privacyPolicy,
                onCheckedChange = { onTermsChange(TermsType.PRIVACY, it) },


                )

            TiggleCheckboxItem(
                text = "마케팅 정보 수신 동의 (선택)",
                isChecked = uiState.termsData.marketingOptional,
                onCheckedChange = { onTermsChange(TermsType.MARKETING, it) },

                )

            TiggleCheckboxItem(
                text = "위치정보 이용약관 (선택)",
                isChecked = uiState.termsData.locationOptional,
                onCheckedChange = { onTermsChange(TermsType.LOCATION, it) },

                )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 2단계: 이메일 입력 화면
 */
@Composable
private fun EmailInputScreen(
    uiState: SignUpUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "다음",
                onClick = onNextClick,
                enabled = uiState.userData.email.isNotBlank()
            )
        }
    ) {
        Column {
            Text(
                text = "이메일을\n입력해주세요.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            TiggleTextField(
                value = uiState.userData.email,
                onValueChange = onEmailChange,
                label = "이메일",
                placeholder = "이메일을 입력해주세요",
                keyboardType = KeyboardType.Email,
                isError = uiState.userData.emailError != null,
                errorMessage = uiState.userData.emailError
            )
        }
    }
}

/**
 * 3단계: 비밀번호 입력 화면
 */
@Composable
private fun PasswordInputScreen(
    uiState: SignUpUiState,
    onBackClick: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "다음",
                onClick = onNextClick,
                enabled = uiState.userData.password.isNotBlank() &&
                        uiState.userData.confirmPassword.isNotBlank()
            )
        }
    ) {
        Column {
            Text(
                text = "비밀번호를\n입력해주세요.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            TiggleTextField(
                value = uiState.userData.password,
                onValueChange = onPasswordChange,
                label = "비밀번호",
                placeholder = "비밀번호를 입력해주세요",
                isPassword = true,
                isError = uiState.userData.passwordError != null,
                errorMessage = uiState.userData.passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            TiggleTextField(
                value = uiState.userData.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "비밀번호 확인",
                placeholder = "비밀번호를 다시 입력해주세요",
                isPassword = true,
                isError = uiState.userData.confirmPasswordError != null,
                errorMessage = uiState.userData.confirmPasswordError
            )
        }
    }
}

/**
 * 4단계: 이름 입력 화면
 */
@Composable
private fun NameInputScreen(
    uiState: SignUpUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "다음",
                onClick = onNextClick,
                enabled = uiState.userData.name.isNotBlank()
            )
        }
    ) {
        Column {
            Text(
                text = "이름을\n입력해주세요.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            TiggleTextField(
                value = uiState.userData.name,
                onValueChange = onNameChange,
                label = "이름",
                placeholder = "이름을 입력해주세요",
                isError = uiState.userData.nameError != null,
                errorMessage = uiState.userData.nameError
            )
        }
    }
}

/**
 * 5단계: 전화번호 입력 화면
 */
@Composable
private fun PhoneInputScreen(
    uiState: SignUpUiState,
    onBackClick: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "다음",
                onClick = onNextClick,
                enabled = uiState.userData.phone.isNotBlank()
            )
        }
    ) {
        Column {
            Text(
                text = "전화번호를\n입력해주세요.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            TiggleTextField(
                value = uiState.userData.phone,
                onValueChange = onPhoneChange,
                label = "전화번호",
                placeholder = "예: 010-1234-5678",
                keyboardType = KeyboardType.Phone,
                isError = uiState.userData.phoneError != null,
                errorMessage = uiState.userData.phoneError
            )
        }
    }
}

/**
 * 5단계: 학교 정보 입력 화면 (학교/학과/학번 통합)
 */
@Composable
private fun SchoolInformationScreen(
    uiState: SignUpUiState,
    onBackClick: () -> Unit,
    onSchoolChange: (String) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onStudentIdChange: (String) -> Unit,
    onLoadUniversities: () -> Unit,
    onNextClick: () -> Unit
) {
    // 화면 진입 시 대학교 목록 로드
    LaunchedEffect(Unit) {
        onLoadUniversities()
    }

    // 드롭다운에 표시할 학교/학과 이름 목록
    val schoolNames = uiState.universities.map { it.name }
    val departmentNames = uiState.departments.map { it.name }

    // 현재 선택된 학교/학과 이름
    val selectedSchoolName =
        uiState.universities.find { "${it.id}" == uiState.userData.universityId }?.name ?: ""
    val selectedDepartmentName =
        uiState.departments.find { "${it.id}" == uiState.userData.departmentId }?.name ?: ""

    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = if (uiState.isLoading) "가입 중..." else "회원가입 완료하기",
                onClick = onNextClick,
                enabled = !uiState.isLoading &&
                        uiState.userData.universityId.isNotBlank() &&
                        uiState.userData.departmentId.isNotBlank() &&
                        uiState.userData.studentId.isNotBlank() &&
                        uiState.userData.schoolError == null &&
                        uiState.userData.departmentError == null &&
                        uiState.userData.studentIdError == null,
                isLoading = uiState.isLoading
            )
        }
    ) {
        Column {
            Text(
                text = "학교 정보를 입력해주세요.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "학교, 학과, 학번을 모두 입력해주세요.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 학교 선택
            TiggleDropdown(
                label = "학교",
                selectedValue = selectedSchoolName,
                options = schoolNames,
                onValueChange = { schoolName ->
                    // 선택된 학교 이름으로 ID를 찾아서 전달
                    val selectedUniversity = uiState.universities.find { it.name == schoolName }
                    selectedUniversity?.let { university ->
                        onSchoolChange("${university.id}")
                    }
                },
                placeholder = "학교를 선택해주세요"
            )

            // 학교 에러 메시지
            uiState.userData.schoolError?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 학과 선택
            TiggleDropdown(
                label = "학과",
                selectedValue = selectedDepartmentName,
                options = departmentNames,
                onValueChange = { departmentName ->
                    // 선택된 학과 이름으로 ID를 찾아서 전달
                    val selectedDepartment = uiState.departments.find { it.name == departmentName }
                    selectedDepartment?.let { department ->
                        onDepartmentChange("${department.id}")
                    }
                },
                placeholder = "학과를 선택해주세요"
            )

            // 학과 에러 메시지
            uiState.userData.departmentError?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 학번 입력
            TiggleTextField(
                value = uiState.userData.studentId,
                onValueChange = onStudentIdChange,
                label = "학번",
                placeholder = "예: 2024001234",
                keyboardType = KeyboardType.Number,
                errorMessage = uiState.userData.studentIdError
            )

            // 에러 메시지 표시
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * 6단계: 가입 완료 화면
 */
@Composable
private fun SignUpCompleteScreen(
    onComplete: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = false,
        showLogo = true,
        bottomButton = {
            TiggleButton(
                text = "로그인 바로가기",
                onClick = onComplete
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "가입을 축하합니다!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "티글의 혁신적인 서비스로 새로운 경험을\n시작해 보세요. 성공적인 시작을 응원합니다!",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

// ========== Preview 섹션 ==========

@Preview(showBackground = true)
@Composable
private fun SignUpTermsScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.TERMS,
        termsData = TermsData(
            serviceTerms = true,
            privacyPolicy = false,
            marketingOptional = false,
            locationOptional = false
        )
    )

    TermsAgreementScreen(
        uiState = uiState,
        onBackClick = {},
        onTermsChange = { _, _ -> },
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpEmailScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.EMAIL,
        userData = UserSignUp(
            email = "user@example.com"
        )
    )

    EmailInputScreen(
        uiState = uiState,
        onBackClick = {},
        onEmailChange = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpPasswordScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.PASSWORD,
        userData = UserSignUp(
            password = "password123",
            confirmPassword = "password123"
        )
    )

    PasswordInputScreen(
        uiState = uiState,
        onBackClick = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpPasswordErrorScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.PASSWORD,
        userData = UserSignUp(
            password = "password123",
            confirmPassword = "different",
            passwordError = "비밀번호는 8자 이상이어야 합니다.",
            confirmPasswordError = "비밀번호가 일치하지 않습니다."
        )
    )

    PasswordInputScreen(
        uiState = uiState,
        onBackClick = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpNameScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.NAME,
        userData = UserSignUp(
            name = "홍길동"
        )
    )

    NameInputScreen(
        uiState = uiState,
        onBackClick = {},
        onNameChange = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpPhoneScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.PHONE,
        userData = UserSignUp(
            phone = "01012345678"
        )
    )

    PhoneInputScreen(
        uiState = uiState,
        onBackClick = {},
        onPhoneChange = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpSchoolScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.SCHOOL,
        userData = UserSignUp(
            universityId = "1"
        ),
        universities = listOf(
            University(1, "SSAFY"),
            University(2, "서울대학교")
        ),
        departments = listOf(
            Department(1, "컴퓨터공학과"),
            Department(2, "소프트웨어학과")
        )
    )

    SchoolInformationScreen(
        uiState = uiState,
        onBackClick = {},
        onSchoolChange = {},
        onDepartmentChange = {},
        onStudentIdChange = {},
        onLoadUniversities = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpCompleteScreenPreview() {
    SignUpCompleteScreen(
        onComplete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpLoadingScreenPreview() {
    val uiState = SignUpUiState(
        currentStep = SignUpStep.SCHOOL,
        isLoading = true,
        userData = UserSignUp(
            universityId = "1"
        ),
        universities = listOf(
            University(1, "SSAFY"),
            University(2, "서울대학교")
        ),
        isUniversitiesLoading = true
    )

    SchoolInformationScreen(
        uiState = uiState,
        onBackClick = {},
        onSchoolChange = {},
        onDepartmentChange = {},
        onStudentIdChange = {},
        onLoadUniversities = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SignUpMainFlowPreview() {
    // 전체 플로우를 보여주는 Preview
    SignUpScreen(
        onBackClick = {},
        onSignUpComplete = {}
    )
}
