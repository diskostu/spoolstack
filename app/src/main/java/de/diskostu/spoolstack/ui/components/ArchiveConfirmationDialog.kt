package de.diskostu.spoolstack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
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
