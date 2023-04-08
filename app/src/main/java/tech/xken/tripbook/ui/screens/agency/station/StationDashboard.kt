package tech.xken.tripbook.ui.screens.agency.station

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Station
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.MainMenuItem
import tech.xken.tripbook.ui.components.MenuItem

@Composable
fun StationDashboard(
    vm: StationDashboardVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateBack: () -> Unit,
    onComplete: (() -> Unit) = {},
    onLocationClick: (station: Station) -> Unit = {},
    onProfileClick: (stationID: String, isEditMode: Boolean) -> Unit,
    onScheduleClick: () -> Unit = {},
    onVehiclesClick: () -> Unit = {},
    onPersonnelClick: () -> Unit = {},
    onStationJobsClick: (stationID: String) -> Unit = {},
) {
    //Initialisation Processes
    val uis by vm.uiState.collectAsState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = (uis.station.name ?: "").titleCase
                            .ifBlank { stringResource(id = R.string.lb_station_dashboard).titleCase },
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
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
            vm.loadPhotoBitmap(LocalContext.current)
            MainMenuItem(
                imageBitmap = uis.photoBitmap,
                title = stringResource(id = R.string.lb_profile).titleCase,
                subtitle = stringResource(R.string.lb_station_subtitle_profile),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                isLoading = uis.isLoading,
                errorText = { vm.profileErrorText?.run { stringResource(id = this).caps } }
            ) { onProfileClick(uis.station.id, uis.isEditMode) }

            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 1.dp)
                    .fillMaxWidth()
            )

            MenuItem(
                imageVector = Icons.Outlined.LocationOn,
                title = stringResource(R.string.lb_location).titleCase,
                subtitle = stringResource(R.string.lb_station_subtitle_location),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                errorText = { vm.locationErrorText?.run { stringResource(id = this).caps } }
            ) {
                if (uis.isEditMode)
                    onLocationClick(uis.station)
                else
                    vm.onMessageChange(R.string.lb_msg_profile_required)
            }

            MenuItem(
                imageVector = Icons.Outlined.WorkOutline,
                title = stringResource(R.string.lb_jobs).titleCase,
                subtitle = stringResource(R.string.lb_station_subtitle_jobs),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                errorText = { vm.personnelErrorText?.run { stringResource(id = this).caps } },
            ) {
                if (uis.isEditMode)
                    onStationJobsClick(uis.station.id)
                else
                    vm.onMessageChange(R.string.lb_msg_profile_required)
            }

            MenuItem(
                imageVector = Icons.Outlined.Group,
                title = stringResource(R.string.lb_personnel).titleCase,
                subtitle = stringResource(R.string.lb_station_subtitle_personnel),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                errorText = { vm.personnelErrorText?.run { stringResource(id = this).caps } }
            ) {
                vm.uploadDefaultJobs()
            }

            MenuItem(
                imageVector = Icons.Outlined.Schedule,
                title = stringResource(R.string.lb_trip_schedules).titleCase,
                subtitle = "You've added 2 schedules",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                errorText = { vm.scheduleErrorText?.run { stringResource(id = this).caps } }
            ) {
                if (uis.isEditMode)
                    onScheduleClick()
                else
                    vm.onMessageChange(R.string.lb_msg_profile_required)
            }

            MenuItem(
                imageVector = Icons.Outlined.DirectionsBus,
                title = stringResource(R.string.lb_vehicles).titleCase,
                subtitle = "You've added 4 different vehicles",
                errorText = { vm.vehiclesErrorText?.run { stringResource(id = this).caps } },
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            ) {
                if (uis.isEditMode)
                    onVehiclesClick()
                else
                    vm.onMessageChange(R.string.lb_msg_profile_required)
            }

        }
    }

    if (uis.message != null) {
        val message = stringResource(id = uis.message!!).caps
        LaunchedEffect(scaffoldState, vm, uis.message) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message
            )
            vm.onMessageChange(null)
        }
    }

}