package tech.xken.tripbook.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import tech.xken.tripbook.data.models.Itinerary
import tech.xken.tripbook.data.models.MasterUI
import tech.xken.tripbook.data.models.TownNode
import kotlin.math.roundToInt

/*@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NodeItem(
    node: TownNode,
    onNodeChange: (new: TownNode) -> Unit,
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(4.dp)
            .offset {
                IntOffset(node.offset.x.roundToInt(), node.offset.y.roundToInt())
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    Log.d("NodeItem", "Dragged $node by: $dragAmount")
                    change.consume()
                    onNodeChange(
                        node.copy(
                            offset = node.offset + dragAmount,
                        )
                    )
                }
            },
        shape = RoundedCornerShape(10),
        onClick = { *//*TODO*//* }, elevation = 8.dp
    ) {
        ConstraintLayout {
            val (nameRef, toRef, fromRef, horizontalDivRef) = createRefs()
            Text(
                modifier = Modifier
                    .constrainAs(nameRef) {
                        top.linkTo(parent.top)
                        centerHorizontallyTo(parent)
                    }
                    .padding(4.dp),
                text = node.town.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Divider(
                modifier = Modifier
                    .constrainAs(horizontalDivRef) {
                        centerHorizontallyTo(nameRef)
                        top.linkTo(nameRef.bottom)
                        width = Dimension.fillToConstraints

                    }
                    .padding(4.dp)
            )

            IconButton(
                onClick = { *//*TODO*//* }, modifier = Modifier
                    .constrainAs(fromRef) {
                        start.linkTo(toRef.end)
                        end.linkTo(parent.end)
                        top.linkTo(nameRef.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .onGloballyPositioned {
                        var (x, y) = it.positionInParent()
                        x += (it.size.width) / 2f + 7f
                        y += (it.size.height) / 2f + 7f
                        onNodeChange(node.copy(fromBtnPos = Offset(x, y)))
                    }
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "From",
                    modifier = Modifier.size(8.dp)
                )
            }

            IconButton(onClick = { *//*TODO*//* },
                modifier = Modifier
                    .constrainAs(toRef) {
                        start.linkTo(parent.start)
                        end.linkTo(fromRef.start)
                        top.linkTo(nameRef.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .onGloballyPositioned {
                        var (x, y) = it.positionInParent()
                        x += it.size.width / 2f + 7f
                        y += it.size.height / 2f + 7f
                        onNodeChange(node.copy(fromBtnPos = Offset(x, y)))
                    }
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "From",
                    modifier = Modifier.size(8.dp)
                )
            }

        }
    }

}

*//**
 * Draws the different roads through towns
 * @param itinerary is the [Itinerary] object i.e a physical Road passing through many towns [Itinerary.townPairs]
 * @param onLinkChange Used by this function to update the link; especially when is the link is highlighted
 * @param nodes Is the list of all [TownNode]s which are required by not only the current road, but others
 * @param masterUI Is from where we get the position of each nodes
 *//*
@Composable
fun LinkItem(
    itinerary: Itinerary,
    onLinkChange: (new: Itinerary) -> Unit,
    nodes: HashMap<String, TownNode>,
    masterUI: MasterUI,
) {
    itinerary.townPairs.forEach {
        val fromID = remember { it.key }
        val toID = remember { it.value }
        val fromNode = remember { nodes[fromID]!! }
        val toNode = remember { nodes[toID]!! }
        val fromOffset = remember { masterUI.items.value[fromID]!!.offset }
        val toOffset = remember { masterUI.items.value[toID]!!.offset }

        Canvas(
            modifier = Modifier.clickable {
                onLinkChange(itinerary.copy(isHighlighted = !itinerary.isHighlighted))
            }
        ) {
            val (x1, y1) = fromOffset + fromNode.fromBtnPos
            val (x2, y2) = toOffset + toNode.toBtnPos

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
                    drawCircle(color = Color.Black, 5f, center = start)
                    quadraticBezierTo(b1.x, b1.y, mid.x, mid.y)
                    drawCircle(color = Color.Black, 5f, center = mid)
                    quadraticBezierTo(b2.x, b2.y, end.x, end.y)
                    drawCircle(color = Color.Black, 5f, center = end)
                },
                color = Color.Black,
                style = Stroke(5f)
            )
        }
    }
}*/

