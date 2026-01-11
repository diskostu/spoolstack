package de.diskostu.spoolstack.ui.main

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val filamentCount by viewModel.filamentCount.collectAsStateWithLifecycle()
    val printCount by viewModel.printCount.collectAsStateWithLifecycle()

    MainScreenContent(
        navController = navController,
        modifier = modifier,
        filamentCount = filamentCount,
        printCount = printCount,
        onClearFilaments = { onCompletion ->
            viewModel.clearAllFilaments(onCompletion)
        },
        onClearPrints = { onCompletion ->
            viewModel.clearAllPrints(onCompletion)
        },
        onAddSampleFilaments = { onCompletion ->
            viewModel.addSampleFilaments(onCompletion)
        }
    )
}

@Composable
internal fun MainScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    filamentCount: Int,
    printCount: Int,
    onClearFilaments: (() -> Unit) -> Unit,
    onClearPrints: (() -> Unit) -> Unit,
    onAddSampleFilaments: (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    val textFilamentsDeleted = stringResource(R.string.debug_filaments_deleted)
    val textPrintsDeleted = stringResource(R.string.debug_prints_deleted)
    val textSampleFilamentsAdded = stringResource(R.string.debug_sample_filaments_added)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Name / Title
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // AREA 1: Filaments
                SectionContainer(
                    title = stringResource(R.string.section_filaments),
                    badgeCount = filamentCount
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("add_filament") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.add))
                        }

                        Button(
                            onClick = { navController.navigate("filament_list") },
                            modifier = Modifier.weight(1f),
                            enabled = filamentCount > 0
                        ) {
                            Text(stringResource(R.string.list))
                        }
                    }
                }

                // AREA 2: Prints
                SectionContainer(
                    title = stringResource(R.string.section_prints),
                    badgeCount = printCount
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("record_print") },
                            modifier = Modifier.weight(1f),
                            enabled = filamentCount > 0
                        ) {
                            Text(stringResource(R.string.add))
                        }

                        Button(
                            onClick = { navController.navigate("print_list") },
                            modifier = Modifier.weight(1f),
                            enabled = printCount > 0
                        ) {
                            Text(stringResource(R.string.list))
                        }
                    }
                }

                // Debug buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DebugButton(
                            onClick = {
                                onClearFilaments {
                                    Toast.makeText(
                                        context,
                                        textFilamentsDeleted,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            text = stringResource(R.string.debug_clear_filaments),
                            modifier = Modifier.weight(1f)
                        )

                        DebugButton(
                            onClick = {
                                onClearPrints {
                                    Toast.makeText(
                                        context,
                                        textPrintsDeleted,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            text = stringResource(R.string.debug_clear_prints),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    DebugButton(
                        onClick = {
                            onAddSampleFilaments {
                                Toast.makeText(
                                    context,
                                    textSampleFilamentsAdded,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        text = stringResource(R.string.debug_add_sample_filaments),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Spacer to push content up if needed
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews

@ThemePreviews
@Composable
fun MainScreenPreview() {
    SpoolstackTheme {
        MainScreenContent(
            navController = rememberNavController(),
            filamentCount = 12,
            printCount = 42,
            onClearFilaments = { },
            onClearPrints = { },
            onAddSampleFilaments = { }
        )
    }
}
