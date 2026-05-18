package com.airetv.app.navigation

sealed class Screen(val route: String) {
    data object Browse : Screen("browse")
    data object Player : Screen("player/{channelId}") {
        fun createRoute(channelId: String) = "player/$channelId"
    }
}
