package tech.xken.tripbook.ui.screens.booking

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Person4
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.xken.tripbook.ui.screens.booking.BusCellType.DOOR
import tech.xken.tripbook.ui.screens.booking.BusCellType.DRIVER_SEAT
import tech.xken.tripbook.ui.screens.booking.BusCellType.EMPTY
import tech.xken.tripbook.ui.screens.booking.BusCellType.SEAT

// Types of cells we can find in a bus
enum class BusCellType {
    SEAT, DRIVER_SEAT, WINDOW, DOOR, EMPTY;
}


fun fillBus(): List<BusCellType> {
    val bus = mutableListOf<BusCellType>()
    bus += listOf(DOOR, EMPTY, EMPTY, EMPTY, EMPTY, DRIVER_SEAT)
    for (i in 1..13)
        bus += listOf(SEAT, SEAT, SEAT, EMPTY, SEAT, SEAT)
    bus += listOf(SEAT, SEAT, SEAT, SEAT, SEAT, SEAT)
    return bus
}

@Composable
fun BusVisual() {
    val bus = remember { fillBus() }
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
        modifier = Modifier.padding(8.dp)
    ) {

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .animateContentSize(),
            columns = GridCells.Fixed(6)
        ) {
            val n = bus.lastIndex
            var counter = 1
            itemsIndexed(bus) { i, item ->
                when (item) {
                    SEAT -> {
                        Column(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(45.dp)
                                .border(0.dp, MaterialTheme.colors.onSurface),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text("$counter", fontSize = 12.sp, modifier = Modifier.padding(4.dp))
                            counter += 1
                        }
                    }

                    DRIVER_SEAT -> {
                        Column(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(45.dp)
                                .border(0.dp, MaterialTheme.colors.onSurface),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person4,
                                contentDescription = null
                            )
                        }
                    }

                    DOOR -> Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(45.dp)
                            .border(0.dp, MaterialTheme.colors.onSurface),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }

                    else -> {}
                }

            }
            // Iterate over the items and display them in the grid.
        }
    }

}

@Composable
@Preview
fun BusVisualPreview(
){
    BusVisual()
}