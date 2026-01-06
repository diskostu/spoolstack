package de.diskostu.spoolstack.ui.add

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilamentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddFilamentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val existingVendors by viewModel.vendors.collectAsState()

    val filamentSavedMessage = stringResource(R.string.filament_saved_message)
    val errorFieldCantBeEmpty = stringResource(R.string.error_field_cant_be_empty)
    val size1kg = stringResource(R.string.size_1kg)
    val unitGrams = stringResource(R.string.unit_grams)

    LaunchedEffect(Unit) {
        viewModel.savedFilamentId.collectLatest { newId ->
            Toast.makeText(
                context,
                filamentSavedMessage.format(newId),
                Toast.LENGTH_SHORT
            ).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_filament_title)) },
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
        // State hoisting with rememberSaveable to survive configuration changes
        var vendor by rememberSaveable { mutableStateOf("") }
        var vendorError by rememberSaveable { mutableStateOf<String?>(null) }
        var color by rememberSaveable { mutableStateOf("") }
        var colorError by rememberSaveable { mutableStateOf<String?>(null) }
        var isCustomSize by rememberSaveable { mutableStateOf(false) }
        var sliderValue by rememberSaveable { mutableFloatStateOf(500f) }

        // Optional fields
        var boughtAt by rememberSaveable { mutableStateOf("") }
        var price by rememberSaveable { mutableStateOf("") }
        var boughtDateLong by rememberSaveable { mutableStateOf<Long?>(null) }

        // Date Picker State
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            boughtDateLong = datePickerState.selectedDateMillis
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Field Composables
        val vendorField: @Composable () -> Unit = {
            var vendorExpanded by rememberSaveable { mutableStateOf(false) }
            val filteredVendors = rememberSaveable(vendor, existingVendors) {
                if (vendor.isBlank()) existingVendors
                else existingVendors.filter { it.contains(vendor, ignoreCase = true) }
            }

            ExposedDropdownMenuBox(
                expanded = vendorExpanded,
                onExpandedChange = { vendorExpanded = !vendorExpanded }
            ) {
                OutlinedTextField(
                    value = vendor,
                    onValueChange = {
                        vendor = it
                        vendorError = null
                        vendorExpanded = true
                    },
                    label = { Text(stringResource(R.string.vendor_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
                    isError = vendorError != null,
                    supportingText = { vendorError?.let { Text(it) } },
                    trailingIcon = if (existingVendors.isNotEmpty()) {
                        { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vendorExpanded) }
                    } else null
                )

                if (existingVendors.isNotEmpty() && filteredVendors.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = vendorExpanded,
                        onDismissRequest = { vendorExpanded = false }
                    ) {
                        filteredVendors.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    vendor = selectionOption
                                    vendorExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        val colorField: @Composable () -> Unit = {
            OutlinedTextField(
                value = color,
                onValueChange = {
                    color = it
                    colorError = null
                },
                label = { Text(stringResource(R.string.color_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = colorError != null,
                supportingText = { colorError?.let { Text(it) } }
            )
        }

        val weightSliderRow: @Composable () -> Unit = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 100f..1000f,
                    steps = 17,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${sliderValue.roundToInt()}$unitGrams",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.width(60.dp), // Fixed width to prevent jumping
                    textAlign = TextAlign.End
                )
            }
        }

        val sizeField: @Composable (isLandscape: Boolean) -> Unit = { landscape ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!landscape) {
                    Text(
                        text = stringResource(R.string.size_label),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (landscape) {
                        Text(
                            text = stringResource(R.string.size_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    FilterChip(
                        selected = !isCustomSize,
                        onClick = { isCustomSize = false },
                        label = { Text(size1kg) }
                    )
                    FilterChip(
                        selected = isCustomSize,
                        onClick = { isCustomSize = true },
                        label = { Text(stringResource(R.string.size_custom)) }
                    )

                    // In landscape mode, if custom size is selected, show slider here in the same row
                    if (landscape) {
                        AnimatedVisibility(
                            visible = isCustomSize,
                            enter = fadeIn(animationSpec = tween(150)),
                            exit = fadeOut(animationSpec = tween(150)),
                            modifier = Modifier.weight(1f)
                        ) {
                            weightSliderRow()
                        }
                    }
                }

                // In portrait mode, the slider remains below
                if (!landscape) {
                    AnimatedVisibility(
                        visible = isCustomSize,
                        enter = fadeIn(animationSpec = tween(150)),
                        exit = fadeOut(animationSpec = tween(150))
                    ) {
                        weightSliderRow()
                    }
                }
            }
        }

        val boughtAtField: @Composable () -> Unit = {
            OutlinedTextField(
                value = boughtAt,
                onValueChange = { boughtAt = it },
                label = { Text(stringResource(R.string.bought_at_label)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        val priceField: @Composable () -> Unit = {
            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        price = it
                    }
                },
                label = { Text(stringResource(R.string.price_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        val boughtDateField: @Composable () -> Unit = {
            OutlinedTextField(
                value = boughtDateLong?.let {
                    val date = Date(it)
                    val format = SimpleDateFormat.getDateInstance(
                        SimpleDateFormat.MEDIUM,
                        Locale.getDefault()
                    )
                    format.format(date)
                } ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.bought_date_label)) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false
            )
        }

        val saveButton: @Composable () -> Unit = {
            Button(
                onClick = {
                    var hasError = false
                    if (vendor.isBlank()) {
                        vendorError = errorFieldCantBeEmpty
                        hasError = true
                    }
                    if (color.isBlank()) {
                        colorError = errorFieldCantBeEmpty
                        hasError = true
                    }

                    if (!hasError) {
                        val sizeToSave = if (isCustomSize) {
                            "${sliderValue.roundToInt()}$unitGrams"
                        } else {
                            size1kg
                        }
                        viewModel.save(
                            vendor,
                            color,
                            sizeToSave,
                            boughtAt.ifBlank { null },
                            boughtDateLong,
                            price.toDoubleOrNull()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.save))
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
                if (isLandscape) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(1f)) { vendorField() }
                        Box(Modifier.weight(1f)) { colorField() }
                    }
                    // Size field spans full width (both columns)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(1f)) { sizeField(true) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(1f)) { boughtAtField() }
                        Box(Modifier.weight(1f)) { priceField() }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.weight(1f)) { boughtDateField() }
                        Box(Modifier.weight(1f)) { /* Empty placeholder to balance grid or just leave as is */ }
                    }
                } else {
                    vendorField()
                    colorField()
                    sizeField(false)
                    Text(
                        text = "Optional Details",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    boughtAtField()
                    priceField()
                    boughtDateField()
                }
            }
            saveButton()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddFilamentScreenPreview() {
    SpoolstackTheme {
        AddFilamentScreen(onNavigateBack = {})
    }
}
