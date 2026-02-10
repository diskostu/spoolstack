package de.diskostu.spoolstack.ui.components.animation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> HorizontalSlideAnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMillis: Int = 200,
    content: @Composable (T) -> Unit
) {
    GenericSlideAnimatedContent(
        targetState = targetState,
        modifier = modifier,
        label = "HorizontalSlide",
        transitionSpec = {
            slideFadeSpec(
                durationMillis = durationMillis,
                enterSlide = slideInHorizontally(tween(durationMillis)) { it },
                exitSlide = slideOutHorizontally(tween(durationMillis)) { it }
            )
        },
        content = content
    )
}
