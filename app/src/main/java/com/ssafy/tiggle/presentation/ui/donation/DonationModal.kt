package com.ssafy.tiggle.presentation.ui.donation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.R
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.domain.entity.donation.DonationCategory
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationModal(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: DonationModalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    
    // 로딩과 성공 다이얼로그 상태
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // 성공 시 모달 닫기
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // 헤더
            Text(
                text = "기부하기",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "기부 금액과 분야를 설정하여 나의 티끌들을 전해보세요",
                fontSize = 14.sp,
                color = TiggleGrayText,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 테마 선택
            Text(
                text = "기부 테마",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DonationCategoryButton(
                    category = DonationCategory.PLANET,
                    isSelected = uiState.selectedCategory == DonationCategory.PLANET,
                    onClick = { viewModel.onCategorySelected(DonationCategory.PLANET) },
                    modifier = Modifier.weight(1f)
                )

                DonationCategoryButton(
                    category = DonationCategory.PEOPLE,
                    isSelected = uiState.selectedCategory == DonationCategory.PEOPLE,
                    onClick = { viewModel.onCategorySelected(DonationCategory.PEOPLE) },
                    modifier = Modifier.weight(1f)
                )

                DonationCategoryButton(
                    category = DonationCategory.PROSPERITY,
                    isSelected = uiState.selectedCategory == DonationCategory.PROSPERITY,
                    onClick = { viewModel.onCategorySelected(DonationCategory.PROSPERITY) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 기부 금액 입력
            Text(
                text = "기부 금액",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChanged(it) },
                placeholder = {
                    Text(
                        text = "기부 금액을 입력해주세요.",
                        color = TiggleGrayText
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                suffix = {
                    Text(
                        text = "원",
                        color = TiggleGrayText
                    )
                }
            )

            // 잔액 표시
            uiState.account?.let { account ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "천사 꿀꿀이 잔액: ${Formatter.formatCurrency(account.balance.toLong())}",
                    fontSize = 12.sp,
                    color = TiggleBlue
                )
            }

            // 오류 메시지
            uiState.errorMessage?.let { errorMessage ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 확인 버튼
            TiggleButton(
                onClick = { showLoadingDialog = true },
                text = if (uiState.isLoading) "처리 중..." else "확인",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isLoading && uiState.amount.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // 로딩 다이얼로그
    if (showLoadingDialog) {
        DonationLoadingDialog(
            onComplete = {
                showLoadingDialog = false
                viewModel.createDonation()
            }
        )
    }
    
    // 기부 성공 다이얼로그
    if (showSuccessDialog) {
        DonationSuccessDialog(
            onDismiss = { 
                showSuccessDialog = false
                viewModel.resetState() // 상태 초기화
                onSuccess()
                onDismiss()
            }
        )
    }
}

@Composable
private fun DonationCategoryButton(
    category: DonationCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else TiggleGrayLight
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, TiggleBlue)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 카테고리 아이콘
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(TiggleGrayLight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getCategoryIconRes(category)),
                    contentDescription = category.value,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 카테고리 이름
            Text(
                text = getCategoryDisplayName(category),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TiggleBlue else TiggleGrayText
            )
        }
    }
}

private fun getCategoryIconRes(category: DonationCategory): Int {
    return when (category) {
        DonationCategory.PLANET -> R.drawable.planet
        DonationCategory.PEOPLE -> R.drawable.people
        DonationCategory.PROSPERITY -> R.drawable.prosperity
    }
}

private fun getCategoryDisplayName(category: DonationCategory): String {
    return when (category) {
        DonationCategory.PLANET -> "Planet"
        DonationCategory.PEOPLE -> "People"
        DonationCategory.PROSPERITY -> "Prosperity"
    }
}

@Composable
private fun DonationLoadingDialog(
    onComplete: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1000) // 1초 대기
        onComplete()
    }
    
    AlertDialog(
        onDismissRequest = { }, // 로딩 중에는 닫을 수 없음
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    color = TiggleBlue,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "기부 처리 중...",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "잠시만 기다려주세요.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = { } // 로딩 중에는 버튼 없음
    )
}

@Composable
private fun DonationSuccessDialog(
    onDismiss: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(com.ssafy.tiggle.R.raw.firework)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Lottie 애니메이션
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(120.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "기부에 성공했습니다!",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "환경 보호에 동참해주셔서 감사합니다.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TiggleBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("확인", color = Color.White)
            }
        }
    )
}
