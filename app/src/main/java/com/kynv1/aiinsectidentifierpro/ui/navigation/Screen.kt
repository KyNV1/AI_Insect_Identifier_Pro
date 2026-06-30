package com.kynv1.aiinsectidentifierpro.ui.navigation

sealed class Screen(val route: String) {
    object Scan : Screen("scan")
    object History : Screen("history")
    object Detail : Screen("detail/{insectId}") {
        fun createRoute(insectId: Long) = "detail/$insectId"
    }
}
