//package tech.xken.tripbook.ui.screens.universe
//
//import android.util.Log
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.State
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import tech.xken.tripbook.R
//import tech.xken.tripbook.ui.components.TownNodeItem
//import tech.xken.tripbook.ui.navigation.UniverseArgs
//
//@Composable
//fun UniverseVisualizer(
//    scaffoldState: ScaffoldState = rememberScaffoldState(),
//    viewModel: UniverseVisualizerVM = hiltViewModel(),
//    navigateToSearch: () -> Unit,
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    viewModel.addTowns()
//
//    val x: State<Pair<Int, List<String>>?> =
//        viewModel.savedStateHandle.getStateFlow(UniverseArgs.UNIV_SEARCH_RESULT, null).collectAsState()
//    Log.d("UNIVERSE SEARCH RES", x.value?.toString() ?: "")
//
//
//
//    Scaffold(modifier = Modifier.fillMaxSize(),
//        scaffoldState = scaffoldState,
//        floatingActionButton = {
//            UniverseToolbox(isVisible = { uiState.isToolboxVisible },
//                toggleVisibility = { viewModel.toggleToolboxVisibility() },
//                isFabTownAddVisible = { uiState.isFabTownAddVisible },
//                fabTownAddClick = { navigateToSearch() },
//                isFabRoadAddVisible = { uiState.isFabRoadAddVisible },
//                fabRoadAddClick = { /*TODO*/ },
//                isFabDetailsVisible = { uiState.isFabDeleteVisible },
//                fabDetailsClick = { /*TODO*/ },
//                isFabDeleteVisible = { uiState.isFabDeleteVisible },
//                fabDeleteClick = { /*TODO*/ },
//                isFabHelpVisible = { uiState.isFabHelpVisible },
//                fabHelpClick = { /*TODO*/ },
//                isLoading = { uiState.isLoading })
//        }
//    ) { paddingValues ->
//        BoxWithConstraints(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()//Pointer options for tapping
//                .background(MaterialTheme.colors.background)
//                .pointerInput(Unit) {//Dragging and scaling
//                    detectTransformGestures { _, pan, _, _ ->
//                        viewModel.onScreenDragged(pan)
//                    }
//                }
//                .pointerInput(Unit) {//Focus and highlights manager
//                    detectTapGestures(
//                        onTap = {
//                            viewModel.clearTownFocus()//We clear the focus town
//                            viewModel.clearSelectedTowns()
//                        }
//                    )
//                }
//                .graphicsLayer(
//                    translationX = uiState.translation.x,
//                    translationY = uiState.translation.y,
//                ),
//        ) {
//            if (uiState.message != "") {
//                Log.d("Univ Message", uiState.message)
//                viewModel.clearMessage()
//            }
//
//            //We display and draw the elements
//            uiState.towns.keys.forEach { id ->
//                TownNodeItem(
//                    node = { uiState.towns[id]!! },
//                    onDragged = { townId, by -> viewModel.updateNodePosition(townId, by) },
//                    onClick = { viewModel.onNodeClicked(id) },
//                    onLongClick = { viewModel.onNodeLongClicked(it) },
//                    onPinPointPositioned = { townId, position ->
//                        viewModel.onPinPointPositioned(townId,
//                            position)
//                    },
//                    isFocus = { townId -> uiState.townFocus == townId },
//                    isSelected = { townId -> uiState.selectedTowns.contains(townId) }
//                )
//            }
//
////            uiState.roads.keys.forEach { id ->
////                for (i in 0 until uiState.roads[id]!!.itineraryTownsIds.size) {
////                    val itinerary = uiState.roads[id]!!.itineraryTownsIds
////                    RoadLinkItem(
////                        point1 = {
////                            uiState.towns[itinerary[i]]!!.pinPoint
////                        },
////                        point2 = {
////                            uiState.towns[itinerary[i + 1]]!!.pinPoint
////                        },
////                        isMarked = {
////                            viewModel.run {
////                                isTownMarked(itinerary[i + 1]) || isTownMarked(itinerary[i + 1])
////                            }
////                        }
////                    )
////                }
////            }
//            //TODO: Show road items
//        }
//    }
//}
//
//@Composable
//fun UniverseToolbox(
//    isVisible: () -> Boolean,
//    isLoading: () -> Boolean,
//    toggleVisibility: () -> Unit,
//    fabTownAddClick: () -> Unit,
//    isFabTownAddVisible: () -> Boolean,
//    isFabRoadAddVisible: () -> Boolean,
//    fabRoadAddClick: () -> Unit,
//    isFabDetailsVisible: () -> Boolean,
//    fabDetailsClick: () -> Unit,
//    isFabDeleteVisible: () -> Boolean,
//    fabDeleteClick: () -> Unit,
//    isFabHelpVisible: () -> Boolean,
//    fabHelpClick: () -> Unit,
//    fabSize: Dp = 40.dp,
//    fabPadding: PaddingValues = PaddingValues(start = 0.dp, top = 8.dp),
//) {
//    Column(modifier = Modifier
//        .padding(top = 20.dp, end = 4.dp, start = 4.dp, bottom = 2.dp)
//        .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Bottom) {
//        AnimatedVisibility(isFabDeleteVisible()) {
//            FloatingActionButton(onClick = { fabDeleteClick() },
//                modifier = Modifier
//                    .padding(fabPadding)
//                    .size(fabSize), backgroundColor = MaterialTheme.colors.error
//            ) {
//                Icon(imageVector = Icons.Default.Delete,
//                    contentDescription = stringResource(R.string.dsc_delete),
//                    tint = MaterialTheme.colors.onError)
//            }
//        }
//        AnimatedVisibility(isFabDetailsVisible()) {
//            FloatingActionButton(onClick = { fabDetailsClick() },
//                modifier = Modifier
//                    .padding(fabPadding)
//                    .size(fabSize), backgroundColor = MaterialTheme.colors.primaryVariant) {
//                Icon(imageVector = Icons.Default.Info,
//                    contentDescription = stringResource(R.string.dsc_show_details),
//                    tint = MaterialTheme.colors.onPrimary)
//            }
//        }
//        AnimatedVisibility(isFabRoadAddVisible()) {
//            FloatingActionButton(onClick = { fabRoadAddClick() },
//                backgroundColor = MaterialTheme.colors.primary,
//                modifier = Modifier
//                    .padding(fabPadding)
//                    .size(fabSize)
//                    .rotate(90f)) {
//                Icon(imageVector = Icons.Default.Route,
//                    contentDescription = stringResource(R.string.dsc_connect_towns),
//                    tint = MaterialTheme.colors.onPrimary)
//            }
//        }
//        AnimatedVisibility(isFabTownAddVisible()) {
//            FloatingActionButton(onClick = { fabTownAddClick() },
//                backgroundColor = MaterialTheme.colors.primary,
//                modifier = Modifier
//                    .padding(fabPadding)
//                    .size(fabSize)) {
//                Icon(imageVector = Icons.Default.AddLocation,
//                    contentDescription = stringResource(R.string.dsc_add_town),
//                    tint = MaterialTheme.colors.onPrimary)
//            }
//        }
//
//        AnimatedVisibility(isFabHelpVisible()) {
//            FloatingActionButton(onClick = { fabHelpClick() },
//                backgroundColor = MaterialTheme.colors.secondary,
//                modifier = Modifier
//                    .padding(fabPadding)
//                    .size(fabSize)) {
//                Icon(imageVector = Icons.Default.Help,
//                    contentDescription = stringResource(R.string.dsc_help),
//                    tint = MaterialTheme.colors.onSecondary)
//            }
//        }
//        FloatingActionButton(onClick = { toggleVisibility() },
//            modifier = Modifier
//                .padding(fabPadding)
//                .size(fabSize)) {
//            Icon(imageVector = if (isVisible()) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                contentDescription = stringResource(R.string.dsc_visibility_fab_toggle))
//        }
//        AnimatedVisibility(visible = isLoading(), modifier = Modifier.padding(top = 4.dp)) {
//            CircularProgressIndicator()
//        }
//    }
//}