package com.ssafy.tiggle.presentation.ui.growth

import android.content.ContentValues.TAG
import android.util.Log

object LevelModels {
     fun assetPathFor(level: Int): String {
         val bucket = when {
             level ==0 -> "level_5.glb"
             level ==1 -> "level_5.glb"
             level ==2 -> "level_5.glb"
             else -> "level_5.glb"
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