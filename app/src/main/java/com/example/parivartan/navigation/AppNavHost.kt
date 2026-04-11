package com.example.parivartan.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
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
import com.example.parivartan.ui.auth.SignupScreen
import com.example.parivartan.ui.citizen.home.HomeScreen
import com.example.parivartan.ui.citizen.home.MyIssuesScreen
import com.example.parivartan.ui.citizen.community.CommunityScreen
import com.example.parivartan.ui.citizen.profile.ProfileScreen
import com.example.parivartan.ui.citizen.map.MapScreen
import com.example.parivartan.ui.citizen.report.ReportIssueScreen
import com.example.parivartan.ui.intro.IntroScreen
import com.example.parivartan.ui.admin.AdminLoginScreen
import com.example.parivartan.ui.staff.StaffLoginScreen
import com.example.parivartan.ui.staff.StaffIssueDetailScreen
import com.example.parivartan.ui.staff.StaffIssueListScreen
import com.example.parivartan.ui.staff.StaffMapViewScreen
import com.example.parivartan.ui.staff.StaffNotificationsScreen
import com.example.parivartan.ui.staff.StaffSettingsScreen
import com.example.parivartan.ui.staff.StaffWorkHistoryScreen
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
            BottomTab(Route.Map, "Map", TabIcon.Vector(Icons.Outlined.Map)),
            BottomTab(Route.Report, "Report", TabIcon.Vector(Icons.Outlined.AddCircle)),
            BottomTab(Route.Community, "Community", TabIcon.Vector(Icons.Outlined.People)),
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
                val isOnAuthScreen = currentRoute?.startsWith(Route.Login.route) == true || currentRoute in setOf(
                    Route.Splash.route,
                    Route.Intro.route,
                    Route.Signup.route,
                    Route.StaffLogin.route,
                    Route.AdminLogin.route,
                    Route.DepartmentLogin.route,
                )

                // If we're not on auth stack, go to the entry of auth.
                if (!isOnAuthScreen) {
                    navController.navigate(Route.Intro.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                    return@LaunchedEffect
                }

                // If we're on Splash, advance to Intro so user sees content.
                if (currentRoute == Route.Splash.route || currentRoute == null) {
                    navController.navigate(Route.Intro.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            is InitState.Authenticated -> {
                val authState = initState as InitState.Authenticated
                val isOnAuthScreen = currentRoute?.startsWith(Route.Login.route) == true || currentRoute in setOf(
                    Route.Splash.route,
                    Route.Intro.route,
                    Route.StaffLogin.route,
                    Route.AdminLogin.route,
                    Route.DepartmentLogin.route,
                    Route.Signup.route,
                    null
                )

                // Only forced navigation to Home if we are currently on an auth screen
                if (isOnAuthScreen) {
                    val role = authState.role // Get role directly from the authenticated state
                    if (role == "admin") {
                        navController.navigate(Route.AdminDashboard.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (role.lowercase().startsWith("department")) {
                        val deptId = role.substringAfter(":", "pwd") // Default to pwd
                        navController.navigate("department_dashboard/$deptId") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (role == "staff") {
                        navController.navigate(Route.StaffDashboard.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Route.Home.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
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
                    tonalElevation = 4.dp,
                    containerColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
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
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(active),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = (tab.icon as TabIcon.Vector).imageVector,
                                            contentDescription = tab.label,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        tab.label,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    )
                                },
                                alwaysShowLabel = true,
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
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                                        softWrap = false,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Visible,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
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
    val currentInitState = initState
    AppNavHost(
        navController = navController,
        initState = currentInitState,
        userFirstName = (currentInitState as? InitState.Authenticated)?.displayName ?: "Citizen",
        onLoginDemoClick = { role -> appViewModel.signInDemo(role) },
            onLogoutClick = { appViewModel.signOut() },
            onSplashFinished = { isSplashFinished = true },
            modifier = Modifier.padding(innerPadding),
            appViewModel = appViewModel
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
    onLoginDemoClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel? = null
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
                onSkip = { navController.navigate("${Route.Login.route}/citizen") },
                onGetStarted = { role ->
                    if (role == "admin") {
                        navController.navigate(Route.AdminLogin.route)
                    } else if (role == "staff") {
                        navController.navigate(Route.StaffLogin.route)
                    } else if (role == "department") {
                        navController.navigate(Route.DepartmentLogin.route)
                    } else {
                        navController.navigate("${Route.Login.route}/$role")
                    }
                },
            )
        }

        composable(Route.AdminLogin.route) {
            var isLoading by rememberSaveable { mutableStateOf(false) }
            var authError by rememberSaveable { mutableStateOf<String?>(null) }

            AdminLoginScreen(
                onLogin = { email, password ->
                    if (email.lowercase().trim() != "admin@gmail.com") {
                        authError = "You are an unauthorized user."
                        return@AdminLoginScreen
                    }
                    isLoading = true
                    authError = null
                    if (appViewModel != null) {
                        appViewModel.signInWithEmail(email, password, "admin") { error ->
                            isLoading = false
                            authError = error
                        }
                    } else {
                        onLoginDemoClick("admin")
                    }
                },
                isLoading = isLoading,
                authError = authError
            )
        }

        composable(Route.DepartmentLogin.route) {
            var isLoading by rememberSaveable { mutableStateOf(false) }
            var authError by rememberSaveable { mutableStateOf<String?>(null) }

            com.example.parivartan.ui.department.DepartmentLoginScreen(
                onLogin = { email, password, deptId ->
                    isLoading = true
                    authError = null
                    if (appViewModel != null) {
                        appViewModel.signInWithEmail(email, password, "department:$deptId") { error ->
                            isLoading = false
                            authError = error
                        }
                    } else {
                        onLoginDemoClick("department:$deptId")
                    }
                },
                onLoginDemoClick = { role -> onLoginDemoClick(role) },
                isLoading = isLoading,
                authError = authError
            )
        }

        composable(Route.StaffLogin.route) {
            var isLoading by rememberSaveable { mutableStateOf(false) }
            var authError by rememberSaveable { mutableStateOf<String?>(null) }

            StaffLoginScreen(
                onLogin = { email, password ->
                    isLoading = true
                    authError = null
                    if (appViewModel != null) {
                        appViewModel.signInWithEmail(email, password, "staff") { error ->
                            isLoading = false
                            authError = error
                        }
                    } else {
                        onLoginDemoClick("staff")
                    }
                },
                onLoginDemoClick = { role -> onLoginDemoClick(role) },
                isLoading = isLoading,
                authError = authError
            )
        }

        composable("${Route.Login.route}/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "citizen"
            var isLoading by rememberSaveable { mutableStateOf(false) }
            var authError by rememberSaveable { mutableStateOf<String?>(null) }

            LoginScreen(
                onBack = { navController.popBackStack() },
                onLogin = { email, password ->
                    if (role == "admin" && email.lowercase().trim() != "admin@gmail.com") {
                        authError = "You are an unauthorized user."
                        return@LoginScreen
                    }
                    isLoading = true
                    authError = null
                    if (appViewModel != null) {
                        appViewModel.signInWithEmail(email, password, role) { error ->
                            isLoading = false
                            authError = error
                        }
                    } else {
                        onLoginDemoClick(role)
                    }
                },
                onGoogleSignIn = { idToken ->
                    if (role == "admin") {
                        authError = "Google login not allowed for Admin."
                        return@LoginScreen
                    }
                    isLoading = true
                    authError = null
                    if (appViewModel != null) {
                        appViewModel.signInWithGoogle(idToken, role) { error ->
                            isLoading = false
                            authError = error
                        }
                    } else {
                        onLoginDemoClick(role)
                    }
                },
                isLoading = isLoading,
                authError = authError,
                onForgotPassword = {
                    // placeholder
                },
                onNavigateToSignup = { navController.navigate(Route.Signup.route) },
            )
        }

        composable(Route.Signup.route) {
            SignupScreen(
                navController = navController,
                onSignup = { fullName, email, password, onError, onSuccess ->
                    if (appViewModel != null) {
                        appViewModel.signUpWithEmail(fullName, email, password, "citizen", onError, onSuccess)
                    } else {
                        // Demo mode
                        onSuccess()
                    }
                }
            )
        }

        composable(Route.Home.route) {
            HomeScreen(
                userFirstName = userFirstName,
                onOpenProfile = { navController.navigate(Route.Profile.route) },
                onReportIssue = { navController.navigate(Route.Report.route) },
                onOpenMyComplaints = { navController.navigate(Route.MyComplaints.route) },
                onOpenMap = { navController.navigate(Route.Map.route) },
                onOpenCommunity = { navController.navigate(Route.Community.route) },
                onOpenIssueDetail = { id -> navController.navigate("${Route.IssueDetail.route}/$id") }
            )
        }

        composable(Route.Map.route) {
            MapScreen(
                onNavigateToReport = { navController.navigate(Route.Report.route) },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.IssueDetail.route}/$id") }
            )
        }
        composable(Route.Report.route) {
            ReportIssueScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.IssueDetail.route}/$id") },
                onNavigateToMyComplaints = { navController.navigate(Route.MyComplaints.route) }
            )
        }
        composable(Route.Community.route) {
            CommunityScreen(
                onNavigateToMap = { navController.navigate(Route.Map.route) },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.IssueDetail.route}/$id") }
            )
        }

        composable(Route.Profile.route) {
            val isAuthed = initState is InitState.Authenticated
            val email = "citizen@example.com" // Placeholder
            ProfileScreen(
                userFirstName = userFirstName,
                userEmail = email,
                onLogout = { if (isAuthed) onLogoutClick() else onLoginDemoClick("citizen") }
            )
        }

        composable(Route.MyComplaints.route) {
            MyIssuesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.IssueDetail.route}/$id") }
            )
        }

        composable("${Route.IssueDetail.route}/{issueId}") { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: "1"
            com.example.parivartan.ui.issue.IssueDetailScreen(
                issueId = issueId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.AdminDashboard.route) {
            com.example.parivartan.ui.admin.AdminDashboardScreen()
        }

        composable(Route.DepartmentDashboard.route) { backStackEntry ->
            val departmentId = backStackEntry.arguments?.getString("departmentId") ?: "pwd"
            com.example.parivartan.ui.department.DepartmentDashboardScreen(
                onNavigateGrievances = { /* TODO */ },
                departmentId = departmentId,
                onLogout = onLogoutClick
            )
        }

        composable(Route.StaffDashboard.route) {
            com.example.parivartan.ui.staff.StaffDashboardScreen(
                onNavigateIssues = { navController.navigate(Route.StaffIssueList.route) },
                onNavigateMap = { navController.navigate(Route.StaffMapView.route) },
                onNavigateNotifications = { navController.navigate(Route.StaffNotifications.route) },
                onNavigateSettings = { navController.navigate(Route.StaffSettings.route) },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.StaffIssueDetail.route}/$id") },
                onLogout = onLogoutClick
            )
        }

        composable(Route.StaffIssueList.route) {
            StaffIssueListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.StaffIssueDetail.route}/$id") }
            )
        }

        composable(Route.StaffMapView.route) {
            StaffMapViewScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.StaffIssueDetail.route}/$id") }
            )
        }

        composable("${Route.StaffIssueDetail.route}/{issueId}") { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: "1"
            StaffIssueDetailScreen(
                issueId = issueId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.StaffNotifications.route) {
            StaffNotificationsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueDetail = { id -> navController.navigate("${Route.StaffIssueDetail.route}/$id") }
            )
        }

        composable(Route.StaffSettings.route) {
            StaffSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateWorkHistory = { navController.navigate(Route.StaffWorkHistory.route) },
                onLogoutClick = { onLogoutClick() }
            )
        }

        composable(Route.StaffWorkHistory.route) {
            StaffWorkHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}