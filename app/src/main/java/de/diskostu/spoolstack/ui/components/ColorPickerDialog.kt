package de.diskostu.spoolstack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import de.diskostu.spoolstack.R

@Composable
fun ColorPickerDialog(
    onColorSelected: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    initialColor: Color = Color.White
) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf(initialColor) }

    // Sync the controller with the initial color when the dialog is first shown or initialColor changes.
    // This ensures that the sliders (Brightness, Alpha) and the wheel thumb correctly reflect the detected color.
    LaunchedEffect(initialColor) {
        controller.selectByColor(initialColor, fromUser = false)
        selectedColor = initialColor
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.select_color)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Preview of the selected color
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectedColor)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(12.dp)
                            )
                    )

                    Column {
                        Text(
                            text = stringResource(R.string.selected_color_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = String.format("#%06X", (0xFFFFFF and selectedColor.toArgb())),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        // Update our local preview state when the user interacts with the picker
                        if (colorEnvelope.fromUser) {
                            selectedColor = colorEnvelope.color
                        }
                    }
                )

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    controller = controller
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
