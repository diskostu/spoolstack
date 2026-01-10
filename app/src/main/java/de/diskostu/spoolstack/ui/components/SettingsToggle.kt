package de.diskostu.spoolstack.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.diskostu.spoolstack.R

@Composable
fun SettingsToggle(
    isSettingsActive: Boolean,
    onMainClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Width of one item is 64dp, spacer is 4dp. Total width is 64+4+64 = 132dp + padding.
    // The selection pill offset: 0 if main, 68dp if settings.
    val indicatorOffset by animateDpAsState(
        targetValue = if (isSettingsActive) 68.dp else 0.dp,
        animationSpec = spring(stiffness = 500f),
        label = "indicatorOffset"
    )

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = CircleShape,
        tonalElevation = 6.dp
    ) {
        Box(modifier = Modifier.padding(6.dp)) {
            // Selection indicator (the pill)
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .size(64.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Paintbrush icon (Main)
                Box(
                    modifier = Modifier
                        .size(64.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onMainClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (!isSettingsActive)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Settings icon
                Box(
                    modifier = Modifier
                        .size(64.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        modifier = Modifier.size(20.dp),
                        tint = if (isSettingsActive)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
