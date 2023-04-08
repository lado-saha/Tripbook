package tech.xken.tripbook.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AirlineSeatFlat
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.Window
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import tech.xken.tripbook.data.models.CellType.*

data class Cell(
    val x: Int,
    val y: Int,
    val type: CellType,
    val isVisible: Boolean = true,
) {
    val logo = when (type) {
        SEAT -> Icons.Outlined.AirlineSeatFlat
        WINDOW -> Icons.Outlined.Window
        DOOR -> Icons.Outlined.DoorFront
        PATH -> null
    }

    companion object {

    }
}

//fun drawLargeBus(): List<Cell> {
//    val bus = mutableListOf<Cell>()
//    //Front row
//    bus += listOf(0, 7).map { x -> Cell(x, 0, DOOR) }
//    bus += listOf(1, 5, 6).map { x -> Cell(x, 0, SEAT) }
//    bus += (2..4).map { x -> Cell(x, 0, PATH) }
//}

enum class CellType {
    SEAT, WINDOW, DOOR, PATH,
}