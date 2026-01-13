package de.diskostu.spoolstack.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.ColorWithName
import de.diskostu.spoolstack.ui.components.animation.HorizontalSlideAnimatedContent
import de.diskostu.spoolstack.ui.components.animation.VerticalSlideAnimatedContent
import de.diskostu.spoolstack.ui.util.ColorUtils
import kotlinx.coroutines.delay

@Composable
fun ColorPickerDialog(
    onColorSelected: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    initialColor: Color = Color.White
) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf(initialColor) }
    var suggestedColors by remember { mutableStateOf<List<ColorWithName>>(emptyList()) }

    // Sync the controller with the initial color when the dialog is first shown or initialColor changes.
    LaunchedEffect(initialColor) {
        controller.selectByColor(initialColor, fromUser = false)
        selectedColor = initialColor
    }

    // Suggested colors debounced update
    LaunchedEffect(selectedColor) {
        val closest = ColorUtils.getClosestColors(ColorUtils.colorToHex(selectedColor))
        suggestedColors = closest.map { ColorWithName(it.second, it.first) }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.select_color),
                textAlign = TextAlign.Center
            )
        },
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

                val rowHeight = 48.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .testTag("suggested_colors_container")
                ) {
                    // use a slide animation
                    HorizontalSlideAnimatedContent(
                        targetState = suggestedColors,
                        durationMillis = 200,
                    ) { currentSuggestions ->
                        if (currentSuggestions.isEmpty()) {
                            // placeholder at first
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            )
                        } else {
                            HorizontalChipRowWithColor(
                                imageVector = null,
                                colors = currentSuggestions,
                                onColorHexSelected = { hex ->
                                    ColorUtils.hexToColor(hex)?.let { color ->
                                        controller.selectByColor(color, fromUser = true)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismissRequest()
                }, Modifier.testTag("save_button_color_picker")
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .testTag("cancel_button_color_picker")
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
