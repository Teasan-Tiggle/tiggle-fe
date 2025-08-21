package com.ssafy.tiggle.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.tiggle.R

sealed class BottomScreen(
    val route: String, val label: String, val activeIconRes: Int,
    val inactiveIconRes: Int
) {
    object PiggyBank : BottomScreen(
        "piggyBank",
        "저금통",
        R.drawable.icon_piggy_active,
        R.drawable.icon_piggy_inactive
    )

    object Growth :
        BottomScreen("growth", "성장", R.drawable.icon_growth_active, R.drawable.icon_growth_inactive)

    object Shorts :
        BottomScreen("shorts", "숏폼", R.drawable.icon_shorts_active, R.drawable.icon_shorts_inactive)


    @Composable
    fun Icon(selected: Boolean) {
        Icon(
            painter = painterResource(id = if (selected) activeIconRes else inactiveIconRes),
            contentDescription = label,
            modifier = Modifier.size(30.dp),
        )
    }
}

