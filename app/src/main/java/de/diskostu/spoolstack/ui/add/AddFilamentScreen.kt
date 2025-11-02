package de.diskostu.spoolstack.ui.add

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilamentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddFilamentViewModel = hiltViewModel()
) {
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
            // vendor input field
            var vendor by remember { mutableStateOf("") }
            OutlinedTextField(
                value = vendor,
                onValueChange = { vendor = it },
                label = { Text(stringResource(R.string.vendor_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            // color input field
            var color by remember { mutableStateOf("") }
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text(stringResource(R.string.color_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            // size dropdown
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

            // save button
            Button(
                onClick = {
                    viewModel.save(vendor, color, selectedSize)
                    onNavigateBack()
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
