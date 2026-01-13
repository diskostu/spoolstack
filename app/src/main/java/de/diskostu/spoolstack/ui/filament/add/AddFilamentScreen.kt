package de.diskostu.spoolstack.ui.filament.add

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import de.diskostu.spoolstack.data.ColorWithName
import de.diskostu.spoolstack.ui.components.ChipRowPlaceholder
import de.diskostu.spoolstack.ui.components.HorizontalChipRowWithColor
import de.diskostu.spoolstack.ui.components.ColorPickerDialog
import de.diskostu.spoolstack.ui.components.DeleteConfirmationDialog
import de.diskostu.spoolstack.ui.components.SectionContainer
import de.diskostu.spoolstack.ui.components.animation.HorizontalSlideAnimatedContent
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import de.diskostu.spoolstack.ui.util.ColorUtils
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AddFilamentScreen(
    onNavigateBack: () -> Unit, viewModel: AddFilamentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val existingVendors by viewModel.vendors.collectAsStateWithLifecycle()
    val frequentColors by viewModel.frequentColors.collectAsStateWithLifecycle()
    val recentColors by viewModel.recentColors.collectAsStateWithLifecycle()
    val filamentState by viewModel.filamentState.collectAsStateWithLifecycle()
    val defaultFilamentSize by viewModel.defaultFilamentSize.collectAsStateWithLifecycle()

    val filamentSavedMessage = stringResource(R.string.filament_saved_message)

    LaunchedEffect(Unit) {
        viewModel.savedFilamentId.collectLatest { newId ->
            Toast.makeText(
                context, filamentSavedMessage.format(newId), Toast.LENGTH_SHORT
            ).show()
            onNavigateBack()
        }
    }

    AddFilamentContent(
        existingVendors = existingVendors,
        frequentColors = frequentColors,
        recentColors = recentColors,
        filamentState = filamentState,
        defaultFilamentSize = defaultFilamentSize,
        onNavigateBack = onNavigateBack,
        onSave = viewModel::save,
        getColorName = { viewModel.getColorName(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilamentContent(
    existingVendors: List<String>,
    frequentColors: List<ColorWithName>,
    recentColors: List<ColorWithName>,
    filamentState: Filament?,
    defaultFilamentSize: Int,
    onNavigateBack: () -> Unit,
    onSave: (String, String, Int, Int, Int?, String?, Long?, Double?, Boolean) -> Unit,
    getColorName: suspend (String) -> String
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val errorFieldCantBeEmpty = stringResource(R.string.error_field_cant_be_empty)
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
                })
        }, modifier = Modifier.imePadding()
    ) { paddingValues ->
        // State hoisting
        var vendor by rememberSaveable { mutableStateOf("") }
        var vendorError by rememberSaveable { mutableStateOf<String?>(null) }
        var colorName by rememberSaveable { mutableStateOf("") }
        var colorHex by rememberSaveable { mutableStateOf<String?>(null) }
        var colorError by rememberSaveable { mutableStateOf<String?>(null) }

        var totalWeight by rememberSaveable { mutableIntStateOf(defaultFilamentSize) }
        var spoolWeightInput by rememberSaveable { mutableStateOf("") }

        var sliderValue by rememberSaveable { mutableFloatStateOf(defaultFilamentSize.toFloat()) }
        var currentWeightInput by rememberSaveable { mutableStateOf(defaultFilamentSize.toString()) }

        var boughtAt by rememberSaveable { mutableStateOf("") }
        var boughtAtError by rememberSaveable { mutableStateOf<String?>(null) }
        var price by rememberSaveable { mutableStateOf("") }
        var boughtDateLong by rememberSaveable { mutableStateOf<Long?>(null) }

        // Initial setup for default size if not editing
        LaunchedEffect(defaultFilamentSize) {
            if (filamentState == null) {
                totalWeight = defaultFilamentSize
                sliderValue = defaultFilamentSize.toFloat()
                currentWeightInput = defaultFilamentSize.toString()
            }
        }

        // Load data if editing
        LaunchedEffect(filamentState) {
            filamentState?.let { filament ->
                vendor = filament.vendor
                colorHex = filament.colorHex
                colorName = getColorName(filament.colorHex)
                totalWeight = filament.totalWeight
                spoolWeightInput = filament.spoolWeight?.toString() ?: ""
                boughtAt = filament.boughtAt ?: ""
                price = filament.price?.toString() ?: ""
                boughtDateLong = filament.boughtDate
                sliderValue = filament.currentWeight.toFloat()
                currentWeightInput = filament.currentWeight.toString()
            }
        }

        // Date Picker
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    boughtDateLong = datePickerState.selectedDateMillis
                }) { Text("OK") }
            }, dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }) { DatePicker(state = datePickerState) }
        }

        // Color Picker
        var showColorPicker by remember { mutableStateOf(false) }
        if (showColorPicker) {
            ColorPickerDialog(
                initialColor = ColorUtils.hexToColor(colorHex) ?: Color.White,
                onColorSelected = { selectedColor ->
                    val hex = ColorUtils.colorToHex(selectedColor)
                    colorHex = hex
                },
                onDismissRequest = { showColorPicker = false })
        }

        // Update color name when hex changes
        LaunchedEffect(colorHex) {
            colorHex?.let {
                colorName = getColorName(it)
                colorError = null
            }
        }

        // Delete Dialog State
        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onConfirm = {
                    showDeleteDialog = false
                    val weightToSave = currentWeightInput.toIntOrNull() ?: sliderValue.roundToInt()
                    colorHex?.let { hex ->
                        onSave(
                            vendor,
                            hex,
                            weightToSave,
                            totalWeight,
                            spoolWeightInput.toIntOrNull(),
                            boughtAt.ifBlank { null },
                            boughtDateLong,
                            price.toDoubleOrNull(),
                            true // deleted = true
                        )
                    }
                },
                onDismiss = {
                    showDeleteDialog = false
                    val weightToSave = currentWeightInput.toIntOrNull() ?: sliderValue.roundToInt()
                    colorHex?.let { hex ->
                        onSave(
                            vendor,
                            hex,
                            weightToSave,
                            totalWeight,
                            spoolWeightInput.toIntOrNull(),
                            boughtAt.ifBlank { null },
                            boughtDateLong,
                            price.toDoubleOrNull(),
                            false // deleted = false
                        )
                    }
                },
                message = stringResource(R.string.delete_empty_confirmation_message),
                confirmButtonText = stringResource(R.string.delete_and_save),
                dismissButtonText = stringResource(R.string.save_without_deleting)
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
                    VendorField(
                        vendor = vendor, onVendorChange = {
                            vendor = it
                            vendorError = null
                        }, vendorError = vendorError, existingVendors = existingVendors
                    )
                }

                // AREA 2: Color
                SectionContainer(title = stringResource(R.string.section_color)) {
                    ColorField(
                        colorName = colorName,
                        colorHex = colorHex,
                        onOpenColorPicker = { showColorPicker = true },
                        colorError = colorError,
                        frequentColors = frequentColors,
                        recentColors = recentColors,
                        isEditMode = filamentState != null,
                        onColorHexSelected = { colorHex = it })
                }

                // AREA 3: Size & Weight
                SectionContainer(title = stringResource(R.string.section_size)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isLandscape) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    TotalWeightField(
                                        totalWeight = totalWeight, onTotalWeightChange = {
                                            totalWeight = it
                                            // Synchronize current weight with total weight when purchase size changes
                                            currentWeightInput = it.toString()
                                            sliderValue = it.toFloat()
                                        }, enabled = filamentState == null
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SpoolWeightField(
                                        spoolWeight = spoolWeightInput,
                                        onSpoolWeightChange = { spoolWeightInput = it },
                                        unitGrams = unitGrams
                                    )
                                }
                            }
                        } else {
                            TotalWeightField(
                                totalWeight = totalWeight, onTotalWeightChange = {
                                    totalWeight = it
                                    // Synchronize current weight with total weight when purchase size changes
                                    currentWeightInput = it.toString()
                                    sliderValue = it.toFloat()
                                }, enabled = filamentState == null
                            )
                            SpoolWeightField(
                                spoolWeight = spoolWeightInput,
                                onSpoolWeightChange = { spoolWeightInput = it },
                                unitGrams = unitGrams
                            )
                        }

                        SizeSection(
                            sizeInput = currentWeightInput,
                            onSizeInputChange = { input, value ->
                                currentWeightInput = input
                                sliderValue = value
                            },
                            sliderValue = sliderValue,
                            onSliderChange = { input, value ->
                                currentWeightInput = input
                                sliderValue = value
                            },
                            totalWeight = totalWeight,
                            unitGrams = unitGrams
                        )
                    }
                }

                // AREA 4: Purchase Information
                SectionContainer(title = stringResource(R.string.section_purchase_info)) {
                    if (isLandscape) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(2f)) {
                                BoughtAtField(
                                    boughtAt = boughtAt, onBoughtAtChange = {
                                        boughtAt = it
                                        boughtAtError = null
                                    }, boughtAtError = boughtAtError
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PriceField(price = price, onPriceChange = { price = it })
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                BoughtDateField(
                                    boughtDateLong = boughtDateLong,
                                    onShowDatePicker = { showDatePicker = true })
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            BoughtAtField(
                                boughtAt = boughtAt, onBoughtAtChange = {
                                    boughtAt = it
                                    boughtAtError = null
                                }, boughtAtError = boughtAtError
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PriceField(price = price, onPriceChange = { price = it })
                                }
                                Box(modifier = Modifier.weight(1.2f)) {
                                    BoughtDateField(
                                        boughtDateLong = boughtDateLong,
                                        onShowDatePicker = { showDatePicker = true })
                                }
                            }
                        }
                    }
                }
            }

            // Save Button
            SaveButtonRow(
                onCancel = onNavigateBack, onSave = {
                    var hasError = false
                    if (vendor.isBlank()) {
                        vendorError = errorFieldCantBeEmpty
                        hasError = true
                    }
                    if (colorHex == null) {
                        colorError = errorFieldCantBeEmpty
                        hasError = true
                    }
                    if (price.isNotBlank() && boughtAt.isBlank()) {
                        boughtAtError = errorFieldCantBeEmpty
                        hasError = true
                    }

                    if (!hasError) {
                        val weightToSave =
                            currentWeightInput.toIntOrNull() ?: sliderValue.roundToInt()
                        if (weightToSave == 0 && filamentState != null) {
                            showDeleteDialog = true
                        } else {
                            colorHex?.let { hex ->
                                onSave(
                                    vendor,
                                    hex,
                                    weightToSave,
                                    totalWeight,
                                    spoolWeightInput.toIntOrNull(),
                                    boughtAt.ifBlank { null },
                                    boughtDateLong,
                                    price.toDoubleOrNull(),
                                    false // deleted = false
                                )
                            }
                        }
                    }
                })
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
        expanded = vendorExpanded, onExpandedChange = { vendorExpanded = !vendorExpanded }) {
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
            singleLine = true)

        if (existingVendors.isNotEmpty() && filteredVendors.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = vendorExpanded, onDismissRequest = { vendorExpanded = false }) {
                filteredVendors.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(selectionOption) }, onClick = {
                        onVendorChange(selectionOption)
                        vendorExpanded = false
                    })
                }
            }
        }
    }
}

@Composable
private fun ColorField(
    colorName: String,
    colorHex: String?,
    onOpenColorPicker: () -> Unit,
    colorError: String?,
    frequentColors: List<ColorWithName> = emptyList(),
    recentColors: List<ColorWithName> = emptyList(),
    isEditMode: Boolean,
    onColorHexSelected: (String) -> Unit
) {
    val displayColor = ColorUtils.hexToColor(colorHex)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!isEditMode) {
            HorizontalSlideAnimatedContent(
                targetState = frequentColors, durationMillis = 500
            ) { currentColors ->
                if (currentColors.isNotEmpty()) {
                    HorizontalChipRowWithColor(
                        imageVector = Icons.Filled.Whatshot,
                        colors = currentColors,
                        onColorHexSelected = onColorHexSelected
                    )
                } else {
                    // Placeholder for frequent colors
                    ChipRowPlaceholder(height = 48.dp)
                }
            }
        }

        // show recent colors chips - only if not editing
        if (!isEditMode) {
            HorizontalSlideAnimatedContent(
                targetState = recentColors, durationMillis = 500
            ) { currentColors ->
                if (currentColors.isNotEmpty()) {
                    HorizontalChipRowWithColor(
                        imageVector = Icons.Filled.History,
                        colors = currentColors,
                        onColorHexSelected = onColorHexSelected
                    )
                } else {
                    // Placeholder for recent colors
                    ChipRowPlaceholder(height = 48.dp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenColorPicker() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(displayColor ?: Color.Gray)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .testTag("color_picker_trigger"), contentAlignment = Alignment.Center
            ) {
                if (displayColor == null) {
                    Icon(
                        imageVector = Icons.Default.QuestionMark,
                        contentDescription = "Unknown color",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.color_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (colorError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.testTag("color_name_text"),
                    text = colorName.ifBlank { stringResource(R.string.select_color) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (colorName.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (colorError != null) {
                    Text(
                        text = colorError,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TotalWeightField(
    totalWeight: Int, onTotalWeightChange: (Int) -> Unit, enabled: Boolean
) {
    val weightOptions = listOf(500, 750, 1000, 2000)
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(
            value = if (totalWeight >= 1000) "${totalWeight / 1000}kg" else "${totalWeight}g",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.total_weight_label)) },
            trailingIcon = if (enabled) {
                { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            } else null,
            modifier = Modifier
                .testTag("total_weight_input")
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true))
        if (enabled) {
            ExposedDropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }) {
                weightOptions.forEach { weight ->
                    DropdownMenuItem(
                        text = { Text(if (weight >= 1000) "${weight / 1000}kg" else "${weight}g") },
                        onClick = {
                            onTotalWeightChange(weight)
                            expanded = false
                        })
                }
            }
        }
    }
}

@Composable
private fun SpoolWeightField(
    spoolWeight: String, onSpoolWeightChange: (String) -> Unit, unitGrams: String
) {
    OutlinedTextField(
        value = spoolWeight,
        onValueChange = {
            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                onSpoolWeightChange(it)
            }
        },
        label = { Text(stringResource(R.string.spool_weight_label)) },
        suffix = { Text(unitGrams) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .testTag("spool_weight_input")
            .fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun SizeSection(
    sizeInput: String,
    onSizeInputChange: (String, Float) -> Unit,
    sliderValue: Float,
    onSliderChange: (String, Float) -> Unit,
    totalWeight: Int,
    unitGrams: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = sizeInput,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    val valInt = it.toIntOrNull() ?: 0
                    onSizeInputChange(it, valInt.toFloat())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("size_input"),
            label = { Text(stringResource(R.string.size_label)) },
            suffix = { Text(unitGrams) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Slider(
            value = sliderValue.coerceIn(0f, maxOf(totalWeight.toFloat(), sliderValue)),
            onValueChange = {
                val rounded = (it / 10).roundToInt() * 10
                onSliderChange(rounded.toString(), rounded.toFloat())
            },
            valueRange = 0f..maxOf(totalWeight.toFloat(), sliderValue),
            modifier = Modifier
                .testTag("size_slider")
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun BoughtAtField(
    boughtAt: String, onBoughtAtChange: (String) -> Unit, boughtAtError: String?
) {
    OutlinedTextField(
        value = boughtAt,
        onValueChange = onBoughtAtChange,
        label = { Text(stringResource(R.string.bought_at_label)) },
        modifier = Modifier
            .testTag("bought_at_input")
            .fillMaxWidth(),
        isError = boughtAtError != null,
        supportingText = { boughtAtError?.let { Text(it) } },
        singleLine = true
    )
}

@Composable
private fun PriceField(
    price: String, onPriceChange: (String) -> Unit
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
        modifier = Modifier
            .testTag("price_input")
            .fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun BoughtDateField(
    boughtDateLong: Long?, onShowDatePicker: () -> Unit
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
            IconButton(
                onClick = { onShowDatePicker() },
                modifier = Modifier.testTag("bought_date_picker_trigger")
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        },
        modifier = Modifier
            .testTag("bought_date_input")
            .fillMaxWidth()
            .clickable { onShowDatePicker() },
        enabled = true,
        singleLine = true
    )
}

@Composable
private fun SaveButtonRow(
    onCancel: () -> Unit, onSave: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onCancel, modifier = Modifier
                .testTag("cancel_button")
                .weight(1f)
        ) { Text(text = stringResource(id = R.string.cancel)) }
        Button(
            onClick = onSave, modifier = Modifier
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

@Suppress("SpellCheckingInspection")
@PortraitPreviews
@LandscapePreviews
@Composable
fun AddFilamentScreenPreview() {
    SpoolstackTheme {
        val frequentColors = listOf(
            ColorWithName("#000000", "Black"), ColorWithName("#FF0000", "Red")
        )
        val recentColors = listOf(
            ColorWithName("#664433", "Lala"), ColorWithName("#FF5577", "Demo")
        )
        AddFilamentContent(
            existingVendors = listOf("Prusa", "Creality", "Extrudr"),
            frequentColors = frequentColors,
            recentColors = recentColors,
            filamentState = null,
            defaultFilamentSize = 1000,
            onNavigateBack = {},
            onSave = { _, _, _, _, _, _, _, _, _ -> },
            getColorName = { "Black" })
    }
}
