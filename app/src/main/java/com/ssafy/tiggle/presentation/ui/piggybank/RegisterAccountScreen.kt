package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.R
import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder
import com.ssafy.tiggle.domain.entity.piggybank.RegisterAccount
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleTextField
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import com.ssafy.tiggle.presentation.ui.theme.TiggleSkyBlue

@Composable
fun RegisterAccountScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterAccountViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    isEdit: Boolean = false,
    onFinish: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // 공통 Back 핸들러: 첫 단계면 pop, 아니면 단계-뒤로
    val handleTopBack: () -> Unit = {
        if (uiState.registerAccountStep == RegisterAccountStep.ACCOUNT) {
            onBackClick()  // 스택에서 화면 제거
        } else {
            viewModel.goToPreviousStep()
        }
    }
    val title = if (isEdit) "계좌 수정" else "계좌 등록"

    when (uiState.registerAccountStep) {
        RegisterAccountStep.ACCOUNT -> {
            AccountInputScreen(
                uiState = uiState,
                onBackClick = handleTopBack,
                onAccountChange = viewModel::updateAccountNum,
                onConfirmClick = { viewModel.fetchAccountHolder() },
                onDismissError = viewModel::clearError,
                title = title
            )
        }

        RegisterAccountStep.ACCOUNTSUCCESS -> {
            AccountInputSuccessScreen(
                uiState = uiState,
                onBackClick = handleTopBack,
                onStartVerification = { viewModel.requestOneWon() },
                title = title
            )

        }

        RegisterAccountStep.SENDCODE -> {
            SendCodeScreen(
                uiState = uiState,
                onBackClick = handleTopBack,
                onNextClick = { viewModel.goToNextStep() },
                title = title
            )
        }

        RegisterAccountStep.CERTIFICATION -> {
            CertificationScreen(
                uiState = uiState,
                onCodeChange = viewModel::updateCode,
                onBackClick = handleTopBack,
                onResendClick = { viewModel.resendOneWon() },
                onNextClick = { viewModel.confirmCodeAndRegisterPrimary() },
                title = title
            )
        }

        RegisterAccountStep.SUCCESS -> {
            RegisterSuccessScreen(
                uiState = uiState,
                onNextClick = onFinish
            )
        }

    }
}


@Composable
fun AccountInputScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onAccountChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissError: () -> Unit,
    title: String
) {
    TiggleScreenLayout(
        showBackButton = true,
        title = title,
        onBackClick = onBackClick,
        bottomButton = {
            val keyboard = LocalSoftwareKeyboardController.current
            val buttonEnabled =
                uiState.registerAccount.accountNum.isNotBlank() &&
                        uiState.registerAccount.accountNumError == null &&
                        !uiState.isLoading

            TiggleButton(
                text = if (uiState.isLoading) "확인 중..." else "확인",
                onClick = {
                    keyboard?.hide()
                    onConfirmClick()
                },
                enabled = buttonEnabled,
            )
        }
    ) {

        Column(Modifier.padding(16.dp)) {

            Spacer(Modifier.height(16.dp))

            //상단 설명
            Image(
                painter = painterResource(id = R.drawable.bank), contentDescription = "은행 아이콘",
                Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "계좌 등록",
                    color = Color.Black,
                    fontSize = 22.sp,
                    style = AppTypography.headlineLarge,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "잔돈 적립과 기부를 위해\n 내 계좌를 등록해주세요.",
                    color = TiggleGrayText,
                    fontSize = 13.sp,
                    style = AppTypography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(100.dp))

            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "신한 은행 계좌번호", style = AppTypography.bodyLarge, fontSize = 15.sp)
                Spacer(Modifier.height(3.dp))
                TiggleTextField(
                    uiState.registerAccount.accountNum,
                    onValueChange = onAccountChange,
                    label = "",
                    placeholder = "계좌번호를 입력해주세요.",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.registerAccount.accountNumError != null,
                    errorMessage = uiState.registerAccount.accountNumError
                )
            }
        }
    }

    //실패 다이얼로그
    if (uiState.errorMessage != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = onDismissError) {
                    Text("확인")
                }
            },
            title = { Text("확인 실패") },
            text = { Text(uiState.errorMessage!!) },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AccountInputSuccessScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onStartVerification: () -> Unit,
    title: String
) {
    TiggleScreenLayout(
        showBackButton = true,
        title = title,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = if (uiState.isLoading) "요청 중..." else "1원 인증 시작",
                onClick = onStartVerification,
                enabled = !uiState.isLoading
            )
        }
    ) {

        Column(Modifier.padding(20.dp)) {

            Spacer(Modifier.height(10.dp))

            //상단 설명
            Image(
                painter = painterResource(id = R.drawable.bank), contentDescription = "은행 아이콘",
                Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "계좌 등록",
                    color = Color.Black,
                    fontSize = 22.sp,
                    style = AppTypography.headlineLarge,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "잔돈 적립과 기부를 위해\n 내 계좌를 등록해주세요.",
                    color = TiggleGrayText,
                    fontSize = 13.sp,
                    style = AppTypography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(100.dp))

            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = uiState.accountHolder.userName,
                    style = AppTypography.bodyLarge,
                    fontSize = 30.sp
                )
                Spacer(Modifier.height(3.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, TiggleGrayLight, RoundedCornerShape(16.dp))
                        .padding(10.dp, 15.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shinhan),
                        contentDescription = "신한 로고",
                        Modifier.size(50.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "신한은행 ${uiState.accountHolder.accountNo}",
                        style = AppTypography.bodyMedium,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, TiggleGrayLight, RoundedCornerShape(12.dp))
                        .background(TiggleSkyBlue) // 연한 파란색 배경
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 아이콘
                    Image(
                        painter = painterResource(id = R.drawable.lock), // 자물쇠 아이콘 리소스
                        contentDescription = "보안 아이콘",
                        modifier = Modifier.size(24.dp)
                    )

                    // 텍스트
                    Column {
                        Text(
                            text = "안전한 계좌 등록",
                            style = AppTypography.bodyMedium,
                            color = Color(0xFF0077CC) // 강조 파란색
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "계좌 정보는 암호화되어 안전하게 보관되며,\n" +
                                    "1원 인증을 통해 계좌 소유주를 확인합니다.\n" +
                                    "잔돈 적립과 더치페이 외에는 사용되지 않습니다.",
                            style = AppTypography.bodySmall,
                            color = TiggleGrayText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SendCodeScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    title: String
) {
    // 하단 버튼 영역 만큼의 여유 (필요에 따라 조정: 80~96dp 권장)
    val bottomBarPadding = 96.dp

    TiggleScreenLayout(
        showBackButton = true,
        title = title,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "인증번호 입력하기",
                onClick = onNextClick,
                enabled = true
            )
        }
    ) {
        // ⬇️ 기존의 Column을 content 슬롯 안으로 이동
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                // 하단 고정 버튼과 겹치지 않도록 여유 공간 확보
                .padding(bottom = bottomBarPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "송금 완료 아이콘",
                modifier = Modifier.size(170.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text("1원 송금 완료", style = AppTypography.headlineLarge, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "계좌로 1원이 입금되었습니다.\n입금자명을 확인해주세요.",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TiggleGrayLight, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shinhan),
                    contentDescription = "신한 로고",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "신한은행 123-456-****12 계좌로\n1원이 성공적으로 입금되었습니다.",
                    style = AppTypography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TiggleGrayLight, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("입금자명 확인 방법", style = AppTypography.bodyMedium, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Text("1. 계좌 예금주명을 입력해주세요.", style = AppTypography.bodySmall)
                Text("2. 입금자명: 티끌1234", style = AppTypography.bodySmall)
                Text("3. 위 4자리 \"1234\" 확인", style = AppTypography.bodySmall)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TiggleGrayLight, RoundedCornerShape(12.dp))
                    .background(TiggleSkyBlue)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.money),
                    contentDescription = "입금 아이콘",
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "입금 확인 방법",
                        style = AppTypography.bodyMedium,
                        color = Color(0xFF0077CC)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• 은행 앱에서 입출금 내역 확인\n" +
                                "• SMS 입금 알림 확인\n" +
                                "• 인터넷뱅킹에서 거래내역 조회\n" +
                                "• 입금자명 끝 4자리 숫자가 인증번호입니다.",
                        style = AppTypography.bodySmall,
                        color = TiggleGrayText
                    )
                }
            }

        }
    }
}


@Composable
fun CertificationScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onCodeChange: (String) -> Unit,
    onResendClick: () -> Unit,
    onNextClick: () -> Unit,
    title: String
) {
    val code = uiState.registerAccount.code
    val error = uiState.registerAccount.codeError
    val attemptsLeft = uiState.registerAccount.attemptsLeft

    TiggleScreenLayout(
        showBackButton = true,
        title = title,
        onBackClick = onBackClick,
        bottomButton = {
            val enabled = uiState.registerAccount.code.length == 4 &&
                    uiState.registerAccount.codeError == null
            TiggleButton(
                text = if (uiState.isLoading) "확인 중..." else "인증 완료",
                onClick = onNextClick,
                enabled = !uiState.isLoading && enabled
            )
        }
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(30.dp))

            Column(
                Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("인증번호 입력", style = AppTypography.headlineLarge, fontSize = 22.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "계좌로 입금된 1원의 입금자명\n뒤 4자리를 입력하세요.",
                    style = AppTypography.bodySmall,
                    color = TiggleGrayText,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(50.dp))

                // 입력 카드
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TiggleGrayLight, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        "인증번호 (4자리)",
                        style = AppTypography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))

                    OtpCodeBoxes(
                        value = code,
                        onValueChange = onCodeChange,
                        error = error,
                        boxCount = 4
                    )

                    Spacer(Modifier.height(8.dp))
                    if (error != null) {
                        Text(error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    } else {
                        Text(
                            "입금자명에서 뒤 4자리 숫자를 입력하세요.",
                            style = AppTypography.bodySmall,
                            color = TiggleGrayText,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 남은 시도 횟수
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TiggleSkyBlue)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("남은 인증 시도 횟수: ${attemptsLeft}회", style = AppTypography.bodySmall)
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    text = "인증번호를 받지 못하셨나요?",
                    style = AppTypography.bodySmall,
                    color = TiggleGrayText
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onResendClick,
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    border = outlinedButtonBorder(enabled = !uiState.isLoading),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                ) {

                    Text("1원 재송금", color = TiggleBlue)
                }
            }

            Spacer(Modifier.height(60.dp))

        }
    }
}

/** OTP 4칸 입력 박스: 숨겨진 텍스트필드 + 박스 시각화 */
@Composable
private fun OtpCodeBoxes(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    boxCount: Int,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // 숨겨진 입력 필드 (0 크기 금지 → 최소 크기 & alpha 0)
    BasicTextField(
        value = value,
        onValueChange = { raw ->
            val filtered = raw.filter { it.isDigit() }.take(boxCount)
            onValueChange(filtered)
        },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        modifier = Modifier
            .size(1.dp)
            .alpha(0f)
            .focusRequester(focusRequester)
    ) { /* no visible text */ }

    // 박스 렌더
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboard?.show()
            },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(boxCount) { i ->
            val ch = value.getOrNull(i)?.toString().orEmpty()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = if (error != null)
                            MaterialTheme.colorScheme.error
                        else TiggleGrayLight,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ch,
                    style = AppTypography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RegisterSuccessScreen(
    uiState: RegisterAccountState,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = false,
        bottomButton = {
            TiggleButton(
                text = "확인",
                onClick = onNextClick,
                enabled = true
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(100.dp))

            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "계좌 등록 완료",
                modifier = Modifier.size(150.dp)
            )

            Spacer(Modifier.height(24.dp))

            // 안내 문구
            Text("계좌 등록 완료!", style = AppTypography.headlineLarge, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "1원 인증이 성공적으로 완료되어\n계좌가 등록되었습니다.",
                style = AppTypography.bodySmall,
                color = TiggleGrayText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // 계좌 정보 박스
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TiggleGrayLight, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "등록된 계좌",
                    style = AppTypography.bodyMedium,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("은행", style = AppTypography.bodySmall, color = TiggleGrayText)
                    Text(
                        uiState.accountHolder.bankName,
                        style = AppTypography.bodySmall,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("계좌번호", style = AppTypography.bodySmall, color = TiggleGrayText)
                    Text(
                        uiState.registerAccount.accountNum,
                        style = AppTypography.bodySmall,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(8.dp))

            }
        }
    }
}


@Preview
@Composable
fun AccountInputPreview() {
    AccountInputScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.ACCOUNT,
            registerAccount = RegisterAccount(
                accountNum = "110123456789",
                accountNumError = null
            )
        ),
        onBackClick = {},
        onAccountChange = {},
        onConfirmClick = {},
        onDismissError = {},
        title = ""
    )
}

@Preview
@Composable
fun AccountInputSuccessPreview() {
    AccountInputSuccessScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.ACCOUNTSUCCESS,
            accountHolder = AccountHolder(
                bankName = "신한은행",
                accountNo = "123-456-78910",
                userName = "최지원"
            )
        ),
        onBackClick = {},
        onStartVerification = {},
        title = ""
    )
}

@Preview
@Composable
fun PreviewOneWonTransferScreen() {
    SendCodeScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.SENDCODE,
            registerAccount = RegisterAccount(accountNum = "1234567890")
        ),
        onBackClick = {},
        onNextClick = {},
        title = ""
    )
}

@Preview(showBackground = true, name = "CertificationScreen - Success")
@Composable
fun PreviewCertificationScreen_Success() {
    CertificationScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.CERTIFICATION,
            registerAccount = RegisterAccount(
                accountNum = "110123456789",
                code = "1234",
                codeError = null,
                attemptsLeft = 3
            )
        ),
        onBackClick = {},
        onCodeChange = {},
        onResendClick = {},
        onNextClick = {},
        title = ""
    )
}

@Preview(showBackground = true, name = "RegisterAccount Success")
@Composable
fun PreviewRegisterAccountSuccessScreen() {
    RegisterSuccessScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.SUCCESS,
            accountHolder = AccountHolder(
                bankName = "신한은행",
                accountNo = "9393-9393-8394",
                userName = "최지원"
            ),
            registerAccount = RegisterAccount(
                accountNum = "939393948394",
                date = "2020-10-20 39:39"
            )
        ),
        onNextClick = {}
    )
}
