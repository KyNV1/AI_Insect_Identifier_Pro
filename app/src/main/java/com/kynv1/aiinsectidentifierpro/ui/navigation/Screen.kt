package com.kynv1.aiinsectidentifierpro.ui.navigation

import com.kynv1.aiinsectidentifierpro.utils.Constants

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Scan : Screen("scan")
    object History : Screen("history")
    object Onboarding : Screen("onboarding")
    object Splash : Screen("splash")
    object SoundScan : Screen("sound_scan")
    object Paywall : Screen("paywall")
    object Assistance : Screen("assistance")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{${Constants.KEY_INSECT_ID}}") {
        fun createRoute(insectId: Long) = "detail/$insectId"
    }
}
