package com.kynv1.aiinsectidentifierpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kynv1.aiinsectidentifierpro.data.local.OnboardingStore
import com.kynv1.aiinsectidentifierpro.ui.navigation.Screen
import com.kynv1.aiinsectidentifierpro.utils.Constants
import com.kynv1.aiinsectidentifierpro.ui.screens.detail.DetailScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.detail.DetailViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.history.HistoryScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.history.HistoryViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.home.HomeScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.home.HomeViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.onboarding.OnboardingScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.onboarding.OnboardingViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.scan.ScanScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.scan.ScanViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.sound.SoundScanScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.splash.SplashScreen
import com.kynv1.aiinsectidentifierpro.ui.theme.AIInsectIdentifierProTheme
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
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

    val homeViewModel: HomeViewModel = hiltViewModel()
    val scanViewModel: ScanViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val detailViewModel: DetailViewModel = hiltViewModel()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

    val startDestination = Screen.Splash.route

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            if (currentRoute == Screen.Home.route || currentRoute == Screen.History.route) {
                MainBottomBar(currentRoute = currentRoute, navController = navController)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            onboardingStore = onboardingStore,
            homeViewModel = homeViewModel,
            scanViewModel = scanViewModel,
            historyViewModel = historyViewModel,
            detailViewModel = detailViewModel,
            onboardingViewModel = onboardingViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MainBottomBar(
    currentRoute: String?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val bottomBarCornerRadius = Dimens.dp_20
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(84.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp_64)
                .background(
                    color = DarkForestGreen,
                    shape = RoundedCornerShape(
                        topStart = bottomBarCornerRadius,
                        topEnd = bottomBarCornerRadius
                    )
                )
                .border(
                    width = Dimens.dp_1,
                    color = CardBorder,
                    shape = RoundedCornerShape(
                        topStart = bottomBarCornerRadius,
                        topEnd = bottomBarCornerRadius
                    )
                )
                .padding(horizontal = Dimens.dp_32),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        if (currentRoute != Screen.Home.route) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                    .padding(Dimens.dp_8)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home_tab_home),
                    tint = if (currentRoute == Screen.Home.route) ActiveGreen else Color.Gray,
                    modifier = Modifier.size(Dimens.dp_24)
                )
                Text(
                    text = stringResource(id = R.string.home_tab_home),
                    color = if (currentRoute == Screen.Home.route) ActiveGreen else Color.Gray,
                    fontSize = Dimens.sp_11,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(64.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        if (currentRoute != Screen.History.route) {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                    .padding(Dimens.dp_8)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(id = R.string.home_tab_collections),
                    tint = if (currentRoute == Screen.History.route) ActiveGreen else Color.Gray,
                    modifier = Modifier.size(Dimens.dp_24)
                )
                Text(
                    text = stringResource(id = R.string.home_tab_collections),
                    color = if (currentRoute == Screen.History.route) ActiveGreen else Color.Gray,
                    fontSize = Dimens.sp_11,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(y = (-16).dp)
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ActiveGreen, ButtonGreen)
                    ),
                    shape = CircleShape
                )
                .border(width = 4.dp, color = DarkBackground, shape = CircleShape)
                .clickable {
                    navController.navigate(Screen.Scan.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(Dimens.dp_12 + Dimens.dp_16)
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    onboardingStore: OnboardingStore,
    homeViewModel: HomeViewModel,
    scanViewModel: ScanViewModel,
    historyViewModel: HistoryViewModel,
    detailViewModel: DetailViewModel,
    onboardingViewModel: OnboardingViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onboardingStore = onboardingStore,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToScan = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToScan = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToScan = {
                    navController.navigate(Screen.Scan.route)
                },
                onNavigateToSoundScan = {
                    navController.navigate(Screen.SoundScan.route)
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
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
        composable(Screen.SoundScan.route) {
            SoundScanScreen(
                onBack = { navController.popBackStack() },
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
            arguments = listOf(navArgument(Constants.KEY_INSECT_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val insectId = backStackEntry.arguments?.getLong(Constants.KEY_INSECT_ID) ?: 0L
            DetailScreen(
                insectId = insectId,
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}