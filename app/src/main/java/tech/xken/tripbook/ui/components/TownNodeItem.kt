package tech.xken.tripbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import tech.xken.tripbook.data.models.TownNode

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TownNodeItem(
    node: () -> TownNode,
//    offset: () -> Offset,
    onClick: (id: String) -> Unit,
    onLongClick: (id: String) -> Unit,
    onDragged: (id: String, dragAmount: Offset) -> Unit,
    modifier: Modifier = Modifier,
    onPinPointPositioned: (id: String, pinPoint: Offset) -> Unit,
    isFocus: (id: String) -> Boolean,
    isSelected: (id: String) -> Boolean,
) {

    val placeSizeDp = 40.dp
    val otherIconSizeDp = placeSizeDp * (39f / 64f)

    ConstraintLayout(
        modifier = modifier
            .graphicsLayer(translationX = node().offset.x, translationY = node().offset.y)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ -> onDragged(node().town.id, pan) }
            }
    ) {
        val (markedIconRef, placeIconRef, currentIconRef, textRef, pinPointRef) = createRefs()

        Icon(imageVector = Icons.Filled.Place,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(placeIconRef) {
                    centerTo(parent)
                }
                .size(placeSizeDp)
                .combinedClickable(
                    onLongClick = { onLongClick(node().town.id) },
                    onClick = { onClick(node().town.id) },
                ), tint = MaterialTheme.colors.onBackground
        )

        //Current Place
        AnimatedVisibility(visible = isFocus(node().town.id),
            modifier = Modifier.constrainAs(currentIconRef) {
                centerHorizontallyTo(placeIconRef)
                centerVerticallyTo(placeIconRef, 0.2f)
            }) {
            Icon(imageVector = Icons.Outlined.MyLocation,
                contentDescription = null,
                modifier = Modifier
                    .size(otherIconSizeDp)
                    .background(MaterialTheme.colors.background, CircleShape),
                tint = MaterialTheme.colors.onBackground
            )
        }

        //Marked place
        AnimatedVisibility(visible = isSelected(node().town.id),
            modifier = Modifier.constrainAs(markedIconRef) {
                centerHorizontallyTo(placeIconRef)
                centerVerticallyTo(placeIconRef, 0.2f)
            }) {
            Icon(imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(otherIconSizeDp)
                    .background(MaterialTheme.colors.background, CircleShape),
                tint = MaterialTheme.colors.onBackground
            )
        }

        Text(
            modifier = Modifier.constrainAs(textRef) {
                centerHorizontallyTo(placeIconRef)
                bottom.linkTo(placeIconRef.top)
            },
            text = node().town.name!!,
            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.onBackground,
                fontWeight = if (isFocus(node().town.id) || isSelected(node().town.id)) FontWeight.ExtraBold else null),
        )

        Text(text = "", modifier = Modifier
            .constrainAs(pinPointRef) {
                centerHorizontallyTo(placeIconRef)
                centerVerticallyTo(placeIconRef, 1f)
            }
            .size(0.dp)
            .onPlaced {
                onPinPointPositioned(node().town.id, it.positionInRoot())
            }
        )
    }
}


/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TownNodeItem(
    node: TownNode,
    onFromBtnPlaced: (Offset) -> Unit,
    onToBtnPlaced: (Offset) -> Unit,
    onDragged: (dragAmount: Offset) -> Unit,
    onClick: (id: String) -> Unit,
    onLongClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .offset {
                IntOffset(node.offset.x.roundToInt(), node.offset.y.roundToInt())
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    onDragged(pan)
                }
            }
            .padding(0.dp),
        elevation = if (node.isSelected || node.isFocus) 10.dp else 1.dp,
        shape = RoundedCornerShape(100),
        border = when {
            node.isFocus -> BorderStroke(2.dp,
                Brush.horizontalGradient(
                    listOf(Color.Green,
                        Color.Yellow,
                        Color.Red))
            )
            node.isSelected -> BorderStroke(2.dp, MaterialTheme.colors.onBackground)
            else -> null
        }
    ) {
        ConstraintLayout(
            modifier = Modifier.combinedClickable(
                onLongClick = {
                    onLongClick(node.town.id)
                },
                onClick = {
                    onClick(node.town.id)
                }
            )
        ) {
            val (nameRef, toRef, fromRef, isHighlightedRef) = createRefs()
            if (node.isSelected || node.isFocus)
                Icon(
                    imageVector = if (node.isSelected) Icons.Filled.CheckCircle else Icons.Filled.MyLocation,
                    contentDescription = "",
                    modifier = Modifier.constrainAs(isHighlightedRef) {
                        start.linkTo(toRef.end)
                        end.linkTo(fromRef.start)
                        bottom.linkTo(toRef.bottom)
                        top.linkTo(fromRef.top)
                    }
                )
            Text(
                modifier = Modifier
                    .constrainAs(nameRef) {
                        top.linkTo(parent.top)
                        centerHorizontallyTo(parent)
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                text = node.town.name!!,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            IconButton(
                onClick = { */
/*TODO*//*
 }, modifier = Modifier
                    .constrainAs(fromRef) {
                        start.linkTo(
                            if (node.isFocus || node.isSelected) isHighlightedRef.end else toRef.end
                        )
                        end.linkTo(parent.end)
                        top.linkTo(nameRef.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .onGloballyPositioned {
                        var (x, y) = it.positionInWindow()
                        x += (it.size.width) / 2f + 7f
                        y += (it.size.height) / 2f + 7f
                        onFromBtnPlaced(Offset(x, y))
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "From",
                    modifier = Modifier.size(8.dp)
                )
            }

            IconButton(onClick = { */
/*TODO*//*
 },
                modifier = Modifier
                    .constrainAs(toRef) {
                        start.linkTo(parent.start)
                        end.linkTo(
                            if (node.isFocus || node.isSelected) isHighlightedRef.start else fromRef.start
                        )
                        top.linkTo(nameRef.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .onGloballyPositioned {
                        var (x, y) = it.positionInParent()
                        x += it.size.width / 2f + 7f
                        y += it.size.height / 2f + 7f
                        onToBtnPlaced(Offset(x, y))
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "From",
                    modifier = Modifier.size(8.dp)
                )
            }
        }
    }
}
*/
