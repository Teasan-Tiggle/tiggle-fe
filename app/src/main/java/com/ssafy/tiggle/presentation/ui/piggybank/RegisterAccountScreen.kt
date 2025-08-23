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
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.tiggle.R
import com.ssafy.tiggle.domain.entity.account.RegisterAccount
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
    viewModel: RegisterAccountViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.registerAccountStep) {
        RegisterAccountStep.ACCOUNT -> {
            AccountInputScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onAccountChange = viewModel::updateAccountNum,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        RegisterAccountStep.ACCOUNTSUCCESS -> {
            AccountInputSuccessScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onNextClick = { viewModel.goToNextStep() }
            )

        }

        RegisterAccountStep.SENDCODE -> {
            SendCodeScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        RegisterAccountStep.CERTIFICATION -> {
            CertificationScreen(
                uiState = uiState,
                onCodeChange = viewModel::updateCode,
                onBackClick = { viewModel.goToPreviousStep() },
                onResendClick = { viewModel.goToPreviousStep() },
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        RegisterAccountStep.SUCCESS -> {
            RegisterSuccessScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

    }
}

@Composable
fun AccountInputScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onAccountChange: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            val nextEnabled =
                uiState.registerAccount.accountNum.isNotBlank() &&
                        uiState.registerAccount.accountNumError == null
            TiggleButton(
                text = "확인",
                onClick = onNextClick,
                enabled = nextEnabled,
            )
        }
    ) {}

    Column(Modifier.padding(16.dp)) {
        // 상단 제목/뒤로
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {

            Text("계좌 등록", style = AppTypography.headlineLarge, fontSize = 20.sp)

        }

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

@Composable
fun AccountInputSuccessScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            val nextEnabled =
                uiState.registerAccount.accountNum.isNotBlank() &&
                        uiState.registerAccount.accountNumError == null
            TiggleButton(
                text = "1원 인증 시작",
                onClick = onNextClick,
                enabled = nextEnabled,
            )
        }
    ) {}

    Column(Modifier.padding(16.dp)) {
        // 상단 제목/뒤로
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {

            Text("계좌 등록", style = AppTypography.headlineLarge, fontSize = 20.sp)

        }

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

        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = uiState.registerAccount.owner,
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
                    "신한은행 ${uiState.registerAccount.accountNum}",
                    style = AppTypography.bodyMedium, fontSize = 20.sp, textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(100.dp))
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

@Composable
fun SendCodeScreen(
    uiState: RegisterAccountState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "인증번호 입력하기",
                onClick = onNextClick,
                enabled = true
            )
        }
    ) {}
    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // 상단 타이틀
            Text("계좌 등록", style = AppTypography.headlineLarge, fontSize = 20.sp)
        }

        Spacer(Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.check),
            contentDescription = "송금 완료 아이콘",
            modifier = Modifier.size(170.dp)
        )

        Spacer(Modifier.height(16.dp))

        // 완료 안내
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

        // 계좌 정보 박스
        Column(Modifier.padding(20.dp)) {
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

            // 입금자명 확인 방법
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

            // 입금 확인 안내 박스
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
                    painter = painterResource(id = R.drawable.money), // 동전 or 금색 아이콘
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
) {
    val code = uiState.registerAccount.code.toString() ?: ""
    val error = uiState.registerAccount.codeError
    val attemptsLeft = uiState.registerAccount.attemptsLeft ?: 3

    val nextEnabled = code.length == 4 && error == null

    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "인증 완료",
                onClick = onNextClick,
                enabled = nextEnabled
            )
        }
    ) {}

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 타이틀
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("계좌 등록", style = AppTypography.headlineLarge, fontSize = 20.sp)
        }

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
                Text("인증번호 (4자리)", style = AppTypography.bodyLarge, textAlign = TextAlign.Center)
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
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text("1원 재송금", color = TiggleBlue)
            }
        }

        Spacer(Modifier.height(60.dp))

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
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "확인",
                onClick = onNextClick,
                enabled = true
            )
        }
    ) {}

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 타이틀
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("계좌 등록", style = AppTypography.headlineLarge, fontSize = 20.sp)
        }

        Spacer(Modifier.height(40.dp))

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
            Text("등록된 계좌", style = AppTypography.bodyMedium, fontSize = 16.sp, color = Color.Black)

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("은행", style = AppTypography.bodySmall, color = TiggleGrayText)
                Text(uiState.registerAccount.bankName, style = AppTypography.bodySmall, color = Color.Black)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("계좌번호", style = AppTypography.bodySmall, color = TiggleGrayText)
                Text(uiState.registerAccount.accountNum, style = AppTypography.bodySmall, color = Color.Black)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("등록일시", style = AppTypography.bodySmall, color = TiggleGrayText)
                Text(uiState.registerAccount.date, style = AppTypography.bodySmall, color = Color.Black)
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
        onNextClick = {}
    )
}

@Preview
@Composable
fun AccountInputSuccessPreview() {
    AccountInputSuccessScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.ACCOUNTSUCCESS,
            registerAccount = RegisterAccount(
                accountNum = "110123456789",
                owner = "최지원",
                accountNumError = null
            )
        ),
        onBackClick = {},
        onNextClick = {}
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
        onNextClick = {}
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
                owner = "최지원",
                code = 1234,
                codeError = null,
                attemptsLeft = 3
            )
        ),
        onBackClick = {},
        onCodeChange = {},
        onResendClick = {},
        onNextClick = {}
    )
}

@Preview(showBackground = true, name = "RegisterAccount Success")
@Composable
fun PreviewRegisterAccountSuccessScreen() {
    RegisterSuccessScreen(
        uiState = RegisterAccountState(
            registerAccountStep = RegisterAccountStep.SUCCESS,
            registerAccount = RegisterAccount(
                bankName = "신한은행",
                accountNum = "939393948394",
                date = "2020-10-20 39:39"
            )
        ),
        onBackClick = {},
        onNextClick = {}
    )
}
