package tech.xken.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import tech.xken.tripbook.data.models.Itinerary
import tech.xken.tripbook.data.models.TownNode
import tech.xken.tripbook.data.models.Town

/*@Composable
fun UniverseGrid(
    modifier: Modifier = Modifier,
) {
    val nodes = remember {
        mutableStateMapOf(
            "town1" to TownNode(
                town = Town("town1",
                    "Dschang",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null),
                offset = Offset(100f, 200f)
            ),
            "town2" to TownNode(
                town = Town("town2",
                    "Bafoussam",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null),
                offset = Offset(10f, 60f)
            ),
            "town3" to TownNode(
                town = Town("town3",
                    "Yaounde",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null),
            ),
            "town4" to TownNode(
                town = Town("town4",
                    "Gaproua",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null),
            ),
        )
    }
    val road = remember {
        Itinerary(
            roadID = "road1",
            start = "town1",
            stop = "town4",
            distance = 40.0,
            townPairs = hashMapOf("town1" to "town2", "town2" to "town3", "town3" to "town4")
        )
    }

    var translation by remember { mutableStateOf(Offset.Zero) }
    var focus by remember { mutableStateOf("") }
    var selectedNodes = remember { mutableStateListOf<String>() }

    fun onNodeLongClicked(id: String) {
        val index = selectedNodes.indexOf(id)
        if (index != -1) selectedNodes.removeAt(index)
        else selectedNodes.add(id)
    }

    *//**
     * If we are selecting, then clicking an element will instead select it
     *//*
    fun onNodeClicked(id: String) {
        if (selectedNodes.isEmpty())
            focus = if (focus != id) id else ""
        else
            onNodeLongClicked(id)
    }

    fun isNodeMarked(id: String) = selectedNodes.contains(id) || focus == id

    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, _, _ ->
                translation += pan
            }
        }
        .graphicsLayer(
            translationX = translation.x,
            translationY = translation.y,
        )
    ) {
        nodes.values.forEach { node ->
            NewTownItem(
                node = node,
                onClick = { id ->
                    onNodeClicked(id)
                },
                onLongClick = { id ->
                    onNodeLongClicked(id)
                },
                onDragged = { id, offset ->
                    nodes[id] = nodes[id]!!.copy(offset = nodes[id]!!.offset + offset)
                },
                onPositioned = { id, pinPoint ->
                    nodes[id] = nodes[id]!!.copy(pinPoint = pinPoint)
                },
                isFocus = { focus == node.town.id },
                isSelected = { selectedNodes.contains(node.town.id) },
            )
        }
        road.townPairs.forEach {
            RoadLinkItem(point1 = { (nodes[it.key]!!.pinPoint - translation) },
                point2 = { (nodes[it.value]!!.pinPoint - translation) },
                isMarked = isNodeMarked(it.key) || isNodeMarked(it.value))
        }

    }

}*/
