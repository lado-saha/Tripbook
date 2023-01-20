package tech.xken.tripbook.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun RoadLinkItem(
    point1: () -> Offset,
    point2:() -> Offset,
    isMarked:() -> Boolean = {false},
) {
    val pathColor = MaterialTheme.colors.onBackground
    val backgroundColor = MaterialTheme.colors.background
    Canvas(
        modifier = Modifier
            .graphicsLayer()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->   }
            }
    ) {
        val (x1, y1) = point1()
        val (x2, y2) = point2()

        val start = Offset(x1, y1)
        val mid = Offset((x1 + x2) / 2f, (y1 + y2) / 2f)
        // Bezier Point 1
        val b1 = Offset(mid.x, start.y)
        val end = Offset(x2, y2)
        // Bezier Point 2
        val b2 = Offset(mid.x, end.y)

        drawPath(
            path = Path().apply {
                moveTo(start.x, start.y)
                drawCircle(color = pathColor, 5f, center = start)
                quadraticBezierTo(b1.x, b1.y, mid.x, mid.y)
                if (isMarked()) {
                    drawCircle(color = pathColor, 7f, center = mid)
                    drawCircle(color = backgroundColor, 5f, center = mid)
                }
                quadraticBezierTo(b2.x, b2.y, end.x, end.y)
                drawCircle(color = pathColor, 5f, center = end)
            },
            color = pathColor,
            style = Stroke(if (isMarked()) 5f else 2f),
        )
    }
}


