package com.kynv1.aiinsectidentifierpro

import android.annotation.SuppressLint
import android.os.Bundle
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
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
import com.kynv1.aiinsectidentifierpro.ui.screens.premium.PaywallScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.assistance.AssistanceScreen
import com.kynv1.aiinsectidentifierpro.ui.screens.assistance.AssistanceViewModel
import com.kynv1.aiinsectidentifierpro.ui.screens.settings.SettingsScreen
import com.kynv1.aiinsectidentifierpro.ui.theme.AIInsectIdentifierProTheme
import com.kynv1.aiinsectidentifierpro.ui.theme.ActiveGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.ButtonGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.Dimens
import com.kynv1.aiinsectidentifierpro.ui.theme.CardBorder
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkBackground
import com.kynv1.aiinsectidentifierpro.ui.theme.DarkForestGreen
import com.kynv1.aiinsectidentifierpro.ui.theme.LightMilkBackground
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var onboardingStore: OnboardingStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            AIInsectIdentifierProTheme {
                MainAppScreen(onboardingStore)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainAppScreen(onboardingStore: OnboardingStore) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabRoutes = setOf(Screen.Home.route, Screen.History.route)
    var stableRoute by remember { mutableStateOf<String?>(null) }
    DisposableEffect(navBackStackEntry) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                stableRoute = navBackStackEntry?.destination?.route
            }
        }
        navBackStackEntry?.lifecycle?.addObserver(observer)
        onDispose {
            navBackStackEntry?.lifecycle?.removeObserver(observer)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val isLightScreen = currentRoute == Screen.Home.route || currentRoute == Screen.History.route
            SideEffect {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = isLightScreen
                    isAppearanceLightNavigationBars = isLightScreen
                }
            }
        }
    }

    val homeViewModel: HomeViewModel = hiltViewModel()
    val scanViewModel: ScanViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val detailViewModel: DetailViewModel = hiltViewModel()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val assistanceViewModel: AssistanceViewModel = hiltViewModel()

    val startDestination = Screen.Splash.route

    Scaffold(
        containerColor = if (currentRoute in tabRoutes) LightMilkBackground else DarkBackground,
        bottomBar = {
            val showBottomBar = currentRoute in tabRoutes && stableRoute in tabRoutes
            if (showBottomBar) {
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
            assistanceViewModel = assistanceViewModel,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        )
    }
}

class CurvedBottomBarShape(
    private val cradleRadius: Dp,
    private val cornerRadius: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val cradleRadiusPx = with(density) { cradleRadius.toPx() }
            val cornerRadiusPx = with(density) { cornerRadius.toPx() }
            val width = size.width
            val height = size.height

            moveTo(0f, cornerRadiusPx)
            quadraticTo(0f, 0f, cornerRadiusPx, 0f)

            val middleX = width / 2
            val cradleStartX = middleX - cradleRadiusPx
            val cradleEndX = middleX + cradleRadiusPx

            lineTo(cradleStartX, 0f)

            cubicTo(
                x1 = cradleStartX + cradleRadiusPx / 3, y1 = 0f,
                x2 = middleX - cradleRadiusPx * 2 / 3, y2 = cradleRadiusPx,
                x3 = middleX, y3 = cradleRadiusPx
            )
            cubicTo(
                x1 = middleX + cradleRadiusPx * 2 / 3, y1 = cradleRadiusPx,
                x2 = cradleEndX - cradleRadiusPx / 3, y2 = 0f,
                x3 = cradleEndX, y3 = 0f
            )

            lineTo(width - cornerRadiusPx, 0f)
            quadraticTo(width, 0f, width, cornerRadiusPx)

            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        return Outline.Generic(path)
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
            .background(Color.Transparent)
            .navigationBarsPadding()
            .height(Dimens.dp_84),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp_64)
                .background(
                    color = Color.White,
                    shape = CurvedBottomBarShape(cradleRadius = Dimens.dp_38, cornerRadius = bottomBarCornerRadius)
                )
                .border(
                    width = Dimens.dp_1,
                    color = Color(0xFFE5EBE6),
                    shape = CurvedBottomBarShape(cradleRadius = Dimens.dp_38, cornerRadius = bottomBarCornerRadius)
                )
                .padding(horizontal = Dimens.dp_32),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
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
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = stringResource(id = R.string.home_tab_home),
                    tint = if (currentRoute == Screen.Home.route) ActiveGreen else Color.Gray,
                )
                Text(
                    text = stringResource(id = R.string.home_tab_home),
                    color = if (currentRoute == Screen.Home.route) ActiveGreen else Color.Gray,
                    fontSize = Dimens.sp_11,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(Dimens.dp_64))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
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
                .size(Dimens.dp_64)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ActiveGreen, ButtonGreen)
                    ),
                    shape = CircleShape
                )
                .border(width = Dimens.dp_4, color = LightMilkBackground, shape = CircleShape)
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
    assistanceViewModel: AssistanceViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SplashScreen(
                onboardingStore = onboardingStore,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToScan = {
                    navController.navigate(Screen.Paywall.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Onboarding.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToScan = {
                    navController.navigate(Screen.Paywall.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
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
                },
                onNavigateToAssistance = {
                    navController.navigate(Screen.Assistance.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(
            route = Screen.Paywall.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            PaywallScreen(
                homeViewModel = homeViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Paywall.route) { inclusive = true }
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
        composable(
            route = Screen.Assistance.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            AssistanceScreen(
                viewModel = assistanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) }
            )
        }
    }
}