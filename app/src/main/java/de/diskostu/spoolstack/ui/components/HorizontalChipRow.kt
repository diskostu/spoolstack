package de.diskostu.spoolstack.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

/**
 * A generic horizontal row that displays an icon followed by a list of chips.
 */
@Composable
fun <T> HorizontalChipRow(
    imageVector: ImageVector? = null,
    items: List<T>,
    modifier: Modifier = Modifier,
    chipContent: @Composable (T) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
        ) {
            items.forEach { item ->
                chipContent(item)
            }
        }
    }
}

/**
 * A specialized [HorizontalChipRow] that displays text chips with default Material 3 styling.
 */
@Composable
fun HorizontalTextChipRow(
    imageVector: ImageVector,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalChipRow(
        imageVector = imageVector,
        items = items,
        modifier = modifier
    ) { item ->
        FilterChip(
            selected = false,
            onClick = { onItemSelected(item) },
            label = { Text(text = item) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalTextChipRowPreview() {
    val sampleItems = listOf("Tag 1", "Another Tag", "M3 Expressive", "Spoolstack")

    SpoolstackTheme {
        Surface {
            HorizontalTextChipRow(
                imageVector = Icons.AutoMirrored.Filled.Label,
                items = sampleItems,
                onItemSelected = {}
            )
        }
    }
}
