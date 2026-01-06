package de.diskostu.spoolstack.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale


@Composable
fun FilamentListScreen(
    onNavigateBack: () -> Unit,
    onFilamentClick: (Int) -> Unit,
    viewModel: FilamentListViewModel = hiltViewModel()
) {
    val filaments by viewModel.filaments.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.onUiStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.onUiStop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    FilamentListContent(
        filaments = filaments,
        onNavigateBack = onNavigateBack,
        onFilamentClick = onFilamentClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentListContent(
    filaments: List<Filament>,
    onNavigateBack: () -> Unit,
    onFilamentClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filament_list_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_content_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = filaments,
                key = { it.id }
            ) { filament ->
                FilamentCard(
                    filament = filament,
                    onClick = { onFilamentClick(filament.id) },
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                )
            }
        }
    }
}

@Composable
fun FilamentCard(
    filament: Filament,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = filament.vendor + " | " + filament.color,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.remaining_weight, filament.size),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            val created =
                stringResource(R.string.created_date_label, formatDate(filament.createdDate, df))
            val modified =
                stringResource(R.string.changed_date_label, formatDate(filament.changeDate, df))
            Text(
                text = "$created | $modified",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun formatDate(timestamp: Long, df: DateFormat): String {
    return df.format(Date(timestamp))
}

@Preview(showBackground = true, heightDp = 500, name = "Light Mode")
@Preview(showBackground = true, heightDp = 500, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilamentListScreenPreview() {
    SpoolstackTheme {
        FilamentListContent(
            filaments = listOf(
                Filament(
                    id = 1,
                    vendor = "Prusament",
                    color = "Galaxy Black",
                    size = "1kg",
                    createdDate = System.currentTimeMillis(),
                    changeDate = System.currentTimeMillis()
                ),
                Filament(
                    id = 2,
                    vendor = "Sunlu",
                    color = "PLA White",
                    size = "1kg",
                    createdDate = System.currentTimeMillis(),
                    changeDate = System.currentTimeMillis()
                )
            ),
            onNavigateBack = {},
            onFilamentClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilamentCardPreview() {
    SpoolstackTheme {
        FilamentCard(
            filament = Filament(
                id = 1,
                vendor = "Prusament",
                color = "Galaxy Black",
                size = "1kg",
                createdDate = System.currentTimeMillis(),
                changeDate = System.currentTimeMillis()
            ),
            onClick = {}
        )
    }
}
