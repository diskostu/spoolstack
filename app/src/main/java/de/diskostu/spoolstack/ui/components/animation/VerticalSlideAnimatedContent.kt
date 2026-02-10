package de.diskostu.spoolstack.ui.components.animation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> VerticalSlideAnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMillis: Int = 100,
    content: @Composable (T) -> Unit
) {
    GenericSlideAnimatedContent(
        targetState = targetState,
        modifier = modifier,
        label = "VerticalSlide",
        transitionSpec = {
            slideFadeSpec(
                durationMillis = durationMillis,
                enterSlide = slideInVertically(tween(durationMillis)) { it },
                exitSlide = slideOutVertically(tween(durationMillis)) { it }
            )
        },
        content = content
    )
}
