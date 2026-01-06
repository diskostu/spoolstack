package de.diskostu.spoolstack.ui.print.record

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPrintScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecordPrintViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val filaments by viewModel.filaments.collectAsState()

    val printSavedMessage = stringResource(R.string.print_saved_message)
    val errorFieldCantBeEmpty = stringResource(R.string.error_field_cant_be_empty)
    val unitGrams = stringResource(R.string.unit_grams)

    LaunchedEffect(Unit) {
        viewModel.printSaved.collectLatest {
            Toast.makeText(context, printSavedMessage, Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.record_print_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_content_description)
                        )
                    }
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { paddingValues ->
        var name by remember { mutableStateOf("") }
        var nameError by remember { mutableStateOf<String?>(null) }

        var selectedFilament by remember { mutableStateOf<Filament?>(null) }
        var filamentError by remember { mutableStateOf<String?>(null) }

        var maxAmount by remember { mutableFloatStateOf(1000f) }
        var sliderValue by remember { mutableFloatStateOf(maxAmount / 2) }

        var url by remember { mutableStateOf("") }
        var comment by remember { mutableStateOf("") }

        // Update max amount when filament changes
        LaunchedEffect(selectedFilament) {
            selectedFilament?.let {
                val sizeStr = it.size.lowercase()
                maxAmount = if (sizeStr.endsWith("kg")) {
                    (sizeStr.replace("kg", "").toDoubleOrNull() ?: 1.0) * 1000
                } else {
                    sizeStr.replace("g", "").toDoubleOrNull() ?: 1000.0
                }.toFloat()

                // Reset slider if it exceeds new max
                if (sliderValue > maxAmount) {
                    sliderValue = maxAmount
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name Field (Mandatory)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError != null && it.isNotBlank()) {
                            nameError = null
                        }
                    },
                    label = { Text(stringResource(R.string.print_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } }
                )

                // Filament Selection Dropdown
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedFilament?.let {
                            stringResource(
                                R.string.filament_dropdown_format,
                                it.vendor,
                                it.color,
                                it.size
                            )
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.filament_selection_label)) },
                        placeholder = { Text(stringResource(R.string.select_filament_hint)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        isError = filamentError != null,
                        supportingText = { filamentError?.let { Text(it) } }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filaments.forEach { filament ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(
                                            R.string.filament_dropdown_format,
                                            filament.vendor,
                                            filament.color,
                                            filament.size
                                        )
                                    )
                                },
                                onClick = {
                                    selectedFilament = filament
                                    filamentError = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Amount Slider
                Text(
                    text = stringResource(R.string.used_amount_label),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 5f..maxAmount,
                        steps = ((maxAmount - 5) / 5).toInt() - 1, // Calculate steps for 5g increments
                        modifier = Modifier.weight(1f),
                        enabled = selectedFilament != null
                    )
                    Text(
                        text = "${sliderValue.roundToInt()}$unitGrams",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.End
                    )
                }

                // Optional URL
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.url_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Optional Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(stringResource(R.string.comment_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 5
                )
            }

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Button(
                    onClick = {
                        var isValid = true

                        if (name.isBlank()) {
                            nameError = errorFieldCantBeEmpty
                            isValid = false
                        }

                        if (selectedFilament == null) {
                            filamentError = errorFieldCantBeEmpty
                            isValid = false
                        }

                        if (isValid) {
                            viewModel.savePrint(
                                name = name,
                                filament = selectedFilament!!,
                                amountUsed = sliderValue.toDouble(),
                                url = url.ifBlank { null },
                                comment = comment.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordPrintScreenPreview() {
    SpoolstackTheme {
        RecordPrintScreen(onNavigateBack = {})
    }
}
