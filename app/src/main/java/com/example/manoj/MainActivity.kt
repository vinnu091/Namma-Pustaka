package com.example.vinnu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vinnu.ui.screens.*
import com.example.vinnu.ui.theme.*
import com.example.vinnu.viewmodel.LibraryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VinnuTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Books", Icons.Default.Home)
    object Scan : Screen("scan", "Scan", Icons.Default.Search)
    object History : Screen("history", "History", Icons.Default.DateRange)
    object Students : Screen("students", "Students", Icons.Default.Person)
    object Leaderboard : Screen("leaderboard", "Ranking", Icons.Default.List)
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: LibraryViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Home,
        Screen.Scan,
        Screen.History,
        Screen.Students,
        Screen.Leaderboard
    )

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            // SOP Bottom Navigation (5.3) - 64px height, Active item uses --color-accent
            NavigationBar(
                containerColor = SurfaceColor,
                tonalElevation = 0.dp, // No harsh shadows/elevation (2.0)
                modifier = Modifier.padding(top = 1.dp) // Subtle separator
            ) {
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label, style = Typography.labelSmall) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentColor,
                            selectedTextColor = AccentColor,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Color.Transparent // Clean look
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel, navController) }
            composable(Screen.Scan.route) { ScannerScreen(viewModel, navController) }
            composable(Screen.History.route) { HistoryScreen(viewModel) }
            composable(Screen.Students.route) { StudentScreen(viewModel) }
            composable(Screen.Leaderboard.route) { LeaderboardScreen(viewModel) }
            composable("add_book") { AddBookScreen(viewModel, navController) }
            composable(
                route = "book_detail/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
                BookDetailScreen(bookId, viewModel, navController)
            }
        }
    }
}
