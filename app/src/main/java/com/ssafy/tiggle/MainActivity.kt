package com.ssafy.tiggle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.tiggle.presentation.navigation.NavigationGraph
import com.ssafy.tiggle.presentation.ui.theme.TiggleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 메인 액티비티
 * Hilt를 사용하여 의존성 주입을 받습니다
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 1. 딥링크 Intent를 담을 StateFlow 생성
    private val deepLinkIntent = MutableStateFlow<Intent?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 2. 앱 시작 시 초기 Intent가 딥링크를 포함하는지 확인
        if (intent?.data != null) {
            deepLinkIntent.value = intent
        }

        setContent {
            TiggleTheme {
                // ✅ Android 13+ 알림 권한 1회 요청
                RequestPostNotificationsPermissionOnce()
                // ⬇️ 기존 네비게이션
                val intentState by deepLinkIntent.collectAsStateWithLifecycle()
                NavigationGraph(
                    intent = intentState,
                    // 6. 딥링크 처리가 완료되면 StateFlow를 null로 비워주는 콜백 전달
                    onDeepLinkHandled = { deepLinkIntent.value = null }
                )
            }
        }
    }

    // 4. 앱이 백그라운드에 있을 때 새 Intent를 받는 부분
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 5. 새로 받은 Intent로 StateFlow를 업데이트 -> Compose에 변경 사항 알림
        if (intent.data != null) {
            deepLinkIntent.value = intent
        }
    }
}

/** Android 13+에서 POST_NOTIFICATIONS 권한 1회 요청 */
@Composable
private fun RequestPostNotificationsPermissionOnce() {
    val ctx = LocalContext.current

    // 현재 상태 로그로 확인
    LaunchedEffect(Unit) {
        android.util.Log.d(
            "NotifPerm",
            "SDK=${android.os.Build.VERSION.SDK_INT}, areEnabled=${
                NotificationManagerCompat.from(
                    ctx
                ).areNotificationsEnabled()
            }"
        )
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        // Android 12 이하: 런타임 권한 없음 (설정에서만 on/off)
        return
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        android.util.Log.d("NotifPerm", "request result granted=$granted")
        if (!granted) {
            // 거부된 상태면 설정으로 유도(선택)
            // open app notification settings
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, ctx.packageName)
            }
            ctx.startActivity(intent)
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("NotifPerm", "already granted=$granted")

        // 미허용이면 팝업 띄움
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}