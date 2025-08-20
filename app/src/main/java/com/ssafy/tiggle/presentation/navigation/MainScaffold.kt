package com.ssafy.tiggle.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ssafy.tiggle.presentation.ui.piggybank.PiggyBankScreen


@Composable
fun MainScaffold() {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(bottomNavController) }
    ) { inner ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomScreen.PiggyBank.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(BottomScreen.PiggyBank.route) {
                PiggyBankScreen()
            }
            composable(BottomScreen.Donate.route) { DonateScreen() }
            composable(BottomScreen.Growth.route) { GrowthScreen() }
            composable(BottomScreen.Shorts.route) { ShortsScreen() }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        listOf(
            BottomScreen.PiggyBank,
            BottomScreen.Donate,
            BottomScreen.Growth,
            BottomScreen.Shorts
        ).forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                },
                icon = { item.Icon(selected) },
                label = { Text(item.label) }
            )
        }
    }
}

//임시
@Composable
fun DonateScreen() {
    Text("기부")
}

@Composable
fun GrowthScreen() {
    Text("성장")
}

@Composable
fun ShortsScreen() {
    Text("숏폼")
}