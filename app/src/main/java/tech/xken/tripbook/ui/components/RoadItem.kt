package tech.xken.tripbook.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.domain.caps


data class RoadItemUiState(
    val road: Road,
    val isSelected: Boolean,
    val showPath: Boolean,
    private val isReversed: Boolean
) {
    val from = if (isReversed) road.town2 else road.town1
    val to = if (isReversed) road.town1 else road.town2
    val fullPath = mutableListOf<Town>().apply {
        add(road.town1!!)
        addAll((if (isReversed) road.roadPathTowns!!.reversed() else road.roadPathTowns)!!)
        add(road.town2!!)
    }.toList()
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun RoadItem(
    uis: RoadItemUiState,
    onPathTownClick: (townId: String) -> Unit,
    onClick: (roadId: String) -> Unit,
    onLongClick: (roadId: String) -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = 8.dp
) {
    Card(
        modifier = modifier.combinedClickable(
            onLongClick = { onLongClick(uis.road.roadId) },
            onClick = { onClick(uis.road.roadId) }),
        elevation = elevation,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(8.dp).fillMaxWidth()
        ) {
            val (fromIconRef, toIconRef, fromRef, toRef, distanceRef, pathRef, divRef) = createRefs()

            Icon(
                imageVector = Icons.Outlined.ArrowCircleUp,
                contentDescription = stringResource(id = R.string.desc_my_location),
                modifier = Modifier
                    .padding(2.dp)
                    .constrainAs(fromIconRef) {
                        centerVerticallyTo(fromRef)
                        end.linkTo(fromRef.start)
                        start.linkTo(parent.start)
                    },
                tint = MaterialTheme.colors.primary
            )

            Text(
                text = uis.from!!.name!!.caps,
                modifier = Modifier
                    .padding(start = 2.dp, end = 4.dp)
                    .constrainAs(fromRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(divRef.top)
                        start.linkTo(parent.start)
                        end.linkTo(fromRef.start)
                    },
            )

            Icon(
                imageVector = Icons.Outlined.ArrowCircleDown,
                contentDescription = stringResource(id = R.string.desc_my_destination),
                modifier = Modifier
                    .padding(2.dp)
                    .constrainAs(toIconRef) {
                        centerVerticallyTo(toRef)
                        end.linkTo(toRef.start)
                        start.linkTo(fromRef.end, 4.dp)
                    },
                tint = MaterialTheme.colors.error
            )
            Text(
                text = uis.to!!.name!!.caps,
                modifier = Modifier
                    .padding(end = 2.dp, start = 4.dp)
                    .constrainAs(toRef) {
                        centerVerticallyTo(fromRef)
                        bottom.linkTo(divRef.top)
//                        centerVerticallyTo(parent)
                        start.linkTo(toIconRef.end)
//                        end.linkTo(distanceRef.start)

                    },
            )

            Text(
                text = "${uis.road.distance} km",
                modifier = Modifier
                    .padding(4.dp)
                    .constrainAs(distanceRef) {
                        centerVerticallyTo(fromRef)
                        end.linkTo(parent.end)
//                        width = Dimension.fillToConstraints
                    },
                fontSize = 12.sp
            )

            Divider(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .constrainAs(divRef) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(pathRef.top)
                        top.linkTo(fromRef.bottom)
                    }, thickness = if (uis.showPath) 1.dp else 0.dp
            )

            if (uis.showPath)
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .animateContentSize()
                        .constrainAs(pathRef) {
                            top.linkTo(divRef.bottom)
                            centerHorizontallyTo(parent)
                        },
                    columns = GridCells.Adaptive(100.dp)
                ) {
                    val n = uis.fullPath.lastIndex
                    itemsIndexed(uis.fullPath) { i, item ->
                        val color = ChipDefaults.chipColors(
                            backgroundColor = when (i) {
                                0 -> MaterialTheme.colors.primary
                                n -> MaterialTheme.colors.error
                                else -> MaterialTheme.colors.onSurface.copy(
                                    0.1f
                                )
                            },
                            contentColor = when (i) {
                                0 -> MaterialTheme.colors.onPrimary
                                n -> MaterialTheme.colors.onError
                                else -> MaterialTheme.colors.onSurface
                            },
                        )
                        Chip(
                            modifier = Modifier.padding(2.dp),
                            onClick = { onPathTownClick(item.townId) },
                            colors = color,
                        ) {

                            Icon(
                                imageVector = if (i != 0) Icons.Default.ArrowForward else Icons.Default.ArrowCircleUp,
                                contentDescription = null,
                                modifier = Modifier.weight(0.1f)
                            )

                            Text(
                                text = item.name!!.caps,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(0.8f, fill = true),
                                fontSize = 12.sp, softWrap = false
                            )

                            Icon(
                                imageVector = if (i != n) Icons.Default.ArrowForward else Icons.Default.ArrowCircleDown,
                                contentDescription = null,
                                modifier = Modifier.weight(0.1f)
                            )
                        }
                    }
                    // Iterate over the items and display them in the grid.
                }
        }
    }
}
