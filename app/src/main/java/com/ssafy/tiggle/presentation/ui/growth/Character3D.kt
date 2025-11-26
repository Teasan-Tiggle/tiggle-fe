package com.ssafy.tiggle.presentation.ui.growth

import android.annotation.SuppressLint
import android.content.Context
import android.view.Choreographer
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.EntityManager
import com.google.android.filament.LightManager
import com.google.android.filament.gltfio.Animator
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import com.ssafy.tiggle.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI

/**
 * 레벨에 따라 다른 GLB를 로드하는 3D 캐릭터 컴포저블 (이미지 플레이스홀더 사용)
 */
// 전역 변수
private var baseTransform: FloatArray? = null

// 애니메이션 자동재생용 상태
private var animIndex: Int = -1
private var animDurationSec: Float = 0f
private var animStartNanos: Long = -1L

// 리소스 로더 (비동기 로딩용)
private var resourceLoader: ResourceLoader? = null

// 로딩 완료 콜백
private var onResourcesLoaded: (() -> Unit)? = null

// 로딩 완료 처리 플래그 (중복 실행 방지)
private var loadingCompleteHandled: Boolean = false

@SuppressLint("ClickableViewAccessibility")
@Composable
fun Character3D(
    level: Int,
    modifier: Modifier = Modifier,
    enableOrbit: Boolean = true,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var modelViewer by remember { mutableStateOf<ModelViewer?>(null) }
    var currentLevel by remember { mutableStateOf(level) }
    var isModelLoaded by remember { mutableStateOf(false) }
    var shouldStartLoading by remember { mutableStateOf(false) }

    // 페이드 애니메이션
    val alpha by animateFloatAsState(
        targetValue = if (isModelLoaded) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "model_fade"
    )

    // 컴포지션 완료 후 로딩 시작
    LaunchedEffect(Unit) {
        delay(50) // UI 렌더링 후 모델 로딩 시작
        shouldStartLoading = true
    }

    // 레벨 변경 시 로딩 상태 리셋
    LaunchedEffect(level) {
        if (currentLevel != level) {
            isModelLoaded = false
            currentLevel = level
        }
    }

    Box(modifier = modifier) {

        // 3D 모델 뷰
        if (shouldStartLoading) {
            // 렌더링 프레임 콜백
            val choreographer = remember { Choreographer.getInstance() }
            val frameCallback = remember {
                object : Choreographer.FrameCallback {
                    override fun doFrame(frameTimeNanos: Long) {
                        // ── 리소스 로더가 있으면 비동기 업데이트 ──
                        resourceLoader?.let { loader ->
                            loader.asyncUpdateLoad()

                            // 로딩 완료 체크 (진행률 1.0 = 100%)
                            val progress = loader.asyncGetLoadProgress()
                            if (progress >= 1.0f && onResourcesLoaded != null && !loadingCompleteHandled) {
                                loadingCompleteHandled = true
                                val callback = onResourcesLoaded
                                onResourcesLoaded = null

                                // 코루틴으로 깔끔하게 처리: 250ms 대기 후 메인 스레드에서 콜백 실행
                                coroutineScope.launch {
                                    delay(250) // Material 로딩 완료 후 추가 대기
                                    withContext(Dispatchers.Main) {
                                        callback?.invoke()
                                    }
                                }
                            }
                        }

                        // ── 애니메이션이 있으면 시간계산해서 적용 ──
                        modelViewer?.let { mv ->
                            val animator = mv.animator
                            if (animIndex >= 0 && animator != null && animator.animationCount > 0) {
                                if (animStartNanos < 0L) animStartNanos = frameTimeNanos
                                val tSec = ((frameTimeNanos - animStartNanos) / 1_000_000_000.0f)
                                // duration이 0일 가능성 방지
                                val dur = if (animDurationSec > 1e-4f) animDurationSec else 1f
                                val loopTime = (tSec % dur)
                                animator.applyAnimation(animIndex, loopTime)
                                animator.updateBoneMatrices()
                            }
                        }
                        modelViewer?.render(frameTimeNanos)
                        choreographer.postFrameCallback(this)
                    }
                }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                factory = { ctx ->
                    val surfaceView = SurfaceView(ctx)

                    // SurfaceView 투명 설정
                    surfaceView.holder.setFormat(android.graphics.PixelFormat.TRANSLUCENT)
                    surfaceView.setZOrderOnTop(false) // 다른 뷰 뒤에 배치

                    try {
                        // Filament 초기화
                        Utils.init()

                        // ModelViewer 생성 (기본 생성자 사용)
                        val viewer = ModelViewer(surfaceView)
                        modelViewer = viewer

                        // 투명 배경 설정
                        setupTransparentBackground(viewer)

                        // 개선된 조명 설정
                        setupFrontLight(viewer)

                        // 터치 이벤트 처리 - 직접 구현
                        if (enableOrbit) {
                            enableHorizontalDragRotation(surfaceView, viewer)
                        }

                        // 비동기로 모델 로드
                        loadModelAsync(ctx, viewer, level, coroutineScope) {
                            isModelLoaded = true
                        }

                        // 렌더링 시작
                        choreographer.postFrameCallback(frameCallback)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    surfaceView
                },
                update = { _ ->
                    // 레벨 변경 시 모델 재로드
                    if (currentLevel != level) {
                        currentLevel = level
                        isModelLoaded = false
                        loadingCompleteHandled = false  // 플래그 리셋
                        modelViewer?.let { viewer ->
                            loadModelAsync(context, viewer, level, coroutineScope) {
                                isModelLoaded = true
                            }
                        }
                    }
                },
                onRelease = {
                    choreographer.removeFrameCallback(frameCallback)
                    // 리소스 로더 정리
                    resourceLoader?.destroy()
                    resourceLoader = null
                    onResourcesLoaded = null
                    loadingCompleteHandled = false
                    modelViewer = null
                    isModelLoaded = false
                }
            )
        }
    }
}

/**
 * 비동기 모델 로딩 (코루틴 사용, 콜백으로 완료 알림)
 */
private fun loadModelAsync(
    context: Context,
    modelViewer: ModelViewer,
    level: Int,
    scope: CoroutineScope,
    onLoadComplete: () -> Unit
) {
    scope.launch(Dispatchers.IO) {
        try {
            val assetPath = LevelModels.assetPathFor(level)
            val buffer = readAssetFile(context, assetPath)

            if (buffer != null) {
                // 메인 스레드에서 Filament 작업 수행
                withContext(Dispatchers.Main) {
                    try {
                        modelViewer.loadModelGlb(buffer)

                        // 리소스 로더 생성 및 비동기 로딩 시작
                        modelViewer.asset?.let { asset ->
                            // 기존 리소스 로더 정리
                            resourceLoader?.destroy()

                            // 새 리소스 로더 생성
                            resourceLoader = ResourceLoader(modelViewer.engine).apply {
                                // 비동기 로딩 시작
                                asyncBeginLoad(asset)
                            }
                        }

                        val asset = modelViewer.asset
                        val animator = modelViewer.animator
                        val animationCount = animator?.animationCount ?: 0
                        val hasAnimation = animationCount > 0

                        if (hasAnimation) {
                            setupAnimatedModel(modelViewer, asset!!, animator!!)
                        } else {
                            // 기존처럼 모델 크기 정규화
                            modelViewer.transformToUnitCube()
                        }

                        // root 트랜스폼 저장
                        saveBaseTransform(modelViewer)

                        // 로딩 완료 콜백 저장 (프레임 콜백에서 호출됨)
                        onResourcesLoaded = onLoadComplete
                        loadingCompleteHandled = false  // 플래그 리셋

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun setupAnimatedModel(modelViewer: ModelViewer, asset: FilamentAsset, animator: Animator) {
    // 애니메이션이 있으니까 크기 줄이기
    val tm = modelViewer.engine.transformManager
    val root = asset.root
    val rootInst = tm.getInstance(root)

    val scale = 0.28f
    val translateY = -0.20f

    // column-major 4x4, translation은 [12],[13],[14] 슬롯
    val trs = floatArrayOf(
        scale, 0f, 0f, 0f,
        0f, scale, 0f, 0f,
        0f, 0f, scale, 0f,
        0f, translateY, 0f, 1f
    )
    tm.setTransform(rootInst, trs)

    // 공통 프레임 루프에서 돌리도록 애니메이션 정보만 세팅
    animIndex = 0
    animDurationSec = animator.getAnimationDuration(0)
    animStartNanos = -1L  // 다음 프레임에서 시작점 리셋
}

private fun saveBaseTransform(modelViewer: ModelViewer) {
    modelViewer.asset?.let { asset ->
        val tm = modelViewer.engine.transformManager
        val ti = tm.getInstance(asset.root)
        if (ti != 0) {
            baseTransform = FloatArray(16)
            tm.getTransform(ti, baseTransform)
        }
    }
}

private fun setupFrontLight(modelViewer: ModelViewer) {
    // 배경은 투명/스카이박스 없음
    modelViewer.scene.skybox = null

    // ✅ Indirect Light 추가 (ambient lighting 제공)
    val indirectLight = com.google.android.filament.IndirectLight.Builder()
        .intensity(30000f)  // ambient light 밝기
        .irradiance(1, floatArrayOf(
            // 1 band SH (3개 float = 1 x float3)
            0.8f, 0.8f, 0.9f  // 밝은 회백색 ambient
        ))
        .build(modelViewer.engine)
    modelViewer.scene.indirectLight = indirectLight

    // 정면에서 살짝 내려 비추는 한 개의 방향광
    val key = EntityManager.get().create()
    LightManager.Builder(LightManager.Type.DIRECTIONAL)
        .color(1.0f, 1.0f, 1.0f)   // 순백색 라이트
        .intensity(450_000f)       // 밝기
        .direction(0f, -0.2f, -1f) // 화면 정면(–Z)에서 약간 아래로
        .castShadows(false)        // 그림자 비활성화
        .build(modelViewer.engine, key)
    modelViewer.scene.addEntity(key)
}

/**
 * 투명 배경 설정
 */
private fun setupTransparentBackground(modelViewer: ModelViewer) {
    try {
        modelViewer.scene.skybox = null
        modelViewer.view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT

        val clearOptions = modelViewer.renderer.clearOptions
        clearOptions.clear = true
        // 투명 대신 흰색으로 설정
        clearOptions.clearColor = floatArrayOf(
            0.94f,  // 거의 흰색에 가까운
            0.98f,
            1.0f,
            1.0f
        )
        modelViewer.renderer.clearOptions = clearOptions

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * assets에서 GLB 파일 읽기
 */
private fun readAssetFile(context: Context, path: String): ByteBuffer? {
    return try {
        context.assets.open(path).use { inputStream ->
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)

            ByteBuffer.allocateDirect(bytes.size).apply {
                order(ByteOrder.nativeOrder())
                put(bytes)
                rewind()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private var lastTouchX = 0f
private var accumulatedRotation = 0f // 누적 회전 각도

@SuppressLint("ClickableViewAccessibility")
private fun enableHorizontalDragRotation(surfaceView: SurfaceView, modelViewer: ModelViewer) {
    val engine = modelViewer.engine
    val tm = engine.transformManager

    surfaceView.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                lastTouchX = event.x

                val deltaAngle = dx * 0.5f
                accumulatedRotation += deltaAngle

                modelViewer.asset?.let { asset ->
                    val root = asset.root
                    val ti = tm.getInstance(root)
                    if (ti != 0 && baseTransform != null) {
                        // 드래그 MOVE에서
                        val radians = (accumulatedRotation * PI / 180f).toFloat()

                        // Y축 회전 행렬을 floatArray로 직접 만들기
                        val cos = kotlin.math.cos(radians)
                        val sin = kotlin.math.sin(radians)
                        val rotMat = floatArrayOf(
                            cos, 0f, -sin, 0f,
                            0f, 1f, 0f, 0f,
                            sin, 0f, cos, 0f,
                            0f, 0f, 0f, 1f
                        )

                        // baseTransform × rotMat
                        val finalMat = multiplyMat4(baseTransform!!, rotMat)
                        tm.setTransform(ti, finalMat)
                    }
                }
            }
        }
        true
    }
}

// 행렬 곱 (4x4) : a * b
private fun multiplyMat4(a: FloatArray, b: FloatArray): FloatArray {
    val out = FloatArray(16)
    for (i in 0..3) {
        for (j in 0..3) {
            var sum = 0f
            for (k in 0..3) {
                sum += a[i + k * 4] * b[k + j * 4]
            }
            out[i + j * 4] = sum
        }
    }
    return out
}
