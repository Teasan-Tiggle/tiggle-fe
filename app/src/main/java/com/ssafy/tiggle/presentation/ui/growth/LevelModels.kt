package com.ssafy.tiggle.presentation.ui.growth

object LevelModels {
    fun assetPathFor(level: Int): String {
        val bucket = when {
            level == 0 -> "level_1.glb"
            level == 1 -> "level_2.glb"
            level == 2 -> "level_3.glb"
            else -> "level_4.glb"
        }
        return "models/$bucket"
    }

    // 필요 시 스케일/초기 카메라 등도 레벨별로 다르게
    fun scaleFor(level: Int): Float = when {
        level < 5 -> 1.0f
        level < 10 -> 1.0f
        level < 20 -> 1.1f
        else -> 1.2f
    }
}