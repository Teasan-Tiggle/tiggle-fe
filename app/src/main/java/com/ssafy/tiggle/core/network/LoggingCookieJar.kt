package com.ssafy.tiggle.core.network

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy

class LoggingCookieJar : CookieJar {
    
    private val javaNetCookieJar = JavaNetCookieJar(
        CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
    )
    
    companion object {
        private const val TAG = "CookieJar"
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        Log.d(TAG, "🍪 쿠키 저장 - URL: $url")
        cookies.forEach { cookie ->
            Log.d(TAG, "  저장: ${cookie.name}=${cookie.value} (secure=${cookie.secure})")
        }
        
        javaNetCookieJar.saveFromResponse(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = javaNetCookieJar.loadForRequest(url)
        Log.d(TAG, "📤 쿠키 로드 - URL: $url, 개수: ${cookies.size}")
        cookies.forEach { cookie ->
            Log.d(TAG, "  전송: ${cookie.name}=${cookie.value}")
        }
        return cookies
    }
}
