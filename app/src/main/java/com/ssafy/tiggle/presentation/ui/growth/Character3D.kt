package com.ssafy.tiggle.presentation.ui.growth

import android.annotation.SuppressLint
import android.content.Context
import android.view.Choreographer
import android.view.SurfaceView
import android.view.MotionEvent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import com.google.android.filament.IndirectLight
import com.google.android.filament.gltfio.ResourceLoader
import android.graphics.PixelFormat
import com.google.android.filament.EntityManager
import com.google.android.filament.LightManager
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI

/**
 * 레벨에 따라 다른 GLB를 로드하는 3D 캐릭터 컴포저블
 */
// 전역 변수
private var baseTransform: FloatArray? = null

@SuppressLint("ClickableViewAccessibility")
@Composable
fun Character3D(
    level: Int,
    modifier: Modifier = Modifier,
    enableOrbit: Boolean = true,
) {
    val context = LocalContext.current
    var modelViewer by remember { mutableStateOf<ModelViewer?>(null) }
    var currentLevel by remember { mutableStateOf(level) }

    // 렌더링 프레임 콜백
    val choreographer = remember { Choreographer.getInstance() }
    val frameCallback = remember {
        object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                modelViewer?.render(frameTimeNanos)
                choreographer.postFrameCallback(this)
            }
        }
    }

    AndroidView(
        modifier = modifier,
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
                addDirectionalLight(viewer)
                // 터치 이벤트 처리 - 직접 구현
                if (enableOrbit) {
                    enableHorizontalDragRotation(surfaceView, viewer)

                }

                // 모델 로드
                loadModelForLevel(ctx, viewer, level)

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
                modelViewer?.let { viewer ->
                    loadModelForLevel(context, viewer, level)
                }
            }
        },
        onRelease = {
            choreographer.removeFrameCallback(frameCallback)
            modelViewer = null
        }
    )
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

        // 간접광 추가
        val indirectLight = IndirectLight.Builder()
            .intensity(30000.0f)
            .build(modelViewer.engine)
        modelViewer.scene.indirectLight = indirectLight

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 터치 이벤트 처리 (직접 구현)
 */
private fun handleTouchEvent(event: MotionEvent) {
    // 기본적인 터치 처리 로직
    when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            // 터치 시작
        }
        MotionEvent.ACTION_MOVE -> {
            // 드래그 중
        }
        MotionEvent.ACTION_UP -> {
            // 터치 종료
        }
    }
}

/**
 * 레벨에 맞는 모델 로드 (안전한 방식)
 */
private fun loadModelForLevel(context: Context, modelViewer: ModelViewer, level: Int) {
    try {
        val assetPath = LevelModels.assetPathFor(level)
        val buffer = readAssetFile(context, assetPath) ?: return

        modelViewer.loadModelGlb(buffer)
        modelViewer.asset?.let { asset ->
            ResourceLoader(modelViewer.engine).loadResources(asset)
        }
        // 🔥 리소스 로더 호출 (텍스처/머티리얼 GPU 업로드)
        modelViewer.asset?.let { asset ->
            ResourceLoader(modelViewer.engine).loadResources(asset)
        }

        // 모델 크기 정규화
        modelViewer.transformToUnitCube()

        // root 트랜스폼 저장
        modelViewer.asset?.let { asset ->
            val tm = modelViewer.engine.transformManager
            val ti = tm.getInstance(asset.root)
            if (ti != 0) {
                baseTransform = FloatArray(16)
                tm.getTransform(ti, baseTransform)
            }
        }

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
                sum += a[i + k*4] * b[k + j*4]
            }
            out[i + j*4] = sum
        }
    }
    return out
}
private fun addDirectionalLight(modelViewer: ModelViewer) {
    val engine = modelViewer.engine

    // (선택) 배경 클리어 색 — 완전 투명 원하면 clear=false로
    val clear = modelViewer.renderer.clearOptions
    clear.clear = true
    clear.clearColor = floatArrayOf(0.94f, 0.98f, 1.0f, 1.0f)
    modelViewer.renderer.clearOptions = clear

    // Skybox는 그대로 null 유지
    // 간접광은 약하게만 (IBL 없는 상태)
    modelViewer.scene.indirectLight = com.google.android.filament.IndirectLight.Builder()
        .intensity(20_000.0f)
        .build(engine)

    // ✅ 방향광(햇빛) 추가
    val sun = EntityManager.get().create()
    LightManager.Builder(LightManager.Type.DIRECTIONAL)
        .color(1.0f, 1.0f, 1.0f)
        .intensity(80_000.0f)           // 밝기 (필요시 조절)
        .direction(0.2f, -1.0f, -0.3f)  // 위에서 비스듬히
        .castShadows(true)
        .build(engine, sun)
    modelViewer.scene.addEntity(sun)
}
