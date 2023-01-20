package tech.xken.tripbook.data.models.uistates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tech.xken.tripbook.data.models.Itinerary
import tech.xken.tripbook.data.models.MasterUI
import tech.xken.tripbook.data.models.TownNode
import tech.xken.tripbook.data.models.VisibilityFabState

data class UniverseEditorUiS(
    val masterUI: MasterUI = MasterUI(),
    val nodes: HashMap<String, TownNode> = hashMapOf(),
    val itinerary: Itinerary = Itinerary(),
    val nodeSelection: List<String> = listOf(),
    val linkSelection: List<String> = listOf(),
    val nodeCursor: String = "",
    val linkCursor: String = "",
    val fabState: VisibilityFabState = VisibilityFabState.NORMAL,
    val fabSize: Dp = 48.dp,
    val screenCenter: Offset = Offset.Zero,
) {

    companion object {
        @Composable
        fun rememberUiS(
            masterUI: MasterUI = MasterUI(),
            nodes: HashMap<String, TownNode> = hashMapOf(),
            itinerary: Itinerary = Itinerary(),
            nodeSelection: List<String> = listOf(),
            linkSelection: List<String> = listOf(),
            nodeCursor: String = "",
            linkCursor: String = "",
            fabState: VisibilityFabState = VisibilityFabState.NORMAL,
            fabSize: Dp = 48.dp,
            screenCenter: Offset = Offset.Zero,
        ) = remember(
            masterUI,
            nodes,
            itinerary,
            nodeSelection,
            linkSelection,
            nodeCursor,
            linkCursor,
            fabSize,
            fabState,
            screenCenter
        ) {
            UniverseEditorUiS(
                masterUI = masterUI,
                nodes = nodes,
                itinerary = itinerary,
                nodeSelection = nodeSelection,
                linkSelection = linkSelection,
                nodeCursor = nodeCursor,
                linkCursor = linkCursor,
                fabSize = fabSize,
                fabState = fabState,
                screenCenter = screenCenter
            )
        }

        @Composable
        fun rememberUiS(uiS: UniverseEditorUiS) = rememberUiS(
            uiS.masterUI,
            nodes = uiS.nodes,
            itinerary = uiS.itinerary,
            nodeSelection = uiS.nodeSelection,
            linkSelection = uiS.linkSelection,
            nodeCursor = uiS.nodeCursor,
            linkCursor = uiS.linkCursor,
            fabState = uiS.fabState,
            fabSize = uiS.fabSize,
            screenCenter = uiS.screenCenter
        )
    }
}

