package com.ssafy.tiggle.core.fcm

import android.content.Context
import android.provider.Settings
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.tiggle.domain.repository.FcmRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FcmTokenUploader"


/**
 * 앱 ↔ 서버 사이에서 FCM 토큰을 업로드하는 "작은 유틸 서비스" 클래스.
 *
 * 언제 쓰나?
 * - 로그인 직후(서버가 유저 인증된 상태에서 토큰을 묶어 저장해야 하므로)
 * - 혹은 onNewToken() 으로 토큰이 갱신되었을 때(서비스에서 직접 서버로 업로드)
 */
@Singleton
class FcmTokenUploader @Inject constructor(
    private val repo: FcmRepository,
    @ApplicationContext private val context: Context
) {
    /**
     * 단말 식별자(디바이스 ID)를 구함.
     * - 1순위: Firebase Installation ID (FIID) : Firebase가 제공하는 안정적 설치 식별자
     * - 실패/예외 시: ANDROID_ID (OS가 제공하는 단말 고유 식별자)
     */
    private suspend fun getDeviceId(): String {
        return try {
            FirebaseInstallations.getInstance().id.await()
        } catch (_: Exception) {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                ?: "unknown"
        }
    }

    /**
     * 현재 단말의 FCM 토큰을 가져와 서버에 업로드.
     *
     * - suspend fun: 호출 측(viewModel 등)에서 코루틴으로 쉽게 호출 가능.
     * - 실패 시 Result.failure 로 전달해서 UI에서 토스트/스낵바 처리 용이.
     */
    suspend fun upload(): Result<Unit> {
        val token = FirebaseMessaging.getInstance().token.await()
        val deviceId = getDeviceId()
        return repo.registerToken(token)
    }
}
