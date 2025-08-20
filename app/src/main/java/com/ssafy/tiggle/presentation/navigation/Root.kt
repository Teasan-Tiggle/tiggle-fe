package com.ssafy.tiggle.presentation.navigation

sealed class Root(val route: String) {
    object Auth : Root("auth")
    object Main : Root("main")
}