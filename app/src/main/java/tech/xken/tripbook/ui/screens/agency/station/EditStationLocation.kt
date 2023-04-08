package tech.xken.tripbook.ui.screens.agency.station

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.domain.*
import tech.xken.tripbook.ui.components.MenuItem
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.navigation.AgencyArgs
import tech.xken.tripbook.ui.navigation.AgencyScreens
import tech.xken.tripbook.ui.navigation.UnivScreens
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue


data class StationLocationUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val towns: List<String> = listOf(),
    val univSelections: List<UnivSelection> = listOf(),
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val station: Station = Station.new(),
    val latStr: String = "",
    val lonStr: String = "",
) {
    val geo = if (station.lat != null && station.lon != null) {
        "${station.lat.absoluteValue}°${
            when {
                station.lat > 0 -> "N"
                station.lat < 0 -> "S"
                else -> ""
            }
        }, ${station.lon.absoluteValue}°${
            when {
                station.lon > 0 -> "E"
                station.lon < 0 -> "W"
                else -> ""
            }
        }"
    } else ""
}

@HiltViewModel
class EditStationLocationVM @Inject constructor(
    private val repo: AgencyRepository,
    private val cacheRepo: CachesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _station = MutableStateFlow(
        Station(
            id = savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!,
            lat = savedStateHandle[AgencyArgs.AGENCY_STATION_LAT]!!,
            lon = savedStateHandle[AgencyArgs.AGENCY_STATION_LON]!!,
            name = savedStateHandle[AgencyArgs.AGENCY_STATION_NAME]!!,
            agencyID = null, supportPhone1 = null, supportPhone2 = null,
            supportPhone1Code = null, supportEmail1 = null, supportPhone2Code = null,
            supportEmail2 = null, photoUrl = null, timestamp = null
        )
    )
    private val _latStr = MutableStateFlow(_station.value.lat?.toString() ?: "")
    private val _lonStr = MutableStateFlow(_station.value.lon?.toString() ?: "")
    private val _selectedTowns = MutableStateFlow<List<String>>(listOf())
    private val _townsAsync =
        repo.observeStationTownMaps(savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!)
            .map { handleStationTownMapResult(it) }
            .onStart {
                onLoading(true)
                _isInitComplete.value = false
            }
    private val _towns = MutableStateFlow<List<String>>(listOf())
    private val _univSelectionsAsync = cacheRepo.observeUnivSelections(
        fromScreen = UnivScreens.UNIVERSE_SEARCH,
        toScreen = AgencyScreens.AGENCY_STATION_LOCATION
    ).map { handleUnivSelections(it) }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    private fun handleUnivSelections(selection: Results<List<UnivSelection>>): Async<List<UnivSelection>?> =
        if (selection is Results.Success) {
            _selectedTowns.value =
                selection.data.find { it.place == R.string.lb_town }?.selections?.trim()
                    ?.split(" ")
                    ?: listOf()
            _isInitComplete.value = true
            onLoading(false)
            Async.Success(selection.data)
        } else Async.Success(null)

    private fun handleStationTownMapResult(selection: Results<List<StationTownMap>>): Async<List<String>?> =
        if (selection is Results.Success) {
            _towns.value = selection.data.map { it.town }
            Async.Success(selection.data.map { it.town })
        } else Async.Success(null)

    val uiState = combine(
        _isLoading,//0
        _message,//1
        _isInitComplete,//2
        _towns,//3
        _station,//4,
        _univSelectionsAsync,//5
        _latStr,//6
        _lonStr,//7
        _townsAsync,//8
        _selectedTowns//9
    ) { args ->
        StationLocationUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            isInitComplete = args[2] as Boolean,
            towns = args[3] as List<String>,
            station = args[4] as Station,
            latStr = args[6] as String,
            lonStr = args[7] as String,
            univSelections = args[9] as List<UnivSelection>
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = StationLocationUiState()
    )

    fun saveTownSelectionToCache() {
        if (uiState.value.towns.isNotEmpty())
            viewModelScope.launch {
                cacheRepo.clearUnivSelections()
                cacheRepo.saveUnivSelections(
                    listOf(
                        UnivSelection(
                            place = R.string.lb_town,
                            selections = uiState.value.towns.fold("") { acc, s -> "$acc $s" },
                            fromScreen = AgencyScreens.AGENCY_STATION_LOCATION,
                            toScreen = UnivScreens.UNIVERSE_SEARCH
                        )
                    )
                )
            }
    }

    /**
     *
     */
    fun saveStation(navigateAway: () -> Unit) {
        when {
            !isNoError -> _message.value = R.string.msg_fields_contain_errors
            !_isInitComplete.value -> _message.value = R.string.msg_init_not_complete
            else -> viewModelScope.launch {
                onLoading(true)
                repo.saveStationLocation(
                    _station.value.id,
                    _station.value.lat!!,
                    _station.value.lon!!
                )
                if (_selectedTowns.value.isNotEmpty()) {
                    val toBeDeletedTowns =
                        _towns.value.filterNot { _selectedTowns.value.contains(it) }
                    val toBeAddedStationTownMaps =
                        _selectedTowns.value.filterNot { _towns.value.contains(it) }.map {
                            StationTownMap(
                                station = _station.value.id,
                                town = it,
                                timestamp = Date().time
                            )
                        }
                    repo.deleteStationTownMaps(_station.value.id, toBeDeletedTowns)
                    repo.saveStationTownMaps(toBeAddedStationTownMaps)
                    cacheRepo.clearUnivSelections()
                }
                onLoading(false)
                navigateAway()
            }
        }
    }

    fun onLatChange(new: String) {
        _latStr.value = new
        _station.value = _station.value.copy(lat = new.toDoubleOrNull())
    }

    fun onLonChange(new: String) {
        _lonStr.value = new
        _station.value = _station.value.copy(lon = new.toDoubleOrNull())
    }

    fun latErrorText(value: Double? = _station.value.lat) = when (value) {
        null -> R.string.msg_required_field
        !in (-90.0..90.0) -> R.string.msg_invalid_field
        else -> null
    }

    fun lonErrorText(value: Double? = _station.value.lon) = when (value) {
        null -> R.string.msg_required_field
        !in (-180.0..180.0) -> R.string.msg_invalid_field
        else -> null
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }

    val isNoError get() = latErrorText() == null && lonErrorText() == null
}

@Composable
fun EditStationLocation(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: EditStationLocationVM = hiltViewModel(),
    onAddEditLocationClick: (hasSelection: Boolean) -> Unit,
    onComplete: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uis by vm.uiState.collectAsState()
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)

    val focusManager = LocalFocusManager.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(uis.station.name ?: "", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { vm.saveStation { onComplete() } }) {
                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .disableComposable(uis.isLoading)
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = uis.isLoading,
                modifier = Modifier.padding(4.dp)
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            AnimatedVisibility(visible = vm.isNoError) {
                Text(
                    text = uis.geo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.h4.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
            }

            //Lat
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.latStr,
                errorText = { vm.latErrorText(it.toDoubleOrNull() ?: -1000.0) },
                onValueChange = { vm.onLatChange(it) },
                trailingIcon = {
                    if (uis.latStr.isNotBlank())
                        IconButton(onClick = { vm.onLatChange("") }) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "")
                        }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "")
                },
                label = { Text(stringResource(R.string.lb_latitude).caps) },
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Decimal
                )
            )

            //Lon
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.lonStr,
                errorText = { vm.lonErrorText(it.toDoubleOrNull() ?: -1000.0) },
                onValueChange = { vm.onLonChange(it) },
                trailingIcon = {
                    if (uis.lonStr.isNotBlank())
                        IconButton(onClick = { vm.onLonChange("") }) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "")
                        }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "")
                },
                keyboardActions = KeyboardActions(
                    onDone = { }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                ),
                label = { Text(stringResource(R.string.lb_longitude).caps) },
            )

            Divider(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            if (uis.univSelections.isNotEmpty())
                MenuItem(
                    imageVector = Icons.Outlined.PendingActions,
                    title = "${uis.univSelections.size} Pending Selections",
                    subtitle = "You have surely made or removed some selection but haven't saved yet. All these will be saved when you complete",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    isEnabled = false
                ) { vm.onMessageChange(R.string.msg_nothing) }

            MenuItem(
                imageVector = Icons.Outlined.AddLocationAlt,
                title = "${if (uis.towns.isEmpty()) "No" else uis.towns.size} Affiliated Towns",
                errorText = { if (uis.towns.isEmpty() && uis.univSelections.isEmpty()) "You need to add towns which can be affiliated to this station" else null },
                subtitle = "Add new or remove existing affiliated towns",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                vm.saveTownSelectionToCache()
                onAddEditLocationClick(uis.towns.isNotEmpty())
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
