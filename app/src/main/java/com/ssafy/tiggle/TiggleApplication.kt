package com.ssafy.tiggle

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

/**
 * Tiggle 애플리케이션 클래스
 * Hilt를 사용하기 위한 Application 클래스
 */
@HiltAndroidApp
class TiggleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // 🔔 앱 시작 시 채널 1회 생성
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.default_notification_channel_id)
            val name = getString(R.string.notification_channel_name)
            val desc = getString(R.string.notification_channel_desc)

            val channel = NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = desc
                enableVibration(true)
            }

            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
