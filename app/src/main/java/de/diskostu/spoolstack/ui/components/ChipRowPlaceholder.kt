package de.diskostu.spoolstack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A generic placeholder for chip rows or similar horizontal content.
 * Provides a subtle visual hint that content is loading or will appear here.
 */
@Composable
fun ChipRowPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 32.dp,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                // Use surfaceVariant with alpha for a subtle, non-intrusive appearance
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = shape
            )
    )
}