package de.diskostu.spoolstack.ui.filament.add

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.components.ArchiveConfirmationDialog
import de.diskostu.spoolstack.ui.components.SectionContainer
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AddFilamentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddFilamentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val existingVendors by viewModel.vendors.collectAsStateWithLifecycle()
    val filamentState by viewModel.filamentState.collectAsStateWithLifecycle()

    val filamentSavedMessage = stringResource(R.string.filament_saved_message)

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

    AddFilamentContent(
        existingVendors = existingVendors,
        filamentState = filamentState,
        onNavigateBack = onNavigateBack,
        onSave = viewModel::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilamentContent(
    existingVendors: List<String>,
    filamentState: Filament?,
    onNavigateBack: () -> Unit,
    onSave: (String, String, Int, String?, Long?, Double?, Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val errorFieldCantBeEmpty = stringResource(R.string.error_field_cant_be_empty)
    val size1kg = stringResource(R.string.size_1kg)
    val unitGrams = stringResource(R.string.unit_grams)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (filamentState == null) R.string.add_filament_title else R.string.edit_filament_title)) },
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
        // State hoisting
        var vendor by rememberSaveable { mutableStateOf("") }
        var vendorError by rememberSaveable { mutableStateOf<String?>(null) }
        var color by rememberSaveable { mutableStateOf("") }
        var colorError by rememberSaveable { mutableStateOf<String?>(null) }
        var sliderValue by rememberSaveable { mutableFloatStateOf(1000f) }
        var sizeInput by rememberSaveable { mutableStateOf("1000") }
        var boughtAt by rememberSaveable { mutableStateOf("") }
        var boughtAtError by rememberSaveable { mutableStateOf<String?>(null) }
        var price by rememberSaveable { mutableStateOf("") }
        var boughtDateLong by rememberSaveable { mutableStateOf<Long?>(null) }

        // Load data if editing
        LaunchedEffect(filamentState) {
            filamentState?.let { filament ->
                vendor = filament.vendor
                color = filament.color
                boughtAt = filament.boughtAt ?: ""
                price = filament.price?.toString() ?: ""
                boughtDateLong = filament.boughtDate
                sliderValue = filament.size.toFloat()
                sizeInput = filament.size.toString()
            }
        }

        // Date Picker
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        boughtDateLong = datePickerState.selectedDateMillis
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        // Archive Dialog State
        var showArchiveDialog by remember { mutableStateOf(false) }

        if (showArchiveDialog) {
            ArchiveConfirmationDialog(
                onConfirm = {
                    showArchiveDialog = false
                    val sizeToSave = sizeInput.toIntOrNull() ?: sliderValue.roundToInt()
                    onSave(
                        vendor, color, sizeToSave,
                        boughtAt.ifBlank { null }, boughtDateLong, price.toDoubleOrNull(),
                        true // archived = true
                    )
                },
                onDismiss = {
                    showArchiveDialog = false
                    val sizeToSave = sizeInput.toIntOrNull() ?: sliderValue.roundToInt()
                    onSave(
                        vendor, color, sizeToSave,
                        boughtAt.ifBlank { null }, boughtDateLong, price.toDoubleOrNull(),
                        false // archived = false
                    )
                },
                message = stringResource(R.string.archive_empty_confirmation_message),
                confirmButtonText = stringResource(R.string.archive_and_save),
                dismissButtonText = stringResource(R.string.save_without_archiving)
            )
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
                // AREA 1: Filament Details
                SectionContainer(title = stringResource(R.string.section_filament_details)) {
                    if (isLandscape) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                VendorField(
                                    vendor = vendor,
                                    onVendorChange = {
                                        vendor = it
                                        vendorError = null
                                    },
                                    vendorError = vendorError,
                                    existingVendors = existingVendors
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ColorField(
                                    color = color,
                                    onColorChange = {
                                        color = it
                                        colorError = null
                                    },
                                    colorError = colorError
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            VendorField(
                                vendor = vendor,
                                onVendorChange = {
                                    vendor = it
                                    vendorError = null
                                },
                                vendorError = vendorError,
                                existingVendors = existingVendors
                            )
                            ColorField(
                                color = color,
                                onColorChange = {
                                    color = it
                                    colorError = null
                                },
                                colorError = colorError
                            )
                        }
                    }
                }

                // AREA 2: Size
                SectionContainer(title = stringResource(R.string.section_size)) {
                    SizeSection(
                        sizeInput = sizeInput,
                        onSizeInputChange = { input, value ->
                            sizeInput = input
                            sliderValue = value
                        },
                        sliderValue = sliderValue,
                        onSliderChange = { input, value ->
                            sizeInput = input
                            sliderValue = value
                        },
                        unitGrams = unitGrams,
                        size1kg = size1kg,
                        isLandscape = isLandscape
                    )
                }

                // AREA 3: Purchase Information
                SectionContainer(title = stringResource(R.string.section_purchase_info)) {
                    if (isLandscape) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(2f)) {
                                BoughtAtField(
                                    boughtAt = boughtAt,
                                    onBoughtAtChange = {
                                        boughtAt = it
                                        boughtAtError = null
                                    },
                                    boughtAtError = boughtAtError
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PriceField(price = price, onPriceChange = { price = it })
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                BoughtDateField(
                                    boughtDateLong = boughtDateLong,
                                    onShowDatePicker = { showDatePicker = true }
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            BoughtAtField(
                                boughtAt = boughtAt,
                                onBoughtAtChange = {
                                    boughtAt = it
                                    boughtAtError = null
                                },
                                boughtAtError = boughtAtError
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PriceField(price = price, onPriceChange = { price = it })
                                }
                                Box(modifier = Modifier.weight(1.2f)) {
                                    BoughtDateField(
                                        boughtDateLong = boughtDateLong,
                                        onShowDatePicker = { showDatePicker = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Save Button
            SaveButtonRow(
                onCancel = onNavigateBack,
                onSave = {
                    var hasError = false
                    if (vendor.isBlank()) {
                        vendorError = errorFieldCantBeEmpty
                        hasError = true
                    }
                    if (color.isBlank()) {
                        colorError = errorFieldCantBeEmpty
                        hasError = true
                    }
                    if (price.isNotBlank() && boughtAt.isBlank()) {
                        boughtAtError = errorFieldCantBeEmpty
                        hasError = true
                    }

                    if (!hasError) {
                        val sizeToSave = sizeInput.toIntOrNull() ?: sliderValue.roundToInt()
                        if (sizeToSave == 0 && filamentState != null) {
                            showArchiveDialog = true
                        } else {
                            onSave(
                                vendor, color, sizeToSave,
                                boughtAt.ifBlank { null }, boughtDateLong, price.toDoubleOrNull(),
                                false // archived = false
                            )
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendorField(
    vendor: String,
    onVendorChange: (String) -> Unit,
    vendorError: String?,
    existingVendors: List<String>
) {
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
                onVendorChange(it)
                vendorExpanded = true
            },
            label = { Text(stringResource(R.string.vendor_label)) },
            modifier = Modifier
                .testTag("vendor_input")
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            isError = vendorError != null,
            supportingText = { vendorError?.let { Text(it) } },
            trailingIcon = if (existingVendors.isNotEmpty()) {
                { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vendorExpanded) }
            } else null,
            singleLine = true
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
                            onVendorChange(selectionOption)
                            vendorExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorField(
    color: String,
    onColorChange: (String) -> Unit,
    colorError: String?
) {
    OutlinedTextField(
        value = color,
        onValueChange = onColorChange,
        label = { Text(stringResource(R.string.color_label)) },
        modifier = Modifier
            .testTag("color_input")
            .fillMaxWidth(),
        isError = colorError != null,
        supportingText = { colorError?.let { Text(it) } },
        singleLine = true
    )
}

@Composable
private fun SizeSection(
    sizeInput: String,
    onSizeInputChange: (String, Float) -> Unit,
    sliderValue: Float,
    onSliderChange: (String, Float) -> Unit,
    unitGrams: String,
    size1kg: String,
    isLandscape: Boolean = false
) {
    if (isLandscape) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = sizeInput,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        val valInt = it.toIntOrNull() ?: 0
                        onSizeInputChange(it, valInt.toFloat())
                    }
                },
                modifier = Modifier
                    .width(110.dp) // Enough for "1000" and suffix "g"
                    .testTag("size_input"),
                label = { Text(stringResource(R.string.size_label)) },
                suffix = { Text(unitGrams) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Slider(
                value = sliderValue.coerceIn(0f, maxOf(1000f, sliderValue)),
                onValueChange = {
                    val rounded = (it / 10).roundToInt() * 10
                    onSliderChange(rounded.toString(), rounded.toFloat())
                },
                valueRange = 0f..maxOf(1000f, sliderValue),
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = sliderValue == 1000f,
                onClick = { onSliderChange("1000", 1000f) },
                label = { Text(size1kg) },
                leadingIcon = if (sliderValue == 1000f) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = sizeInput,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            val valInt = it.toIntOrNull() ?: 0
                            onSizeInputChange(it, valInt.toFloat())
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("size_input"),
                    label = { Text(stringResource(R.string.size_label)) },
                    suffix = { Text(unitGrams) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                FilterChip(
                    selected = sliderValue == 1000f,
                    onClick = { onSliderChange("1000", 1000f) },
                    label = { Text(size1kg) },
                    leadingIcon = if (sliderValue == 1000f) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }

            Slider(
                value = sliderValue.coerceIn(0f, maxOf(1000f, sliderValue)),
                onValueChange = {
                    val rounded = (it / 10).roundToInt() * 10
                    onSliderChange(rounded.toString(), rounded.toFloat())
                },
                valueRange = 0f..maxOf(1000f, sliderValue),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun BoughtAtField(
    boughtAt: String,
    onBoughtAtChange: (String) -> Unit,
    boughtAtError: String?
) {
    OutlinedTextField(
        value = boughtAt,
        onValueChange = onBoughtAtChange,
        label = { Text(stringResource(R.string.bought_at_label)) },
        modifier = Modifier.fillMaxWidth(),
        isError = boughtAtError != null,
        supportingText = { boughtAtError?.let { Text(it) } },
        singleLine = true
    )
}

@Composable
private fun PriceField(
    price: String,
    onPriceChange: (String) -> Unit
) {
    OutlinedTextField(
        value = price,
        onValueChange = {
            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                onPriceChange(it)
            }
        },
        label = { Text(stringResource(R.string.price_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun BoughtDateField(
    boughtDateLong: Long?,
    onShowDatePicker: () -> Unit
) {
    OutlinedTextField(
        value = boughtDateLong?.let {
            val date = Date(it)
            val format =
                SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault())
            format.format(date)
        } ?: "",
        onValueChange = { },
        readOnly = true,
        label = { Text(stringResource(R.string.bought_date_label)) },
        trailingIcon = {
            IconButton(onClick = onShowDatePicker) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDatePicker() },
        enabled = true,
        singleLine = true
    )
}

@Composable
private fun SaveButtonRow(
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) { Text(text = stringResource(id = R.string.cancel)) }
        Button(
            onClick = onSave,
            modifier = Modifier
                .testTag("save_button")
                .weight(1f)
        ) { Text(text = stringResource(id = R.string.save)) }
    }
}

@Preview(name = "bright", group = "portrait", showBackground = true)
@Preview(
    name = "dark",
    group = "portrait",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class PortraitPreviews

@Preview(
    name = "bright",
    group = "landscape",
    showBackground = true,
    device = "spec:width=800dp,height=480dp,orientation=landscape"
)
@Preview(
    name = "dark",
    group = "landscape",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=480dp,orientation=landscape"
)
annotation class LandscapePreviews

@PortraitPreviews
@LandscapePreviews
@Composable
fun AddFilamentScreenPreview() {
    SpoolstackTheme {
        AddFilamentContent(
            existingVendors = listOf("Prusa", "Creality", "Extrudr"),
            filamentState = null,
            onNavigateBack = {},
            onSave = { _, _, _, _, _, _, _ -> }
        )
    }
}
