package com.ssafy.tiggle.core.fcm

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.tiggle.MainActivity
import com.ssafy.tiggle.R
import com.ssafy.tiggle.domain.repository.FcmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TiggleMessageService"

/**
 * FirebaseMessagingService 구현체.
 *
 * 역할 요약
 * 1) onNewToken(token): 단말의 FCM 토큰이 갱신될 때 호출 → 서버에 새 토큰 업로드
 * 2) onMessageReceived(msg): 포그라운드(또는 data-only) 수신 시 직접 알림 표시
 *
 * 주의
 * - 앱이 "백그라운드"이고 메시지 payload에 "notification" 키가 있으면,
 *   시스템이 알림을 자동으로 표시하고 onMessageReceived()는 호출되지 않는게 정상.
 * - 앱이 포그라운드이거나 "data-only" payload면 onMessageReceived()가 호출됨.
 */

@AndroidEntryPoint // Hilt로 Repository 등 주입 받을 수 있게 함
class TiggleMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmRepository: FcmRepository

    /**
     * 새로운 FCM 토큰이 발급/갱신되었을 때(앱 설치 직후, 데이터 초기화, 토큰 만료 등)
     * -> 서버에 업로드해서 최신 토큰으로 푸시를 받을 수 있도록 함.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceId = Settings.Secure.getString(
                    applicationContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) ?: "unknown"

                //서버로 토큰 전송
                fcmRepository.registerToken(token)
            } catch (e: Exception) {
                Log.e("TiggleFCM", "FCM 토큰 업로드 실패", e)
            }
        }
    }

    /**
     * 앱이 "포그라운드"일 때, 또는 "data-only" 메시지를 받았을 때 호출됨.
     *
     * ※ 백그라운드 + notification payload 조합이면 시스템이 자동으로 알림을 띄우며
     *    이 메서드는 호출되지 않는 게 정상
     *
     * 권한
     * - Android 13+ 에서는 POST_NOTIFICATIONS 권한이 필요.
     *   권한이 없으면 알림을 띄우지 않고 리턴.
     */

    override fun onMessageReceived(message: RemoteMessage) {
        // notification payload + data payload 모두 수동 처리
        val title = message.data["title"]
            ?: message.notification?.title
            ?: "티끌"
        val body = message.data["body"]
            ?: message.notification?.body
            ?: "새 알림이 도착했어요."

        Log.d(TAG, "onMessageReceived: data=${message.data}, notif=${message.notification}")

        // Android 13+ 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ 항상 직접 알림 생성
        val channelId = getString(R.string.default_notification_channel_id)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis() % 100000).toInt(), notification)
    }

}
