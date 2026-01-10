package de.diskostu.spoolstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.diskostu.spoolstack.ui.filament.add.AddFilamentScreen
import de.diskostu.spoolstack.ui.filament.list.FilamentListScreen
import de.diskostu.spoolstack.ui.main.MainScreen
import de.diskostu.spoolstack.ui.print.list.PrintListScreen
import de.diskostu.spoolstack.ui.print.add.RecordPrintScreen
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpoolstackTheme {
                val navController = rememberNavController()

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
                }
            }
        }
    }
}
