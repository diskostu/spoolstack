package de.diskostu.spoolstack.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.diskostu.spoolstack.BuildConfig

@Composable
fun DebugButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    if (BuildConfig.DEBUG) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = modifier
        ) {
            Text(text)
        }
    }
}
