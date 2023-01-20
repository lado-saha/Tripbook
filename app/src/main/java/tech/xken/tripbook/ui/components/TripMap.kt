package tech.xken.tripbook.ui.components////@file:OptIn(ExperimentalFoundationApi::class)
//package tech.xken.solar.tripbook
//
//import androidx.compose.animation.AnimatedContent
//import androidx.compose.animation.Crossfade
//import androidx.compose.animation.ExperimentalAnimationApi
//import androidx.compose.foundation.*
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.twotone.AutoGraph
//import androidx.compose.runtime.*
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.runtime.snapshots.SnapshotStateMap
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.layout.positionInParent
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstraintLayout
//import androidx.constraintlayout.compose.Dimension
//import tech.xken.tripbook.data.models.Link
//import tech.xken.tripbook.data.models.Node
//import kotlin.math.roundToInt
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun TownItem(
//    node: Node,
//    onPositionChange: (offset: Offset, fromBtnPos: Offset, toBtnPos: Offset) -> Unit,
//) {
//    var fromBtnPos by remember { mutableStateOf(node.fromBtnPos) }
//    var toBtnPos by remember { mutableStateOf(node.toBtnPos) }
//
//    Card(
//        modifier = Modifier
//            .wrapContentSize()
//            .padding(4.dp)
//            .offset {
//                IntOffset(node.offset.x.roundToInt(), node.offset.y.roundToInt())
//            }
//            .pointerInput(Unit) {
//                detectDragGestures { change, dragAmount ->
//                    change.consume()
//                    onPositionChange(dragAmount, fromBtnPos, toBtnPos)
//                }
//            },
//        shape = RoundedCornerShape(10),
//        onClick = { /*TODO*/ }, elevation = 8.dp
//    ) {
//        ConstraintLayout {
//            val (nameRef, toRef, fromRef, horizontalDivRef) = createRefs()
//            Text(
//                modifier = Modifier
//                    .constrainAs(nameRef) {
//                        top.linkTo(parent.top)
//                        centerHorizontallyTo(parent)
//                    }
//                    .padding(4.dp),
//                text = node.name,
//                textAlign = TextAlign.Center,
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp
//            )
//
//            Divider(
//                modifier = Modifier
//                    .constrainAs(horizontalDivRef) {
//                        centerHorizontallyTo(nameRef)
//                        top.linkTo(nameRef.bottom)
//                        width = Dimension.fillToConstraints
//
//                    }
//                    .padding(4.dp)
//            )
//
//            IconButton(
//                onClick = { /*TODO*/ }, modifier = Modifier
//                    .constrainAs(
//                        fromRef
//                    ) {
//                        start.linkTo(toRef.end)
//                        end.linkTo(parent.end)
//                        top.linkTo(nameRef.bottom)
//                        bottom.linkTo(parent.bottom)
//
//                    }
//                    .onGloballyPositioned {
//                        var (x, y) = it.positionInParent()
//                        x += (it.size.width) / 2f + 7f
//                        y += (it.size.height) / 2f + 7f
//                        fromBtnPos = Offset(x, y)
//                    }
//                    .padding(2.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Circle,
//                    contentDescription = "From",
//                    modifier = Modifier.size(8.dp)
//                )
//            }
//
//            IconButton(onClick = { /*TODO*/ },
//                modifier = Modifier
//                    .constrainAs(toRef) {
//                        start.linkTo(parent.start)
//                        end.linkTo(fromRef.start)
//                        top.linkTo(nameRef.bottom)
//                        bottom.linkTo(parent.bottom)
//                    }
//                    .onGloballyPositioned {
//                        var (x, y) = it.positionInParent()
//                        x += it.size.width / 2f + 7f
//                        y += it.size.height / 2f + 7f
//                        toBtnPos = Offset(x, y)
//                    }
//                    .padding(2.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Circle,
//                    contentDescription = "From",
//                    modifier = Modifier.size(8.dp)
//                )
//            }
//
//        }
//    }
//
//}
//
//@Composable
//fun RoadItem(
//    link: Link,
//) {
//    var isHighlighted by remember { mutableStateOf(false) }
//    Canvas(
//        modifier = Modifier.clickable {
//            isHighlighted = !isHighlighted
//        }
//    ) {
//        val (x1, y1) = link.node1.offset + link.node1.fromBtnPos
//        val (x2, y2) = link.node2.offset + link.node2.toBtnPos
//
//        val start = Offset(x1, y1)
//        val mid = Offset((x1 + x2) / 2f, (y1 + y2) / 2f)
//        // Bezier Point 1
//        val b1 = Offset(mid.x, start.y)
//        val end = Offset(x2, y2)
//        // Bezier Point 2
//        val b2 = Offset(mid.x, end.y)
//
//        drawPath(
//            path = Path().apply {
//                moveTo(start.x, start.y)
//                drawCircle(color = Color.Black, 5f, center = start)
//                quadraticBezierTo(b1.x, b1.y, mid.x, mid.y)
//                drawCircle(color = Color.Black, 5f, center = mid)
//                quadraticBezierTo(b2.x, b2.y, end.x, end.y)
//                drawCircle(color = Color.Black, 5f, center = end)
//            },
//            color = Color.Black,
//            style = Stroke(5f)
//        )
//    }
//}
//
//@Composable
//@Preview
//fun TownPrev() {
//    UniverseEditor()
//}
//
//
//
//@OptIn(ExperimentalAnimationApi::class)
//@Composable
//fun UniverseEditor(
//    tripMap: TripMapUiState = TripMapUiState(),
//) {
//    //All uistates
//    var nodeSelection: SnapshotStateList<String> = remember { mutableStateListOf() }
//    var linkSelection: SnapshotStateList<String> = remember { mutableStateListOf() }
//    var nodeCursor by remember { mutableStateOf("") }
//    var linkCursor by remember { mutableStateOf("") }
//
//    var nodes: SnapshotStateMap<String, Node> = remember { mutableStateMapOf() }
//    var links: SnapshotStateMap<String, Link> = remember { mutableStateMapOf() }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    var scale by remember { mutableStateOf(1f) }
//    var fabState by remember { mutableStateOf(FabState.NORMAL) }
//
//    var center by remember { mutableStateOf(Offset.Zero) }
//
//    var fabSize by remember { mutableStateOf(48.dp) }
//    var cameraDistance by remember { mutableStateOf(200f) }
//
//    ConstraintLayout(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        val (toolboxRef, mapRef) = createRefs()
//        BoxWithConstraints(
//            modifier = Modifier
//                .constrainAs(mapRef) {
//                    centerVerticallyTo(parent)
//                    centerHorizontallyTo(parent)
//                    width = Dimension.fillToConstraints
//                    height = Dimension.fillToConstraints
//                }
//                .fillMaxSize()
//                .graphicsLayer(
//                    scaleX = scale,
//                    scaleY = scale,
//                    translationX = offset.x,
//                    translationY = offset.y,
//                    cameraDistance = cameraDistance,
//                    clip = false,
//                )
//                //Pointer input for scaling and dragging
//                .pointerInput(Unit) {
//                    detectTransformGestures { _, pan, zoom, _ ->
//                        scale *= zoom
//                        offset += pan
//                    }
//                }
//                //Pointer options for tapping
//                .pointerInput(Unit) {
//                    detectTapGestures(
//                        onTap = {
//                            //Loose all focused from children
//                            nodeCursor = ""
//                            linkSelection.clear()
//                            nodeSelection.clear()
//                            linkCursor = ""
//                        }
//                    )
//                }
//        ) {
//            center = Offset(this.maxWidth.value / 2, maxHeight.value / 2f)
//
//            nodes.forEachIndexed { i, oldNode ->
//                TownItem(
//                    oldNode,
//                    onPositionChange = { dragAmt, fromPos, toPos ->
//                        //We set the from and to button coordinates only once (At the start)
//                        nodes[i] = nodes[i].copy(
//                            offset = nodes[i].offset + dragAmt,
//                            fromBtnPos = fromPos,
//                            toBtnPos = toPos
//                        )
//                        links = links.apply {
//                            this[0] = Link(
//                                node1 = nodes[0],
//                                node2 = nodes[1],
//                            )
//                            this[1] = Link(
//                                node1 = nodes[1],
//                                node2 = nodes[2]
//                            )
//                        }
//                    }
//                )
//            }
//            links.forEach { link ->
//                RoadItem(link = link)
//            }
//        }
//
//        AnimatedContent(
//            targetState = fabState,
//            modifier = Modifier
//                .padding(vertical = 2.dp)
//                .constrainAs(toolboxRef) {
//                    centerVerticallyTo(parent, 1f)
//                    centerHorizontallyTo(parent, 0.99f)
//                },
//        ) {
//            Column(
//                modifier = Modifier
//                    .verticalScroll(state = rememberScrollState())
//            ) {
//
//                if (!nodeCursor.isBlank())
//
//
//                    if (it == FabState.NORMAL) {
//                        FloatingActionButton(
//                            onClick = { /*Add node*/ },
//                            modifier = Modifier.size(fabSize)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Route,
//                                contentDescription = "Add Town",
//                                modifier = Modifier.rotate(90f)
//                            )
//                        }
//                        FloatingActionButton(
//                            onClick = { /*TODO*/ },
//                            modifier = Modifier.size(fabSize)
//                        ) {
//                            Icon(
//                                imageVector = Icons.TwoTone.AutoGraph,
//                                contentDescription = "Add Town",
//                                modifier = Modifier.rotate(90f)
//                            )
//                        }
//                        FloatingActionButton(
//                            onClick = { /*TODO*/ },
//                            modifier = Modifier.size(fabSize)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Description,
//                                contentDescription = "Get Details"
//                            )
//                        }
//                    }
//
//                FabVisibilityToggle(
//                    isVisible = fabState == FabState.NORMAL,
//                    onClick = {
//                        fabState = if (it) FabState.HIDDEN else FabState.NORMAL
//                    },
//                    isVisibleIcon = Icons.Default.Visibility,
//                    isNotVisibleIcon = Icons.Default.VisibilityOff,
//                    modifier = Modifier
//                        .size(fabSize)
//                )
//            }
//        }
//    }
//}
//
///*
//@Preview
//@Composable
//fun PreviewTown() {
//    var map by remember {
//        mutableStateOf(Map())
//    }
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    val state = rememberTransformableState { zoomChange, panChange, _ ->
//        offset += panChange
//        scale *= zoomChange
//    }
//
//    BoxWithConstraints(
//        modifier = Modifier
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale,
//                translationX = offset.x,
//                translationY = offset.y,
//            )
//            .transformable(state = state)
////            .offset {
////                IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
////            }
////            .pointerInput(Unit) {
////                detectDragGestures { change, dragAmount ->
////                    change.consume()
////                    offset += dragAmount
////                }
////            }
//            .fillMaxSize(),
////        contentAlignment = Alignment.Center
//    ) {
//        var nodes = remember {
//            mutableStateListOf(
//                Node(
//                    Town(name = "Dschang", region = "West "),
//                    Offset.Zero
//                ),
//                Node(
//                    Town(name = "Yaounde", region = "Center "),
//                    Offset.Zero
//                ),
//                Node(
//                    Town(name = "Bafoussam", region = "Center "),
//                    Offset.Zero
//                ),
//                Node(
//                    Town(name = "Douala", region = "Littoral "),
//                    Offset.Zero
//                )
//            )
//        }
//        var links = remember {
//            mutableStateListOf(
//                Link(
//                    node1 = nodes[0],
//                    node2 = nodes[1],
//
//                    ),
//                Link(
//                    node1 = nodes[1],
//                    node2 = nodes[2],
//                ),
//                Link(
//                    node1 = nodes[2],
//                    node2 = nodes[3],
//                )
//            )
//        }
//
//        nodes.forEachIndexed { i, oldNode ->
//            TownItem(oldNode) {
//                nodes[i] = nodes[i].copy(offset = nodes[i].offset + it)
//                links = links.apply {
//                    this[0] = Link(
//                        node1 = nodes[0],
//                        node2 = nodes[1],
//                    )
//                    this[1] = Link(
//                        node1 = nodes[1],
//                        node2 = nodes[2],
//                    )
//                    this[2] = Link(
//                        node1 = nodes[2],
//                        node2 = nodes[3],
//                    )
////                    this[3] = RoadLink(
////                        node1 = nodes[3],
////                        node2 = nodes[1],
////                    )
//                }
//            }
//        }
//
//        links.forEach { link ->
//            RoadItem(link = link)
//        }
//    }
//}*/
