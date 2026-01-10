package de.diskostu.spoolstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.diskostu.spoolstack.ui.components.SettingsToggle
import de.diskostu.spoolstack.ui.filament.add.AddFilamentScreen
import de.diskostu.spoolstack.ui.filament.list.FilamentListScreen
import de.diskostu.spoolstack.ui.main.MainScreen
import de.diskostu.spoolstack.ui.print.list.PrintListScreen
import de.diskostu.spoolstack.ui.print.add.RecordPrintScreen
import de.diskostu.spoolstack.ui.settings.SettingsScreen
import de.diskostu.spoolstack.ui.settings.SettingsViewModel
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by settingsViewModel.appTheme.collectAsStateWithLifecycle()

            SpoolstackTheme(appTheme = appTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        composable("main") {
                            MainScreen(navController = navController)
                        }
                        composable(
                            route = "add_filament?filamentId={filamentId}",
                            arguments = listOf(
                                navArgument("filamentId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            AddFilamentScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("filament_list") {
                            FilamentListScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onFilamentClick = { filamentId ->
                                    navController.navigate("add_filament?filamentId=$filamentId")
                                }
                            )
                        }
                        composable("record_print") {
                            RecordPrintScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("print_list") {
                            PrintListScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    // Persistent toggle for fluid animation across navigation
                    val showToggle = currentRoute == "main" || currentRoute == "settings"
                    if (showToggle) {
                        SettingsToggle(
                            isSettingsActive = currentRoute == "settings",
                            onMainClick = {
                                if (currentRoute != "main") {
                                    navController.navigate("main") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            },
                            onSettingsClick = {
                                if (currentRoute != "settings") {
                                    navController.navigate("settings")
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
