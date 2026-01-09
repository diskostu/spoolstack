package de.diskostu.spoolstack.ui.print.list

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.data.Print
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrintListScreen(
    onNavigateBack: () -> Unit,
    viewModel: PrintListViewModel = hiltViewModel()
) {
    val prints by viewModel.prints.collectAsState()
    val filaments by viewModel.filaments.collectAsState()

    PrintListContent(
        prints = prints,
        filaments = filaments,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintListContent(
    prints: List<Print>,
    filaments: Map<Int, Filament>,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.print_list_title)) },
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
            items(prints) { print ->
                PrintCard(
                    print = print,
                    filament = filaments[print.filamentId]
                )
            }
        }
    }
}

@Composable
fun PrintCard(
    print: Print,
    filament: Filament?
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault())
    val dateStr = dateFormat.format(Date(print.printDate))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Print Name
            Text(
                text = print.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Filament details
            Text(
                text = filament?.let {
                    stringResource(R.string.filament_display_format, it.vendor, it.color)
                } ?: "Unknown Filament (ID: ${print.filamentId})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Amount used
            Text(
                text = stringResource(R.string.used_amount_format, print.amountUsed),
                style = MaterialTheme.typography.bodyMedium
            )

            // Date
            Text(
                text = stringResource(R.string.print_date_format, dateStr),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Comment if present
            if (!print.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = print.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
            }

            // URL if present
            if (!print.url.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = print.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(print.url))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handle potential errors if no browser is available or url is invalid
                            e.printStackTrace()
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrintListScreenPreview() {
    val sampleFilament = Filament(
        id = 1,
        vendor = "Prusa",
        color = "Orange",
        size = 1000
    )
    val samplePrint1 = Print(
        id = 1,
        name = "Benchy",
        filamentId = 1,
        amountUsed = 12.5,
        printDate = System.currentTimeMillis(),
        comment = "Sample comment",
        url = "https://www.google.com"
    )
    val samplePrint2 = Print(
        id = 2,
        name = "Boat",
        filamentId = 1,
        amountUsed = 60.0,
        printDate = System.currentTimeMillis(),
        url = "https://www.google.com"
    )

    SpoolstackTheme {
        PrintListContent(
            prints = listOf(samplePrint1, samplePrint2),
            filaments = mapOf(1 to sampleFilament),
            onNavigateBack = {}
        )
    }
}
