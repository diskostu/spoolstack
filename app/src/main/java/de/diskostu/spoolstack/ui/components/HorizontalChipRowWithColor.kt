package de.diskostu.spoolstack.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.diskostu.spoolstack.data.ColorWithName
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import de.diskostu.spoolstack.ui.util.ColorUtils

@Composable
fun HorizontalChipRowWithColor(
    imageVector: ImageVector,
    colors: List<ColorWithName>,
    onColorHexSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        colors.forEach { color ->
            val chipColor =
                ColorUtils.hexToColor(color.colorHex) ?: Color.Transparent
            val isLight = ColorUtils.isColorLight(chipColor)

            FilterChip(
                selected = false,
                onClick = { onColorHexSelected(color.colorHex) },
                label = {
                    Text(
                        text = color.name ?: color.colorHex,
                        color = if (isLight) Color.Black else Color.White
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = chipColor,
                    labelColor = if (isLight) Color.Black else Color.White
                )
            )
        }
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
