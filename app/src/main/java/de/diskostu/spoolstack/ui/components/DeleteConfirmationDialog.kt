package de.diskostu.spoolstack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
 * A reusable confirmation dialog for deleting filaments.
 *
 * @param onConfirm Callback when the delete button is pressed.
 * @param onDismiss Callback when the dialog is dismissed or cancel is pressed.
 * @param message The message to show in the dialog.
 * @param title The title of the dialog. Defaults to R.string.delete_confirmation_title.
 * @param confirmButtonText The text for the confirm button. Defaults to R.string.delete.
 * @param dismissButtonText The text for the dismiss button. Defaults to R.string.cancel.
 * @param icon The icon to show. Defaults to Icons.Default.Delete.
 */
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String,
    title: String? = null,
    confirmButtonText: String? = null,
    dismissButtonText: String? = null,
    icon: ImageVector = Icons.Default.Delete
) {
    // Resolve default strings inside the body to avoid potential preview issues with stale R classes
    // when using stringResource in default parameters.
    val actualTitle = title ?: stringResource(R.string.delete_confirmation_title)
    val actualConfirmButtonText = confirmButtonText ?: stringResource(R.string.delete)
    val actualDismissButtonText = dismissButtonText ?: stringResource(R.string.cancel)

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
                text = actualTitle,
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
                Text(text = actualConfirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = actualDismissButtonText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteConfirmationDialogPreview() {
    SpoolstackTheme {
        DeleteConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
            message = "Are you sure you want to delete this filament?"
        )
    }
}

@Preview(showBackground = true, device = "spec:width=600dp,height=480dp,orientation=landscape")
@Composable
private fun DeleteConfirmationDialogLandscapePreview() {
    SpoolstackTheme {
        DeleteConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
            message = "Are you sure you want to delete this filament? This message is a bit longer to see the effect of the wider dialog in landscape mode."
        )
    }
}
