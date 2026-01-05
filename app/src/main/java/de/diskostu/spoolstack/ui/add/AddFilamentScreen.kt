package de.diskostu.spoolstack.ui.add

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilamentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddFilamentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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

                // Vendor Autocomplete
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

                // Size Selection
                Text(
                    text = stringResource(R.string.size_label),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                }

                AnimatedVisibility(
                    visible = isCustomSize,
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${sliderValue.toInt()}$unitGrams",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 100f..1000f,
                            steps = 17 // (1000-100)/50 - 1 = 18 - 1 = 17 steps
                        )
                    }
                }
            }

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
                            "${sliderValue.toInt()}$unitGrams"
                        } else {
                            size1kg
                        }
                        viewModel.save(vendor, color, sizeToSave)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.save))
            }
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
