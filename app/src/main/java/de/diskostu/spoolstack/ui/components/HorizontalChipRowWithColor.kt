package de.diskostu.spoolstack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import de.diskostu.spoolstack.data.ColorWithName
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import de.diskostu.spoolstack.ui.util.ColorUtils

/**
 * A specialized [HorizontalChipRow] that displays chips with custom background colors.
 */
@Composable
fun HorizontalChipRowWithColor(
    imageVector: ImageVector,
    colors: List<ColorWithName>,
    onColorHexSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalChipRow(
        imageVector = imageVector,
        items = colors,
        modifier = modifier
    ) { color ->
        val chipColor = ColorUtils.hexToColor(color.colorHex) ?: Color.Transparent
        val isLight = ColorUtils.isColorLight(chipColor)
        val textColor = if (isLight) Color.Black else Color.White

        FilterChip(
            selected = false,
            onClick = { onColorHexSelected(color.colorHex) },
            label = {
                Text(
                    text = color.name ?: color.colorHex,
                    color = textColor
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = chipColor,
                labelColor = textColor
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalChipRowWithColorPreview() {
    val sampleColors = listOf(
        ColorWithName("#FF0000", "Red"),
        ColorWithName("#00FF00", "Green"),
        ColorWithName("#0000FF", "Blue"),
        ColorWithName("#FFFFFF", "White"),
        ColorWithName("#000000", "Black")
    )

    SpoolstackTheme {
        Surface {
            HorizontalChipRowWithColor(
                imageVector = Icons.Default.Palette,
                colors = sampleColors,
                onColorHexSelected = {}
            )
        }
    }
}
