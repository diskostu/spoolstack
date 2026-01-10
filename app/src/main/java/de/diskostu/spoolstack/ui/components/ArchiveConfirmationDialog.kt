package de.diskostu.spoolstack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme

/**
 * A reusable confirmation dialog for archiving filaments.
 *
 * @param onConfirm Callback when the archive button is pressed.
 * @param onDismiss Callback when the dialog is dismissed or cancel is pressed.
 * @param message The message to show in the dialog.
 * @param title The title of the dialog. Defaults to archive_confirmation_title.
 * @param confirmButtonText The text for the confirm button. Defaults to archive.
 * @param dismissButtonText The text for the dismiss button. Defaults to cancel.
 * @param icon The icon to show. Defaults to Icons.Default.Archive.
 */
@Composable
fun ArchiveConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String,
    title: String = stringResource(R.string.archive_confirmation_title),
    confirmButtonText: String = stringResource(R.string.archive),
    dismissButtonText: String = stringResource(R.string.cancel),
    icon: ImageVector = Icons.Default.Archive
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Material 3 Expressive guidelines suggest wider dialogs for larger screens/landscape.
    // 560dp is a recommended width for large-screen/landscape dialogs to improve readability.
    val dialogModifier = if (isLandscape) {
        Modifier.widthIn(min = 560.dp, max = 560.dp)
    } else {
        Modifier
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = dialogModifier,
        properties = DialogProperties(usePlatformDefaultWidth = !isLandscape),
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        title = {
            // Center title text
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            // Center message text
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissButtonText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ArchiveConfirmationDialogPreview() {
    SpoolstackTheme {
        ArchiveConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
            message = "Are you sure you want to archive this filament?"
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=480dp,orientation=landscape")
@Composable
private fun ArchiveConfirmationDialogLandscapePreview() {
    SpoolstackTheme {
        ArchiveConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
            message = "Are you sure you want to archive this filament? This message is a bit longer to see the effect of the wider dialog in landscape mode."
        )
    }
}
