package tech.xken.tripbook.ui.screens.universe

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.ui.components.LoadingContent

@Composable
fun UniverseEditor(
    modifier: Modifier = Modifier,
    viewModel: UniverseEditorVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsState()
        val townsArray = stringArrayResource(id = R.array.universe)
        val roadsArray = stringArrayResource(id = R.array.roads)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // Shows loading spinner
            if (uiState.isLoading)
                LoadingContent(modifier = Modifier, percentProgress = uiState.percentProgression)

            if (uiState.currentItem.isNotBlank()) Text(uiState.currentItem,
                modifier = Modifier.fillMaxWidth())

            Button(onClick = { viewModel.saveUniverse(townsArray) },
                modifier = Modifier.padding(2.dp)) {
                Icon(imageVector = Icons.Default.Add, "Adding towns")
                Text("Add towns")
            }
            Button(onClick = { viewModel.getTowns() }, modifier = Modifier.padding(2.dp)) {
                Icon(imageVector = Icons.Default.Place, "Getting towns")
                Text("Get towns")
            }
            Button(onClick = { viewModel.addRoads(roadsArray) },
                modifier = Modifier.padding(2.dp)) {
                Icon(imageVector = Icons.Default.Add, "Adding roads")
                Text("Add roads")
            }
            Button(onClick = { viewModel.getRoads() }, modifier = Modifier.padding(2.dp)) {
                Icon(imageVector = Icons.Default.Place, "Getting roads")
                Text("Get roads")
            }
            if (uiState.towns.isNotEmpty())
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.towns) {
                        Text("${it.name} ${it.subdivision}", Modifier.padding(horizontal = 8.dp))
                    }
                }
            if (uiState.roads.isNotEmpty())
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.roads) {
                        Text("${it.town1} - ${it.town2} : ${it.distance}",
                            Modifier.padding(horizontal = 8.dp))
                    }
                }
        }

        // Check for user messages to display on the screen
        uiState.message?.let { msg ->
            LaunchedEffect(scaffoldState, viewModel, msg) {
                scaffoldState.snackbarHostState.showSnackbar(msg)
                viewModel.snackbarMessageShown()
            }
        }
    }
}

@Composable
fun FabVisibilityToggle(
    isVisible: Boolean,
    onClick: (Boolean) -> Unit,
    isVisibleIcon: ImageVector,
    isNotVisibleIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = { onClick(isVisible) },
        modifier = modifier
    ) {
        Crossfade(targetState = isVisible) {
            when (it) {
                true -> Icon(
                    imageVector = isNotVisibleIcon,
                    contentDescription = null
                )
                else -> Icon(
                    imageVector = isVisibleIcon,
                    contentDescription = null
                )
            }
        }
    }
}


/*
*
 * A screen to add and configure the trips and towns; Visualize roads etc
 *
*/
/*
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UniverseEditor(
    viewModel: UniverseEditorVM,
) {
    val uiState = UniverseEditorUiS.rememberUiS(uiS = viewModel.uiS)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (toolboxRef, mapRef) = createRefs()

        BoxWithConstraints(modifier = Modifier
            .constrainAs(mapRef) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            .fillMaxSize()
            .graphicsLayer {
                uiState.masterUI.root.also { root ->
                    scaleX = root.scale
                    scaleY = root.scale
                    translationX = root.offset.x
                    translationX = root.offset.y
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    viewModel.updateMasterUi {
                        Log.d("Root", "Pan:$pan, zoom:$zoom")
                        it.update(it.root.copy(offset = pan, scale = zoom))
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    viewModel.updateUiS { uis ->
                        uis.copy(nodeCursor = "",
                            nodeSelection = emptyList(),
                            linkCursor = "",
                            linkSelection = emptyList())
                    }
                })
            }) {
            //We specify the screen center for later measurements
            viewModel.updateUiS {
                it.copy(screenCenter = Offset(this.maxWidth.value / 2, maxHeight.value / 2f))
            }
            //pair: key = ID, value = Node
            // Displaying all nodes
            uiState.nodes.forEach { pair ->
                NodeItem(node = pair.value, onNodeChange = { new ->
                    //When a node is moved or clicked etc, we immediately update the state
                    viewModel.updateUiS {
                        Log.d("NodeItem", "Modified $new")
                        //We replace the old node with the new one
                        it.copy(nodes = it.nodes.apply { this[pair.key] = new })
                    }
                })
            }
            // Displaying the link i.e roads
            LinkItem(itinerary = uiState.itinerary, onLinkChange = { newLink ->
                viewModel.updateUiS { it.copy(itinerary = newLink) }
            }, nodes = uiState.nodes, masterUI = uiState.masterUI)
        }

        //Toolbox
        AnimatedContent(
            targetState = uiState.fabState,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .constrainAs(toolboxRef) {
                    centerVerticallyTo(parent, 1f)
                    centerHorizontallyTo(parent, 0.99f)
                },
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState())
            ) {
                if (uiState.nodeCursor.isBlank())
                    if (it == NORMAL) {
                        FloatingActionButton(
                            onClick = { */
/*Add node*//*
 },
                            modifier = Modifier.size(uiState.fabSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Route,
                                contentDescription = "Add Town",
                                modifier = Modifier.rotate(90f)
                            )
                        }
                        FloatingActionButton(
                            onClick = { */
/*TODO*//*
 },
                            modifier = Modifier.size(uiState.fabSize)
                        ) {
                            Icon(
                                imageVector = Icons.TwoTone.AutoGraph,
                                contentDescription = "Add Town",
                                modifier = Modifier.rotate(90f)
                            )
                        }
                        FloatingActionButton(
                            onClick = { */
/*TODO*//*
 },
                            modifier = Modifier.size(uiState.fabSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Get Details"
                            )
                        }
                    }

                FabVisibilityToggle(
                    isVisible = uiState.fabState == NORMAL,
                    onClick = {
                        Log.d("Clicked me", uiState.toString())
                        viewModel.updateUiS { uis ->
                            uis.copy(fabState = if (it) HIDDEN else NORMAL)
                        }
                    },
                    isVisibleIcon = Icons.Default.Visibility,
                    isNotVisibleIcon = Icons.Default.VisibilityOff,
                    modifier = Modifier.size(uiState.fabSize)
                )
            }
        }
    }
}
*/
