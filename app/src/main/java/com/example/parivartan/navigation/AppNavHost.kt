package com.example.parivartan.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parivartan.R
import com.example.parivartan.app.AppViewModel
import com.example.parivartan.app.AppViewModelFactory
import com.example.parivartan.app.InitState
import com.example.parivartan.auth.AuthRepository
import com.example.parivartan.ui.auth.LoginScreen
import com.example.parivartan.ui.components.SimpleAction
import com.example.parivartan.ui.components.SimpleScreen
import com.example.parivartan.ui.home.HomeScreen
import com.example.parivartan.ui.community.CommunityScreen
import com.example.parivartan.ui.profile.ProfileScreen
import com.example.parivartan.ui.map.MapScreen
import com.example.parivartan.ui.intro.IntroScreen
import androidx.navigation.NavHostController
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf

@Composable
fun ParivartanApp(authRepository: AuthRepository) {
    val navController = rememberNavController()

    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory(authRepository))
    val initState by appViewModel.initState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        appViewModel.initialize()
    }

    val tabs = remember {
        listOf(
            BottomTab(Route.Home, "Home", TabIcon.Vector(Icons.Outlined.Home)),
            BottomTab(Route.Map, "Map", TabIcon.Vector(Icons.Outlined.Place)),
            BottomTab(Route.Report, "Report", TabIcon.Vector(Icons.Outlined.AddCircle)),
            BottomTab(Route.Community, "Community", TabIcon.Vector(Icons.Outlined.List)),
            BottomTab(Route.Profile, "Profile", TabIcon.Vector(Icons.Outlined.Person)),
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route

    var isSplashFinished by rememberSaveable { mutableStateOf(false) }

    // Perform auth-driven redirects only after the NavHost has set the graph.
    LaunchedEffect(initState, currentRoute, isSplashFinished) {
        if (!isSplashFinished && currentRoute == Route.Splash.route) return@LaunchedEffect

        when (initState) {
            InitState.Initializing -> Unit

            InitState.Unauthenticated -> {
                val isOnAuthScreen = currentRoute in setOf(
                    Route.Splash.route,
                    Route.Intro.route,
                    Route.Login.route,
                    Route.Signup.route,
                )

                // If we're not on auth stack, go to the entry of auth.
                if (!isOnAuthScreen) {
                    navController.navigate(Route.Intro.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                    return@LaunchedEffect
                }

                // If we're on Splash, advance to Intro so user sees content.
                if (currentRoute == Route.Splash.route || currentRoute == null) {
                    navController.navigate(Route.Intro.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            is InitState.Authenticated -> {
                val isOnAuthScreen = currentRoute in setOf(
                    Route.Splash.route,
                    Route.Intro.route,
                    Route.Login.route,
                    Route.Signup.route,
                    null
                )

                // Only forced navigation to Home if we are currently on an auth screen
                if (isOnAuthScreen) {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    val showBottomBar = currentRoute in setOf(
        Route.Home.route,
        Route.Map.route,
        Route.Report.route,
        Route.Community.route,
        Route.Profile.route,
    )

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            if (showBottomBar) {
                val active = Color(0xFF0D9488)
                val inactive = Color(0xFF64748B)

                NavigationBar(
                    tonalElevation = 0.dp,
                    containerColor = Color.White,
                    modifier = Modifier.height(60.dp),
                ) {
                    tabs.forEach { tab ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == tab.route.route } == true

                        // Make the center "Report" tab a floating action style button.
                        if (tab.route == Route.Report) {
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route.route) {
                                        popUpTo(Route.Splash.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .shadow(6.dp, CircleShape)
                                            .clip(CircleShape)
                                            .background(active),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = (tab.icon as TabIcon.Vector).imageVector,
                                            contentDescription = tab.label,
                                            tint = Color.White
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        tab.label,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                                alwaysShowLabel = false,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = Color.White,
                                    indicatorColor = Color.Transparent,
                                    selectedTextColor = active,
                                    unselectedTextColor = inactive,
                                )
                            )
                        } else {
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route.route) {
                                        popUpTo(Route.Splash.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    when (tab.icon) {
                                        is TabIcon.Vector -> Icon(imageVector = tab.icon.imageVector, contentDescription = tab.label)
                                        is TabIcon.Drawable -> Icon(painter = painterResource(id = tab.icon.resId), contentDescription = tab.label)
                                    }
                                },
                                label = {
                                    Text(
                                        tab.label,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Visible,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = active,
                                    selectedTextColor = active,
                                    indicatorColor = active.copy(alpha = 0.10f),
                                    unselectedIconColor = inactive,
                                    unselectedTextColor = inactive,
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            initState = initState,
            userFirstName = (initState as? InitState.Authenticated)?.displayName ?: "Citizen",
            onLoginDemoClick = { appViewModel.signInDemo() },
            onLogoutClick = { appViewModel.signOut() },
            onSplashFinished = { isSplashFinished = true },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private data class BottomTab(
    val route: Route,
    val label: String,
    val icon: TabIcon
)

private sealed interface TabIcon {
    data class Vector(val imageVector: androidx.compose.ui.graphics.vector.ImageVector) : TabIcon
    data class Drawable(val resId: Int) : TabIcon
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    initState: InitState,
    userFirstName: String,
    onLoginDemoClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val startDestination = Route.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.Splash.route) {
            com.example.parivartan.ui.splash.SplashScreen(onSplashFinished)
        }

        composable(Route.Intro.route) {
            IntroScreen(
                onSkip = { navController.navigate(Route.Login.route) },
                onGetStarted = { navController.navigate(Route.Login.route) },
            )
        }

        composable(Route.Login.route) {
            var isLoading by rememberSaveable { mutableStateOf(false) }

            LoginScreen(
                onBack = { navController.popBackStack() },
                onLogin = { _, _ ->
                    onLoginDemoClick()
                },
                isLoading = isLoading,
                onForgotPassword = {
                    // placeholder
                },
                onNavigateToSignup = { navController.navigate(Route.Signup.route) },
            )
        }

        composable(Route.Signup.route) {
            SimpleScreen(
                title = "Signup",
                actions = listOf(
                    SimpleAction("Signup (demo)") { onLoginDemoClick() },
                    SimpleAction("Back") { navController.popBackStack() },
                )
            )
        }

        composable(Route.Home.route) {
            HomeScreen(
                userFirstName = userFirstName,
                onOpenProfile = { navController.navigate(Route.Profile.route) },
                onReportIssue = { navController.navigate(Route.Report.route) },
                onOpenMyComplaints = { navController.navigate(Route.MyComplaints.route) }
            )
        }

        composable(Route.Map.route) {
            MapScreen(
                onNavigateToReport = { navController.navigate(Route.Report.route) },
                onNavigateToIssueDetail = { id -> navController.navigate(Route.IssueDetail.route) }
            )
        }
        composable(Route.Report.route) { SimpleScreen("Report Issue") }
        composable(Route.Community.route) {
            CommunityScreen(
                onNavigateToMap = { navController.navigate(Route.Map.route) },
                onNavigateToIssueDetail = { id -> navController.navigate(Route.IssueDetail.route) } // Example
            )
        }

        composable(Route.Profile.route) {
            val isAuthed = initState is InitState.Authenticated
            val email = "citizen@example.com" // Placeholder
            ProfileScreen(
                userFirstName = userFirstName,
                userEmail = email,
                onLogout = { if (isAuthed) onLogoutClick() else onLoginDemoClick() }
            )
        }

        composable(Route.MyComplaints.route) { SimpleScreen("My Complaints") }
        composable(Route.IssueDetail.route) { SimpleScreen("Issue Detail") }
    }
}
