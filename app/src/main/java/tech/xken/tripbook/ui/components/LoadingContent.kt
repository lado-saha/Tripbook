package tech.xken.tripbook.ui.components

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param loading (state) when true, display a loading spinner over [content]
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param onRefresh (event) event to request refresh
 * @param modifier the modifier to apply to this layout.
 * @param content (slot) the main content to show
 */
@Composable
fun LoadingContent(
    percentProgress: Double = Double.NaN,
    modifier: Modifier = Modifier,
) {
    if (percentProgress.isNaN()) CircularProgressIndicator(modifier = modifier)
    else CircularProgressIndicator(progress = percentProgress.toFloat() / 100f, modifier = modifier)
}
