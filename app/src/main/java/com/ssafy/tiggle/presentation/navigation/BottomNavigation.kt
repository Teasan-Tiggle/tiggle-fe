package com.ssafy.tiggle.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ssafy.tiggle.R

/**
 * 메인 앱의 바텀 네비게이션
 */
@Composable
fun BottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        modifier = modifier
    ) {
        // 저금통
        NavigationBarItem(
            selected = currentRoute == Screen.PiggyBank.route,
            onClick = {
                navController.navigate(Screen.PiggyBank.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = { 
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.PiggyBank.route) R.drawable.icon_piggy_active 
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
            selected = currentRoute == Screen.Growth.route,
            onClick = {
                navController.navigate(Screen.Growth.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = { 
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Growth.route) R.drawable.icon_growth_active 
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
            selected = currentRoute == Screen.Shorts.route,
            onClick = {
                navController.navigate(Screen.Shorts.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = { 
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Shorts.route) R.drawable.icon_shorts_active 
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
