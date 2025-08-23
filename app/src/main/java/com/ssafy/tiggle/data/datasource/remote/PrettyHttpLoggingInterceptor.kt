package com.ssafy.tiggle.data.datasource.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class PrettyHttpLoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "OkHttp"
        private val UTF8 = Charset.forName("UTF-8")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        var requestBodyString: String? = null
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            requestBodyString = buffer.readString(UTF8)
        }

        // ┌─────── Request ───────
        Log.i(TAG, "┌─ Request ───────────────────────────────────────────────────────────────────")
        Log.i(TAG, "│ ${request.method} ${request.url}")

        if (request.headers.size > 0) {
            Log.v(TAG, "│ Headers:")
            request.headers.forEach { header ->
                Log.v(TAG, "│  ${header.first}: ${header.second}")
            }
        }

        if (requestBodyString != null) {
            
            Log.v(TAG, "│  ${getPrettyJson(requestBodyString)}")
        }
        Log.i(TAG, "└─────────────────────────────────────────────────────────────────────────────")
        // └───────────────────────

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "┌─ Error ─────────────────────────────────────────────────────────────────────"
            )
            Log.e(TAG, "│ HTTP FAILED: $e")
            Log.e(
                TAG,
                "└─────────────────────────────────────────────────────────────────────────────"
            )
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val mediaType: MediaType? = responseBody?.contentType()
        val isTextBody = isTextLike(mediaType)
        var bodyString: String? = null
        
        if (isTextBody && responseBody != null) {
            try {
                // 안전한 방식으로 응답 본문 읽기
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // 전체 본문 요청
                val buffer = source.buffer
                
                val charset = responseBody.contentType()?.charset(UTF8) ?: UTF8
                bodyString = buffer.clone().readString(charset)
            } catch (e: Exception) {
                Log.w(TAG, "응답 본문 읽기 실패: ${e.message}")
                bodyString = "<응답 본문 읽기 실패: ${e.javaClass.simpleName}>"
            }
        }

        val logTag = if (response.isSuccessful) Log.INFO else Log.ERROR

        // ┌─────── Response ───────
        Log.println(
            logTag,
            TAG,
            "┌─ Response ──────────────────────────────────────────────────────────────────"
        )
        Log.println(
            logTag,
            TAG,
            "│ ${response.code} ${response.message} ${response.request.url} (${tookMs}ms)"
        )

        if (response.headers.size > 0) {
            Log.v(TAG, "│ Headers:")
            response.headers.forEach { header ->
                Log.v(TAG, "│  ${header.first}: ${header.second}")
            }
        }

        if (!bodyString.isNullOrEmpty()) {
            Log.v(TAG, "│ Body:")
            Log.v(TAG, "│  ${getPrettyJson(bodyString)}")
        } else if (responseBody != null && !isTextBody) {
            Log.v(
                TAG,
                "│ Body: <binary ${mediaType ?: "unknown"}; length=${responseBody.contentLength()}> (omitted)"
            )
        }

        Log.println(
            logTag,
            TAG,
            "└─────────────────────────────────────────────────────────────────────────────"
        )
        // └────────────────────────

        // 원본 응답 그대로 반환 (buffer.clone()을 사용했으므로 안전)
        return response
    }

    /**
     * 문자열이 JSON 형태이면 예쁘게 포맷팅하고, 아니면 그대로 반환합니다.
     */
    private fun getPrettyJson(jsonString: String?): String {
        if (jsonString.isNullOrEmpty()) {
            return "Empty/Null json content"
        }
        return try {
            val trimmed = jsonString.trim()
            if (trimmed.startsWith("{")) {
                JSONObject(trimmed).toString(2)
            } else if (trimmed.startsWith("[")) {
                JSONArray(trimmed).toString(2)
            } else {
                jsonString
            }
        } catch (e: Exception) {
            jsonString // JSON 파싱 실패 시 원본 문자열 반환
        }
    }

    private fun isTextLike(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        val type = mediaType.type
        val subtype = mediaType.subtype.lowercase()
        if (type == "text") return true
        if (type == "application") {
            return subtype.contains("json") ||
                    subtype.contains("xml") ||
                    subtype.contains("x-www-form-urlencoded") ||
                    subtype.contains("javascript")
        }
        return false
    }
}
