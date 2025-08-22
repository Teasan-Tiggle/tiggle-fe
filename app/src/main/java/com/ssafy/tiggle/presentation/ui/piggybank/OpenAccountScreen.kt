package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.tiggle.R
import com.ssafy.tiggle.presentation.ui.components.TiggleAllAgreeCheckboxItem
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleCheckboxItem
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGray
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import com.ssafy.tiggle.presentation.ui.theme.TiggleSkyBlue

@Composable
fun OpenAccountScreen(
    modifier: Modifier = Modifier,
    viewModel: OpenAccountViewModel = viewModel(),
    onBackClick: () -> Unit = {}

) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.openAccountStep) {
        OpenAccountStep.INFO -> {
            AccountInfoInputScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onTargetDonationAmountChange = viewModel::updateTargetDonationAmount,
                onPiggyBankNameChange = viewModel::updatePiggyBankName,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        OpenAccountStep.TERMS -> {
            TermsAgreementScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onTermsChange = viewModel::updateTermsAgreement,
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        OpenAccountStep.CERTIFICATION -> {
            CertificateScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        OpenAccountStep.CODE -> {
            CodeScreen(
                uiState = uiState,
                onCodeChange = viewModel::updateCode,
                onBackClick = { viewModel.goToPreviousStep() },
                onNextClick = { viewModel.goToNextStep() }
            )
        }

        OpenAccountStep.SUCCESS -> {
            SuccessScreen(
                uiState = uiState,
                onBackClick = { viewModel.goToPreviousStep() },
            )
        }
    }
}


@Composable
fun AccountInfoInputScreen(
    uiState: OpenAccountState,
    onBackClick: () -> Unit,
    onTargetDonationAmountChange: (String) -> Unit,
    onPiggyBankNameChange: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            val nextEnabled =
                uiState.amountInput.isNotBlank() &&                                   // 금액 입력됨
                        uiState.piggyBankAccount.piggyBankName.isNotBlank() &&       // 이름 입력됨
                        uiState.piggyBankAccount.amountError == null &&              // 금액 에러 없음
                        uiState.piggyBankAccount.piggyBankNameError == null          // 이름 에러 없음
            TiggleButton(
                text = "다음",
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

            Text("티끌 계좌 개설", style = AppTypography.headlineLarge, fontSize = 20.sp)

        }

        Spacer(Modifier.height(16.dp))

        //상단 설명
        Image(
            painter = painterResource(id = R.drawable.pig), contentDescription = "돼지 아이콘",
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
                text = "티끌 저금통 계좌 개설",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "티끌을 모아\n 나눔의 가치를 실천해보세요!",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(15.dp))

        // 빠른 금액 선택
        Column(modifier = Modifier.padding(20.dp)) {
            Text("기부 목표 금액", style = AppTypography.bodyLarge)
            Spacer(Modifier.height(8.dp))

            QuickAmountRow(
                selected = uiState.amountInput,
                onSelect = onTargetDonationAmountChange
            )

            Spacer(Modifier.height(8.dp))

            // 금액 직접 입력
            OutlinedTextField(
                value = uiState.amountInput,
                onValueChange = onTargetDonationAmountChange,
                placeholder = { Text("기부하고 싶은 금액을 입력하세요") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = uiState.piggyBankAccount.amountError != null,
                supportingText = {
                    val err = uiState.piggyBankAccount.amountError
                    if (err != null) Text(err, color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "목표 달성 시 한 번에 기부하거나 나누어 기부할 수 있습니다.",
                style = AppTypography.bodySmall,
                color = TiggleGrayText,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(18.dp))

            // 저금통 이름
            Text("저금통 이름", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.piggyBankAccount.piggyBankName,
                onValueChange = onPiggyBankNameChange,
                placeholder = { Text("예: 천사 꿀꿀이") },
                singleLine = true,
                isError = uiState.piggyBankAccount.piggyBankNameError != null,
                supportingText = {
                    val err = uiState.piggyBankAccount.piggyBankNameError
                    if (err != null) Text(err, color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth()
            )

        }

    }
}


//계좌 정보 - 목표 금액 설정
@Composable
private fun QuickAmountRow(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf(1_000, 5_000, 7_000, 10_000)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AmountOptionChip(
                amount = options[0],
                selected = selected == options[0].toString(),
                onClick = { onSelect(options[0].toString()) },
                modifier = Modifier.weight(1f)
            )
            AmountOptionChip(
                amount = options[1],
                selected = selected == options[1].toString(),
                onClick = { onSelect(options[1].toString()) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AmountOptionChip(
                amount = options[2],
                selected = selected == options[2].toString(),
                onClick = { onSelect(options[2].toString()) },
                modifier = Modifier.weight(1f)
            )
            AmountOptionChip(
                amount = options[3],
                selected = selected == options[3].toString(),
                onClick = { onSelect(options[3].toString()) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AmountOptionChip(
    amount: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            0.5.dp,
            if (selected) TiggleBlue
            else TiggleGray
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
            else Color.Transparent,
            contentColor = if (selected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        Text(
            text = "%,d원".format(amount),
            style = AppTypography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = TiggleGrayText
        )
    }
}


@Composable
private fun TermsAgreementScreen(
    uiState: OpenAccountState,
    onBackClick: () -> Unit,
    onTermsChange: (TermsType, Boolean) -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "동의하고 계속",
                onClick = onNextClick,
                enabled = uiState.termsData.allRequired,
                variant = if (uiState.termsData.allRequired) TiggleButtonVariant.Primary else TiggleButtonVariant.Disabled
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

            Text("티끌 계좌 개설", style = AppTypography.headlineLarge, fontSize = 20.sp)

        }

        Spacer(Modifier.height(50.dp))

        //상단 설명
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "서비스 약관 동의",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "안전한 서버 이용을 위해\n 약관에 동의해주세요.",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(80.dp))

        Column(modifier = Modifier.padding(20.dp)) {
            // 전체 동의
            TiggleAllAgreeCheckboxItem(
                text = "모든 약관에 동의합니다.",
                isChecked = uiState.termsData.allTerms,
                onCheckedChange = {
                    onTermsChange(
                        TermsType.ALL,
                        it
                    )
                },
                onDetailClick = { /* TODO: Show service terms detail */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 개별 약관들
            TiggleCheckboxItem(
                text = "서비스 이용약관 (필수)",
                isChecked = uiState.termsData.serviceTerms,
                onCheckedChange = {
                    onTermsChange(
                        TermsType.SERVICE,
                        it
                    )
                },
                onDetailClick = { /* TODO: Show service terms detail */ }

            )

            TiggleCheckboxItem(
                text = "개인정보 수집 이용 동의 (필수)",
                isChecked = uiState.termsData.privacyPolicy,
                onCheckedChange = {
                    onTermsChange(
                        TermsType.PRIVACY,
                        it
                    )
                },


                )

            TiggleCheckboxItem(
                text = "마케팅 정보 수신 동의 (선택)",
                isChecked = uiState.termsData.marketingOptional,
                onCheckedChange = {
                    onTermsChange(
                        TermsType.MARKETING,
                        it
                    )
                },

                )

            TiggleCheckboxItem(
                text = "위치정보 이용약관 (선택)",
                isChecked = uiState.termsData.financeTerms,
                onCheckedChange = { onTermsChange(TermsType.FINANCE, it) },

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

@Composable
fun CertificateScreen(
    uiState: OpenAccountState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
    ) {}

    Column(Modifier.padding(16.dp)) {
        // 상단 제목/뒤로
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {

            Text("티끌 계좌 개설", style = AppTypography.headlineLarge, fontSize = 20.sp)

        }

        Spacer(Modifier.height(100.dp))

        //상단 설명
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "본인 인증",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "금융 서비스 이용을 위해\n 본인 인증을 진행해주세요.",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(100.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, TiggleGrayLight, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .clickable { onNextClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(TiggleBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.phone),
                        contentDescription = "폰 아이콘",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 텍스트 영역
                Column {
                    Text(
                        text = "휴대폰 인증",
                        style = AppTypography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "휴대폰 SMS로 인증번호 발송",
                        style = AppTypography.bodySmall,
                        color = TiggleGrayText
                    )
                }
            }

        }
    }
}

@Composable
fun CodeScreen(
    uiState: OpenAccountState,
    onCodeChange: (String) -> Unit,          // 문자열 원본(숫자만) 전달
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onResendClick: () -> Unit = {}           // 재전송 클릭 (옵션)
) {
    // codeInput: OpenAccountState에 문자열로 가지고 있다고 가정
    val code = uiState.piggyBankAccount.certificateCode
    val codeError = uiState.piggyBankAccount.codeError
    val nextEnabled = code.toString().length == 6 && codeError == null

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

    Column(Modifier.padding(20.dp)) {
        // 상단 타이틀
        Row(
            Modifier
                .fillMaxWidth()
                .padding(60.dp, 15.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("티끌 계좌 개설", style = AppTypography.headlineLarge, fontSize = 20.sp)
        }

        Spacer(Modifier.height(36.dp))

        // 안내
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "인증번호 입력",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "휴대폰 SMS로 인증번호가 발송되었습니다.\n인증번호 6자리를 입력해주세요.",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        // 입력 카드
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, TiggleGrayLight, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("인증번호 (6자리)", style = AppTypography.bodyLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))

            OtpCodeInput(
                value = code.toString(),
                onValueChange = onCodeChange,
                error = codeError,
                boxCount = 6
            )

            Spacer(Modifier.height(12.dp))
            if (codeError != null) {
                Text(codeError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            } else {
                Text(
                    "인증번호 6자리를 입력해주세요.",
                    style = AppTypography.bodySmall,
                    color = TiggleGrayText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 남은 시도 횟수 (예: 3회 - 필요 시 state로 뺄 것)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TiggleSkyBlue)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("남은 인증 시도 횟수: 3회", style = AppTypography.bodySmall, color = Color.Black)
        }

        Spacer(Modifier.height(36.dp))

        // 재전송
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "인증번호를 받지 못하셨나요?",
                style = AppTypography.bodySmall,
                color = TiggleGrayText
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onResendClick,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TiggleBlue),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text("인증번호 재전송", color = TiggleBlue)
            }
        }
    }
}

/** 6칸 OTP 입력 UI: 내부에 숨겨진 TextField로 입력을 받고, 박스는 시각화만 담당 */
@Composable
private fun OtpCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    boxCount: Int,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    // 숨겨진 입력 필드
    BasicTextField(
        value = value,
        onValueChange = { raw ->
            val filtered = raw.filter { it.isDigit() }.take(boxCount)
            onValueChange(filtered)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .height(0.dp)
            .focusRequester(focusRequester)
    ) { /* no visible text */ }

    // 박스 렌더
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { focusRequester.requestFocus() },
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
                        color = when {
                            error != null -> MaterialTheme.colorScheme.error
                            else -> TiggleGrayLight
                        },
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
private fun SuccessScreen(
    uiState: OpenAccountState,
    onBackClick: () -> Unit,
) {
    TiggleScreenLayout(
        showBackButton = true,
        onBackClick = onBackClick,
        bottomButton = {
            TiggleButton(
                text = "확인",
                onClick = {}
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

            Text("티끌 계좌 개설", style = AppTypography.headlineLarge, fontSize = 20.sp)

        }

        Spacer(Modifier.height(100.dp))

        //상단 설명
        Image(
            painter = painterResource(id = R.drawable.happy), contentDescription = "축하 아이콘",
            Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "티끌 계좌 개설 완료!",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "티끌 저금통 계좌가 성공적으로 개설되었습니다.\n 이제 티끌을 모아 저금통을 채워보세요!",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

