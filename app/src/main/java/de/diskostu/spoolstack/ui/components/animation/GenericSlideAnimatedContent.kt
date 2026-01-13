package de.diskostu.spoolstack.ui.components.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Base component for slide animations to avoid code duplication.
 */
@Composable
fun <T> GenericSlideAnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMillis: Int = 100,
    // Defines how the new content enters and old content exits
    transitionSpec: () -> ContentTransform,
    label: String,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = { transitionSpec() },
        label = label
    ) { state ->
        content(state)
    }
}

/**
 * Common fade spec used in all slide animations.
 */
fun <T> slideFadeSpec(durationMillis: Int) =
    fadeIn(animationSpec = tween(durationMillis)) togetherWith
            fadeOut(animationSpec = tween(durationMillis))