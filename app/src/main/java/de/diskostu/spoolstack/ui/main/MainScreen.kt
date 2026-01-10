package de.diskostu.spoolstack.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.ui.components.DebugButton
import de.diskostu.spoolstack.ui.components.SectionContainer
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    var filamentCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getFilamentCount { count ->
            filamentCount = count
        }
    }

    MainScreenContent(
        navController = navController,
        modifier = modifier,
        filamentCount = filamentCount,
        onClearAll = { onCompletion ->
            viewModel.clearAllFilaments(onCompletion)
        }
    )
}

@Composable
private fun MainScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    filamentCount: Int,
    onClearAll: (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    val textFilamentsDeleted = stringResource(R.string.debug_filaments_deleted)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AREA 1: Filaments
            SectionContainer(title = stringResource(R.string.section_filaments)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.filaments_in_stock, filamentCount),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.navigate("add_filament") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.add_filament))
                    }

                    Button(
                        onClick = { navController.navigate("filament_list") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.view_filaments))
                    }
                }
            }

            // AREA 2: Prints
            SectionContainer(title = stringResource(R.string.section_prints)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("record_print") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.record_print))
                    }

                    Button(
                        onClick = { navController.navigate("print_list") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.view_prints))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            DebugButton(
                onClick = {
                    onClearAll {
                        Toast.makeText(
                            context,
                            textFilamentsDeleted,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                text = stringResource(R.string.debug_clear_filaments)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SpoolstackTheme {
        MainScreenContent(
            navController = rememberNavController(),
            filamentCount = 12,
            onClearAll = { }
        )
    }
}
