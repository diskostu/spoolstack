package de.diskostu.spoolstack.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun FilamentListScreen(
    onNavigateBack: () -> Unit,
    viewModel: FilamentListViewModel = hiltViewModel()
) {
    val filaments by viewModel.filaments.collectAsState()

    FilamentListContent(
        filaments = filaments,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentListContent(
    filaments: List<Filament>,
    onNavigateBack: () -> Unit
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filaments) { filament ->
                FilamentCard(filament = filament)
            }
        }
    }
}

@Composable
fun FilamentCard(filament: Filament) {
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = filament.vendor,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = filament.color,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.remaining_weight, filament.size),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = stringResource(R.string.created_date_label, formatDate(filament.createdDate, df)),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = stringResource(R.string.changed_date_label, formatDate(filament.changeDate, df)),
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
                    size = "1kg"
                ),
                Filament(
                    id = 2,
                    vendor = "Sunlu",
                    color = "PLA White",
                    size = "1kg"
                )
            ),
            onNavigateBack = {}
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
                size = "1kg"
            )
        )
    }
}
