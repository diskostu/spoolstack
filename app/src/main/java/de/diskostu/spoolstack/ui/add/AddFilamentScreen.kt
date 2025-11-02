package de.diskostu.spoolstack.ui.add

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    LaunchedEffect(Unit) {
        viewModel.savedFilamentId.collectLatest { newId ->
            Toast.makeText(
                context,
                context.getString(R.string.filament_saved_message, newId),
                Toast.LENGTH_SHORT
            ).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_filament_title)) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var vendor by remember { mutableStateOf("") }
            var vendorError by remember { mutableStateOf<String?>(null) }
            var color by remember { mutableStateOf("") }
            var colorError by remember { mutableStateOf<String?>(null) }

            OutlinedTextField(
                value = vendor,
                onValueChange = { 
                    vendor = it
                    vendorError = null
                 },
                label = { Text(stringResource(R.string.vendor_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = vendorError != null,
                supportingText = { vendorError?.let { Text(it) } }
            )

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

            val sizeOptions = listOf(
                stringResource(R.string.size_500g),
                stringResource(R.string.size_1kg),
                stringResource(R.string.size_2kg)
            )
            var expanded by remember { mutableStateOf(false) }
            var selectedSize by remember { mutableStateOf(sizeOptions[1]) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedSize,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.size_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sizeOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedSize = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    var hasError = false
                    if (vendor.isBlank()) {
                        vendorError = context.getString(R.string.error_field_cant_be_empty)
                        hasError = true
                    }
                    if (color.isBlank()) {
                        colorError = context.getString(R.string.error_field_cant_be_empty)
                        hasError = true
                    }

                    if (!hasError) {
                        viewModel.save(vendor, color, selectedSize)
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
