package com.kynv1.aiinsectidentifierpro.ui.navigation

sealed class Screen(val route: String) {
    object Scan : Screen("scan")
    object History : Screen("history")
    object Onboarding : Screen("onboarding")
    object Splash : Screen("splash")
    object Detail : Screen("detail/{insectId}") {
        fun createRoute(insectId: Long) = "detail/$insectId"
    }
}
