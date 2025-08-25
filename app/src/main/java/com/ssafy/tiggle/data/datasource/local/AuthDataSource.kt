package com.ssafy.tiggle.data.datasource.local

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataSource @Inject constructor(
    private val prefs: SharedPreferences, // Hilt 모듈로부터 주입
) {

    /**
     * Token 관리 클래스
     * - 로그인 시 저장되는 accessToken, refreshToken, userId 등을 관리
     * - 앱 종료 후에도 유지되는 데이터로, 앱 재시작 시 자동 로그인 기능에 사용
     */

    // SharedPreferences에 저장된 access 토큰을 관리
    fun saveAccessToken(accessToken: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
        }
        Log.d("AuthLocalDataSource", "✅ accessToken 저장됨: $accessToken")
    }

    // 토큰을 가져오는 메소드
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // refresh 토큰은 쿠키로만 관리됨

    /**
     * 쿠키 관리 클래스
     * - 서버와의 통신에서 필요한 쿠키들을 관리
     * - 재발급 시 필요한 Cookie 헤더 문자열을 구성
     */

    // 쿠키는 CookieJar가 관리하므로 앱 로컬 저장 불필요


    // 인증 데이터를 모두 지우는 메소드
    fun clearAuthData() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
        }
    }

    // 로그인 상태 확인 메소드
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }


    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}