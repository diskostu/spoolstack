package de.diskostu.spoolstack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.diskostu.spoolstack.ui.add.AddFilamentScreen
import de.diskostu.spoolstack.ui.list.FilamentListScreen
import de.diskostu.spoolstack.ui.main.MainScreen
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpoolstackTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController = navController)
                    }
                    composable("add_filament") {
                        AddFilamentScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("filament_list") {
                        FilamentListScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
