package com.example.parivartan.navigation

/**
 * Mirrors the React Navigation structure:
 * - Root stack decides between Auth vs Main (tabs)
 * - Main contains bottom tabs + extra stack destinations (details, my-complaints)
 */
sealed interface Route {
    val route: String

    data object Splash : Route { override val route = "splash" }

    data object Intro : Route { override val route = "intro" }
    data object Login : Route { override val route = "login" }
    data object Signup : Route { override val route = "signup" }

    data object Home : Route { override val route = "home" }
    data object Map : Route { override val route = "map" }
    data object Report : Route { override val route = "report" }
    data object Community : Route { override val route = "community" }
    data object Profile : Route { override val route = "profile" }

    data object MyComplaints : Route { override val route = "my_complaints" }
    data object IssueDetail : Route { override val route = "issue_detail" }
}
