package tech.xken.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import tech.xken.tripbook.ui.navigation.AppNavGraph

import tech.xken.tripbook.ui.theme.TripbookTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripbookTheme {
                AppNavGraph()
            }
        }
    }
}

/*
@Composable
fun TownAdder(viewModel: TownsVM, roadsVM: RoadsVM) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isLoading by remember { mutableStateOf(false) }
        Text(text = "Remaining: ${viewModel.left}")
        Button(
            modifier = Modifier.padding(4.dp),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.repo.addAllTowns(
                        towns = viewModel.townsFromFile,
                        onLoading = {
                            isLoading = it
                        },
                        onFailure = {
                            Log.e("FAILURE_TOWNS", it.message.toString())
                            isLoading = false
                        },
                        onSuccess = {
                            isLoading = false
                            viewModel.townStr = "DONE"
                        }
                    )
                }

            }
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add towns",
                    modifier = Modifier.padding(4.dp)
                )
                Text("Add Towns", modifier = Modifier.padding(4.dp))
            }
        }
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.repo.getAllTowns(
                        fields = with(Town.Companion) {
                            "$ID,$NAME,$LAT, $LON"
                        },
                        onLoading = {
                            isLoading = it
                        },
                        onFailure = {
                            Log.e("FAILURE_TOWNS", it.message.toString())
                            isLoading = false
                        },
                        onSuccess = {
                            isLoading = false
                            //We fill the towns
                            roadsVM.towns = it
                            viewModel.townStr = it.size.toString()
                        }
                    )
                }
            },
            modifier = Modifier.padding(4.dp),
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Add towns",
                    modifier = Modifier.padding(4.dp)
                )
                Text("Get Towns", modifier = Modifier.padding(4.dp))
            }
        }
        AnimatedVisibility(visible = !isLoading) {
            Text(
                text = viewModel.townStr,
                modifier = Modifier.padding(4.dp),
            )
        }
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RoadAdder(viewModel: RoadsVM) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isLoading by remember { mutableStateOf(false) }
        val allRoadStr = stringArrayResource(id = R.array.roads)
        Text(text = "Remaining: ${viewModel.left}")
        Button(
            modifier = Modifier.padding(4.dp),
            onClick = {
                if (viewModel.towns.isNotEmpty())
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("ARRAY", allRoadStr.toString())
                        viewModel.getRoadsFromFile(allRoadStr).forEach { road ->
                            viewModel.repo.addRoad(
                                road = road,
                                onLoading = {
                                    isLoading = it
                                },
                                onFailure = {
                                    Log.e("FAILURE_TOWNS", "$road -> ${it.message}")
                                },
                                onSuccess = {
                                    viewModel.roadStr = road.through.toString()
                                }
                            )
                        }
                        isLoading = false

                    }
                else Log.d("TOWNS", "Is empty")
            }
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.AddRoad,
                    contentDescription = "Add towns",
                    modifier = Modifier.padding(4.dp)
                )
                Text("Add Roads", modifier = Modifier.padding(4.dp))
            }
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.repo.getAllRoads(
                        fields = with(Road.Companion) {
                            "$ID,$NAME, $DISTANCE"
                        },
                        onLoading = {
                            isLoading = it
                        },
                        onFailure = {
                            Log.e("FAILURE_TOWNS", it.message.toString())
                            isLoading = false
                        },
                        onSuccess = {
                            isLoading = false
                            viewModel.roadStr = it.toString()
                        }
                    )
                }
            },
            modifier = Modifier.padding(4.dp),
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Add towns",
                    modifier = Modifier.padding(4.dp)
                )
                Text("Get Roads", modifier = Modifier.padding(4.dp))
            }
        }
        AnimatedVisibility(visible = !isLoading) {
            Text(
                text = viewModel.roadStr,
                modifier = Modifier.padding(4.dp),
            )
        }
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator()
        }
    }
}*/
