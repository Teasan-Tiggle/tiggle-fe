package com.ssafy.tiggle.presentation.ui.shorts

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.VideoSize
import androidx.media3.common.C
import androidx.media3.ui.PlayerView
import java.text.NumberFormat
import java.util.Locale
import androidx.media3.ui.AspectRatioFrameLayout
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ShortsScreen(
    viewModel: ShortsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uiState.videos.size }
    )
    
    // 현재 페이지 변경 시 ViewModel 업데이트
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentVideoIndex(pagerState.currentPage)
        
        // 마지막 몇 개 동영상에 도달하면 더 많은 동영상 로드
        if (pagerState.currentPage >= uiState.videos.size - 3) {
            viewModel.loadMoreVideos()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            if (page < uiState.videos.size) {
                ShortsVideoItem(
                    video = uiState.videos[page],
                    isCurrentItem = page == pagerState.currentPage,
                    screenHeight = screenHeight,
                    onLikeClick = { viewModel.toggleLike(uiState.videos[page].id) },
                    onShareClick = { /* 공유 기능 */ },
                    onMoreClick = { /* 더보기 기능 */ }
                )
            }
        }
        
        // 로딩 상태
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
private fun ShortsVideoItem(
    video: ShortsVideo,
    isCurrentItem: Boolean,
    screenHeight: Dp,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    var showLikeSheet by remember { mutableStateOf(false) }
    val likeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showInfoBadge by remember { mutableStateOf(false) }
    
    // 현재 아이템이 될 때만 재생
    LaunchedEffect(isCurrentItem) {
        if (isCurrentItem) {
            try {
                exoPlayer.setMediaItem(MediaItem.fromUri(video.videoUrl))
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                // 화면을 꽉 채우도록 스케일링 (크롭)
                exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                isPlaying = true
                // 5초 후 ESG 안내 배지 표시
                showInfoBadge = false
                delay(8000)
                // 여전히 현재 아이템인 경우에만 표시
                if (isCurrentItem) showInfoBadge = true
            } catch (e: Exception) {
                Log.e("ShortsScreen", "동영상 재생 오류: ${e.message}")
                isPlaying = false
            }
        } else {
            exoPlayer.pause()
            isPlaying = false
            showInfoBadge = false
        }
    }
    
    // 컴포넌트가 dispose될 때 플레이어 해제
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight)
            .background(Color.Black)
    ) {
        // 비디오 플레이어
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    // 터치 이벤트 비활성화하여 스크롤 가능하도록 설정
                    isClickable = false
                    isFocusable = false
                    // 플레이어 뷰가 화면을 꽉 채우도록 크롭
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        

        // 우측 액션 버튼들
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 좋아요 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = if (video.isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = formatCount(video.likeCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // 공유 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "공유",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = formatCount(video.shareCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // 더보기 버튼
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "더보기",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // 하단 비디오 정보
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth(0.7f)
        ) {
            // ESG 기부 안내 (info 스타일 배지)
            if (showInfoBadge) Surface(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp)
                    .clickable { showLikeSheet = true },
                color = Color(0xCC111827),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "ESG 기부 안내",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "지금 나오는 영상과 연관된 ESG 테마에 기부해보세요!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 사용자 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = video.username,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { /* 팔로우 기능 */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "팔로우",
                        fontSize = 12.sp
                    )
                }
            }
            
            // 비디오 제목
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            // 해시태그
            Text(
                text = video.hashtags.joinToString(" ") { "#$it" },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    // 좋아요 모달 시트
    if (showLikeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLikeSheet = false },
            sheetState = likeSheetState,
            containerColor = Color.White
        ) {
            // 카드 래핑 없이 내용만 직접 표시
            DonationContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onSuccess = {
                    showLikeSheet = false // 모달도 함께 닫기
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
    }
}

@Composable
private fun DonationContent(
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit = {}
) {
    // 금액 선택 상태
    var selectedAmount by remember { mutableStateOf<Int?>(100) }
    var showCustomInput by remember { mutableStateOf(false) }
    var customAmountText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.KOREA) }

    Column(modifier = modifier.fillMaxWidth().padding(4.dp)) {
            // 헤더
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFEAF3FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color(0xFF1B6BFF)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "환경 보호에 동참하세요",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "이런 혁신이 더 많이 생기도록 도와주세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 금액 선택 버튼들 (4등분 균등 배치, 한 줄 고정)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AmountChip(
                    text = "100원",
                    selected = selectedAmount == 100,
                    modifier = Modifier.weight(1f)
                ) { selectedAmount = 100 }
                AmountChip(
                    text = "300원",
                    selected = selectedAmount == 300,
                    modifier = Modifier.weight(1f)
                ) { selectedAmount = 300 }
                AmountChip(
                    text = "500원",
                    selected = selectedAmount == 500,
                    modifier = Modifier.weight(1f)
                ) { selectedAmount = 500 }
                AmountChip(
                    text = "직접 입력",
                    selected = selectedAmount == null,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedAmount = null
                    showCustomInput = true
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 통계 박스들 (좌우 꽉 채우기)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    title = "12,847명",
                    subtitle = "이 영상으로 기부한 사람",
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "8,471,200원",
                    subtitle = "모인 기부금",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 기부 버튼
            val donateText = buildString {
                val amount = selectedAmount ?: customAmountText.filter { it.isDigit() }.toIntOrNull()
                val display = amount?.let { currencyFormatter.format(it) } ?: "금액"
                append(display).append("원 기부하기")
            }
            Button(
                onClick = { showLoadingDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF))
            ) {
                Text(text = donateText, color = Color.White)
            }
    }

    // 직접 입력 다이얼로그
    if (showCustomInput) {
        AlertDialog(
            onDismissRequest = { showCustomInput = false },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = customAmountText.filter { it.isDigit() }.toIntOrNull()
                    if (parsed != null && parsed > 0) {
                        selectedAmount = parsed
                        showCustomInput = false
                    }
                }) { Text("확인") }
            },
            dismissButton = { TextButton(onClick = { showCustomInput = false }) { Text("취소") } },
            title = { Text("기부 금액 입력") },
            text = {
                OutlinedTextField(
                    value = customAmountText,
                    onValueChange = { customAmountText = it.filter { ch -> ch.isDigit() } },
                    singleLine = true,
                    placeholder = { Text("금액 (원)") }
                )
            }
        )
    }
    
    // 로딩 다이얼로그
    if (showLoadingDialog) {
        DonationLoadingDialog(
            onComplete = {
                showLoadingDialog = false
                showSuccessDialog = true
            }
        )
    }
    
    // 기부 성공 다이얼로그
    if (showSuccessDialog) {
        DonationSuccessDialog(
            onDismiss = { 
                showSuccessDialog = false
                onSuccess()
            }
        )
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
                    color = Color(0xFF3B5BFF),
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "기부 처리 중...",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "잠시만 기다려주세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "환경 보호에 동참해주셔서 감사합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("확인", color = Color.White)
            }
        }
    )
}

@Composable
private fun AmountChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF)),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                color = Color(0xFF475569),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatBox(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewShortsScreen() {
    val mockUiState = ShortsUiState(
        videos = listOf(
            ShortsVideo(
                id = "1",
                videoUrl = "https://www.youtube.com/shorts/Y9hBFOavqO4?feature=share",
                title = "재미있는 금융 꿀팁!\n적금 vs 투자 어떤게 좋을까요?",
                username = "티끌금융왕",
                likeCount = 15234,
                shareCount = 892,
                viewCount = 45632,
                hashtags = listOf("금융", "투자", "꿀팁"),
                isLiked = false
            ),
            ShortsVideo(
                id = "2",
                videoUrl = "https://www.youtube.com/shorts/8o5IOilnJus?feature=share",
                title = "하루 1000원 절약법\n이것만 따라해도 한달에 3만원!",
                username = "절약마스터",
                likeCount = 8921,
                shareCount = 456,
                viewCount = 23184,
                hashtags = listOf("절약", "일상", "브이로그"),
                isLiked = true
            )
        ),
        currentVideoIndex = 0,
        isLoading = false
    )
    
    PreviewShortsContent(uiState = mockUiState)
}

@Preview(showBackground = true)
@Composable
private fun PreviewShortsVideoItem() {
    val mockVideo = ShortsVideo(
        id = "preview",
        videoUrl = "https://www.youtube.com/shorts/Y9hBFOavqO4?feature=share",
        title = "2024년 최고의 투자 전략\n초보자도 쉽게 따라할 수 있어요!",
        username = "투자고수",
        likeCount = 25678,
        shareCount = 1234,
        viewCount = 89432,
        hashtags = listOf("투자", "주식", "부자되기", "금융"),
        isLiked = false
    )
    
    ShortsVideoItemPreview(
        video = mockVideo,
        isCurrentItem = true,
        screenHeight = 800.dp
    )
}

@Composable
private fun PreviewShortsContent(uiState: ShortsUiState) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(uiState.videos) { index, video ->
                ShortsVideoItemPreview(
                    video = video,
                    isCurrentItem = index == uiState.currentVideoIndex,
                    screenHeight = screenHeight
                )
            }
        }
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ShortsVideoItemPreview(
    video: ShortsVideo,
    isCurrentItem: Boolean,
    screenHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight)
            .background(Color.Black)
    ) {
        // 비디오 플레이어 대신 썸네일 이미지로 대체 (Preview용)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VIDEO\nPLAYER",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 우측 액션 버튼들
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 좋아요 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = if (video.isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = formatCount(video.likeCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // 공유 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "공유",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = formatCount(video.shareCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // 더보기 버튼
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "더보기",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // 하단 비디오 정보
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth(0.7f)
        ) {
            // 사용자 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = video.username,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "팔로우",
                        fontSize = 12.sp
                    )
                }
            }
            
            // 비디오 제목
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            // 해시태그
            Text(
                text = video.hashtags.joinToString(" ") { "#$it" },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// 추가 프리뷰: 바텀시트 내 기부 UI 상태별
@Preview(showBackground = true)
@Composable
private fun PreviewDonationContent_Default() {
    DonationContent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewDonationContent_CustomInput() {
    var show by remember { mutableStateOf(true) }
    if (show) {
        // 다이얼로그까지 띄운 상태는 preview 제약이 있으므로
        // 금액 칩과 통계 레이아웃이 깨지지 않는지만 검증용으로만 표시
        DonationContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStatRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(title = "12,847명", subtitle = "이 영상으로 기부한 사람", modifier = Modifier.weight(1f))
        StatBox(title = "8,471,200원", subtitle = "모인 기부금", modifier = Modifier.weight(1f))
    }
}
