package com.ssafy.tiggle.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.ssafy.tiggle.R

/**
 * 메인 앱의 바텀 네비게이션
 */
@Composable
fun BottomNavigation(
    navBackStack: NavBackStack,
    modifier: Modifier = Modifier
) {
    val currentRoute = navBackStack.last()

    NavigationBar(
        containerColor = Color.White,
        modifier = modifier
    ) {
        // 저금통
        NavigationBarItem(
            selected = currentRoute == BottomScreen.PiggyBank,
            onClick = {
                navBackStack.add(BottomScreen.PiggyBank)
//                {
//                    launchSingleTop = true
//                    restoreState = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
            },
            icon = { 
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == BottomScreen.PiggyBank) R.drawable.icon_piggy_active
                             else R.drawable.icon_piggy_inactive
                    ),
                    contentDescription = "저금통",
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text("저금통") }
        )
        
        // 성장
        NavigationBarItem(
            selected = currentRoute == BottomScreen.Growth,
            onClick = {
                navBackStack.add(BottomScreen.Growth)
//                {
//                    launchSingleTop = true
//                    restoreState = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
            },
            icon = { 
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == BottomScreen.Growth) R.drawable.icon_growth_active
                             else R.drawable.icon_growth_inactive
                    ),
                    contentDescription = "성장",
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text("성장") }
        )
        
        // 숏폼
        NavigationBarItem(
            selected = currentRoute == BottomScreen.Shorts,
            onClick = {
                navBackStack.add(BottomScreen.Shorts)
//                {
//                    launchSingleTop = true
//                    restoreState = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
            },
            icon = { 
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == BottomScreen.Shorts) R.drawable.icon_shorts_active
                             else R.drawable.icon_shorts_inactive
                    ),
                    contentDescription = "숏폼",
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text("숏폼") }
        )
    }
}
