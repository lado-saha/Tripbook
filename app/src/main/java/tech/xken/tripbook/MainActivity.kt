@file:OptIn(ExperimentalMaterialApi::class)

package tech.xken.tripbook


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.CacheSyncDialogStatus
import tech.xken.tripbook.ui.CacheSyncUiState
import tech.xken.tripbook.ui.MainViewModel
import tech.xken.tripbook.ui.SheetState
import tech.xken.tripbook.ui.components.DashboardItem
import tech.xken.tripbook.ui.components.DashboardItemUiState
import tech.xken.tripbook.ui.components.DashboardSubItem
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.navigation.AppNavGraph
import tech.xken.tripbook.ui.theme.TripbookTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var vm: MainViewModel

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            vm = hiltViewModel(this)
            val syncUis by vm.integrityUiState.collectAsState()
            val uis by vm.uiState.collectAsState()
            val sheetState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                confirmValueChange = { true }
            )
            LaunchedEffect(uis.sheetState, uis.isConnected) {
                when (uis.sheetState) {
                    SheetState.NETWORK_STATUS_MINIMAL_OFFLINE -> sheetState.show()
                    SheetState.NETWORK_STATUS_EXPANDED_OFFLINE -> sheetState.show()
                    SheetState.NONE -> sheetState.hide()
                    SheetState.NETWORK_STATUS_MINIMAL_ONLINE -> sheetState.show()
                }
            }

            TripbookTheme {
                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 0.4f, topEnd = 0.4f),
                    sheetContent = {
                        when (uis.sheetState) {
                            SheetState.NETWORK_STATUS_MINIMAL_OFFLINE -> Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 14.dp)
                            ) {
                                Card(
                                    onClick = {
                                        vm.onSheetStateChange(SheetState.NETWORK_STATUS_EXPANDED_OFFLINE)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = MaterialTheme.colors.error,
                                    shape = MaterialTheme.shapes.small.copy(CornerSize(0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.run { CloudOff },
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(vertical = 2.dp)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.lb_you_are_offline),
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 2.dp
                                            ),
                                            style = MaterialTheme.typography.h6.copy(fontSize = 10.sp)
                                        )

//                                }
                                    }
                                }
                            }

                            SheetState.NETWORK_STATUS_EXPANDED_OFFLINE ->
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.lb_you_are_offline),
                                            style = MaterialTheme.typography.h6,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .weight(0.9f, true),
                                        )

                                        IconButton(
                                            onClick = { vm.onSheetStateChange(SheetState.NONE) },
                                            modifier = Modifier
                                                .padding(2.dp)
                                                .weight(0.1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                    Divider(modifier = Modifier.fillMaxWidth())

                                    Icon(
                                        imageVector = Icons.Outlined.CloudOff,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(4.dp)
                                            .size(64.dp),
                                        tint = MaterialTheme.colors.error
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = stringResource(R.string.msg_you_are_offline),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth(), textAlign = TextAlign.Justify
                                    )

                                }

                            SheetState.NONE -> {}
                            SheetState.NETWORK_STATUS_MINIMAL_ONLINE -> Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 14.dp)
                            ) {
                                Card(
                                    onClick = {
                                        vm.onSheetStateChange(SheetState.NONE)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = MaterialTheme.colors.primary,
                                    shape = MaterialTheme.shapes.small.copy(CornerSize(0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.run { CloudDone },
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(vertical = 2.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.lb_you_are_back_online),
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 2.dp
                                            ),
                                            style = MaterialTheme.typography.h6.copy(fontSize = 10.sp)
                                        )

//                                }
                                    }
                                }
                            }
                        }
                    },
                    sheetState = sheetState,
                    scrimColor = Color.Unspecified,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (syncUis.syncDone)
                        AppNavGraph(
                            authRepo = vm.authRepo,
                            modifier = Modifier.fillMaxSize()
                        )
                    else
                        CacheSync(
                            vm = vm,
                            uis = syncUis,
                            modifier = Modifier.fillMaxSize()
                        )
                }

            }
        }
    }
}

@Composable
fun CacheSync(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: MainViewModel,
    uis: CacheSyncUiState
) {
    val statusMap = remember {
        mapOf(
            CacheSyncDialogStatus.HELP_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.Person,
                title = "My Booking Profile",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            CacheSyncDialogStatus.HELP_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Badge,
                title = "Me",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            CacheSyncDialogStatus.HELP_OM to InfoDialogUiState(
                mainIcon = Icons.Filled.Payments,
                title = "Orange Money Accounts",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            CacheSyncDialogStatus.HELP_MOMO to InfoDialogUiState(
                mainIcon = Icons.Filled.Payments,
                title = "MTN MoMo Accounts",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            CacheSyncDialogStatus.FAILED_GET_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            CacheSyncDialogStatus.FAILED_GET_OM to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get OM account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            CacheSyncDialogStatus.FAILED_GET_MOMO to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get MoMo account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            )
        )
    }

    val context = LocalContext.current
    val fieldPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    val scrollState = rememberScrollState()

    when (val status = uis.dialogStatus) {
        CacheSyncDialogStatus.HELP_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }
        )

        CacheSyncDialogStatus.HELP_MOMO -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }
        )

        CacheSyncDialogStatus.HELP_OM -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }
        )

        CacheSyncDialogStatus.HELP_CREDIT_CARD -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }
        )

        CacheSyncDialogStatus.HELP_AGENCY_SETTINGS -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)

            }, onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }
        )

        CacheSyncDialogStatus.HELP_MAIN_PAGE ->
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
                }, onPositiveClick = {
                    vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
                }
            )

        CacheSyncDialogStatus.NONE -> {}
        CacheSyncDialogStatus.FAILED_GET_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            },
            onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            },
        )

        CacheSyncDialogStatus.FAILED_GET_OM -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            },
            onPositiveClick = {

                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            },

            )

        CacheSyncDialogStatus.FAILED_GET_MOMO -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            },
            onPositiveClick = {
                vm.onSyncDialogStatusChange(CacheSyncDialogStatus.NONE)
            }
        )
    }

    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.lb_synchronization).titleCase,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(onClick = {
                        vm.onSyncDialogStatusChange(
                            CacheSyncDialogStatus.HELP_MAIN_PAGE
                        )
                    }) {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            contentDescription = stringResource(id = R.string.desc_help),
                            tint = LocalContentColor.current

                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(fieldPadding),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(id = R.string.app_name).caps,
                    style = MaterialTheme.typography.h2.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                    ),
                    modifier = Modifier.padding(4.dp)
                )
                Icon(imageVector = Icons.Outlined.Luggage, contentDescription = null)
            }

            Divider(
                modifier = Modifier
                    .padding(fieldPadding)
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
            )

            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Default.Badge,
                    title = "My Booker Account Sync...",
                    isClickable = false,
                    isHelpable = true,
                    isLoading = !uis.syncAccountDone,
                    isFailure = uis.syncAccountFailed,
                ),
                onHelpClick = {
                    vm.onSyncDialogStatusChange(CacheSyncDialogStatus.HELP_ACCOUNT)
                },
                mainIconColor = MaterialTheme.colors.onSurface,
                onRetryClick = {
                    vm.onSyncAccountDoneChange(false)
                },
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    DashboardSubItem(
                        modifier = Modifier.padding(2.dp),
                        isError = uis.hasSyncAccount != true,
                        positiveText = "Synchronized",
                        negativeText = "Unable to Synchronize"
                    )
                }
            }

            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Filled.Payments,
                    title = "Booker MoMo Accounts Sync...",
                    isClickable = false,
                    isHelpable = true,
                    isLoading = !uis.syncMoMoDone,
                    isFailure = uis.syncMoMoFailed,
                ),
                onHelpClick = {
                    vm.onSyncDialogStatusChange(CacheSyncDialogStatus.HELP_MOMO)
                },
                mainIconColor = MaterialTheme.colors.onSurface,
                onRetryClick = {
                    vm.onSyncMoMoDoneChange(false)
                },
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    DashboardSubItem(
                        modifier = Modifier.padding(2.dp),
                        isError = uis.hasSyncMoMo != true,
                        positiveText = "Synchronized",
                        negativeText = "Unable to Synchronize"
                    )
                }
            }

            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Default.Payments,
                    title = "Booker OM Accounts Sync...",
                    isClickable = false,
                    isHelpable = true,
                    isLoading = !uis.syncOMDone,
                    isFailure = uis.syncOMFailed,
                ),
                onHelpClick = {
                    vm.onSyncDialogStatusChange(CacheSyncDialogStatus.HELP_ACCOUNT)
                },
                mainIconColor = MaterialTheme.colors.onSurface,
                onRetryClick = {
                    vm.onSyncOMDoneChange(false)
                },
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    DashboardSubItem(
                        modifier = Modifier.padding(2.dp),
                        isError = uis.hasSyncOM != true,
                        positiveText = "Synchronized",
                        negativeText = "Unable to Synchronize"
                    )
                }
            }
        }

    }

}


/*
@Composable
fun TownAdder(vm: TownsVM, roadsVM: RoadsVM) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isLoading by remember { mutableStateOf(false) }
        Text(text = "Remaining: ${vm.left}")
        Button(
            modifier = Modifier.padding(4.dp),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    vm.repo.addAllTowns(
                        towns = vm.townsFromFile,
                        onLoading = {
                            isLoading = it
                        },
                        onFailure = {
                            Log.e("FAILURE_TOWNS", it.message.toString())
                            isLoading = false
                        },
                        onSuccess = {
                            isLoading = false
                            vm.townStr = "DONE"
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
                    vm.repo.getAllTowns(
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
                            vm.townStr = it.size.toString()
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
                text = vm.townStr,
                modifier = Modifier.padding(4.dp),
            )
        }
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RoadAdder(vm: RoadsVM) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isLoading by remember { mutableStateOf(false) }
        val allRoadStr = stringArrayResource(id = R.array.roads)
        Text(text = "Remaining: ${vm.left}")
        Button(
            modifier = Modifier.padding(4.dp),
            onClick = {
                if (vm.towns.isNotEmpty())
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("ARRAY", allRoadStr.toString())
                        vm.getRoadsFromFile(allRoadStr).forEach { road ->
                            vm.repo.addRoad(
                                road = road,
                                onLoading = {
                                    isLoading = it
                                },
                                onFailure = {
                                    Log.e("FAILURE_TOWNS", "$road -> ${it.message}")
                                },
                                onSuccess = {
                                    vm.roadStr = road.through.toString()
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
                    vm.repo.getAllRoads(
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
                            vm.roadStr = it.toString()
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
                text = vm.roadStr,
                modifier = Modifier.padding(4.dp),
            )
        }
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator()
        }
    }
}*/
