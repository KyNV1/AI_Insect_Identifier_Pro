package com.kynv1.aiinsectidentifierpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kynv1.aiinsectidentifierpro.data.local.OnboardingStore
import com.kynv1.aiinsectidentifierpro.ui.navigation.Screen
import com.kynv1.aiinsectidentifierpro.ui.screens.detail.DetailScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.detail.DetailViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.history.HistoryScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.history.HistoryViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.onboarding.OnboardingScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.onboarding.OnboardingViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.scan.ScanScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.scan.ScanViewModel
import com.kynv1.aiinsectidentifierpro.ui.theme.AIInsectIdentifierProTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var onboardingStore: OnboardingStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIInsectIdentifierProTheme {
                MainAppScreen(onboardingStore)
            }
        }
    }
}

@Composable
fun MainAppScreen(onboardingStore: OnboardingStore) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Khởi tạo ViewModels thông qua Hilt DI
    val scanViewModel: ScanViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val detailViewModel: DetailViewModel = hiltViewModel()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

    val startDestination = if (onboardingStore.isOnboardingCompleted()) {
        Screen.Scan.route
    } else {
        Screen.Onboarding.route
    }

    Scaffold(
        bottomBar = {
            // Chỉ hiển thị Bottom Navigation ở màn hình Scan và History
            if (currentRoute == Screen.Scan.route || currentRoute == Screen.History.route) {
                NavigationBar(
                    containerColor = Color(0xFF0F1E16), // Dark green theme
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Nhận diện"
                            )
                        },
                        label = { Text("Nhận diện") },
                        selected = currentRoute == Screen.Scan.route,
                        onClick = {
                            navController.navigate(Screen.Scan.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF1B3227)
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Lịch sử"
                            )
                        },
                        label = { Text("Lịch sử") },
                        selected = currentRoute == Screen.History.route,
                        onClick = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF1B3227)
                        )
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onNavigateToScan = {
                        navController.navigate(Screen.Scan.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Scan.route) {
                ScanScreen(
                    viewModel = scanViewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("insectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val insectId = backStackEntry.arguments?.getLong("insectId") ?: 0L
                DetailScreen(
                    insectId = insectId,
                    viewModel = detailViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}