package de.diskostu.spoolstack.ui.components.animation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
        durationMillis = durationMillis,
        label = "VerticalSlide",
        transitionSpec = {
            val enter =
                slideInVertically(tween(durationMillis)) { it } + fadeIn(tween(durationMillis))
            val exit =
                slideOutVertically(tween(durationMillis)) { it } + fadeOut(tween(durationMillis))
            enter togetherWith exit
        },
        content = content
    )
}