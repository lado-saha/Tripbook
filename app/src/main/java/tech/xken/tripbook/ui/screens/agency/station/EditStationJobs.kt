package tech.xken.tripbook.ui.screens.agency.station

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Bottom
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.ui.navigation.AgencyArgs
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.sources.init.InitRepository
import tech.xken.tripbook.domain.*
import tech.xken.tripbook.ui.components.JobItem
import tech.xken.tripbook.ui.components.ListHeader
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.components.StationJobItem
import java.util.*

data class StationJobsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val stationJobs: List<StationJob> = listOf(),
    val jobs: List<Job> = listOf(),
    val isStationJobsVisible: Boolean = true,
    val isJobsVisible: Boolean = true,
    val currentJob: StationJob? = null,
    val isDetailsView: Boolean = false,
)

/** For the Detail View*/
data class StationJobUiState(
    val stationJob: StationJob? = null,
    val nameStr: String = "",
    val salaryStr: String = "",
    val isScheduleExpanded: Boolean = false,
    val selectedSchedule: PaymentSchedule = PaymentSchedule.MONTHLY,
    val isDetailViewComplete: Boolean = false
)

@HiltViewModel
class StationJobsVM @Inject constructor(
    private val repo: AgencyRepository,
    private val cacheRepo: CachesRepository,
    private val initRepo: InitRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isJobsVisible = MutableStateFlow(true)
    private val _isStationJobsVisible = MutableStateFlow(true)
    private val _isEditMode = MutableStateFlow(false)
    private val _stationJobs = MutableStateFlow(listOf<StationJob>())
    private val _jobs = MutableStateFlow(listOf<Job>())
    private val _stationJobAsync =
        repo.observeStationJobs(savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!).map {
            handleStationJobResults(it)
        }.onStart {
            onLoading(true)
        }
    private val _isDetailsView = MutableStateFlow(false)

    //For the detail view
    private val _currentStationJob = MutableStateFlow<StationJob?>(null)
    private val _nameStr = MutableStateFlow("")
    private val _salaryStr = MutableStateFlow("")
    private val _selectedSchedule = MutableStateFlow(PaymentSchedule.MONTHLY)
    private val _isScheduleExpanded = MutableStateFlow(false)
    private val _isDetailComplete = MutableStateFlow(false)

    private fun handleStationJobResults(stationJobs: Results<List<StationJob>>) =
        if (stationJobs is Success) {
            _isInitComplete.value = true
            _stationJobs.value = stationJobs.data
            Async.Success(stationJobs.data)
        } else Async.Success(null)

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }


    @Suppress("UNCHECKED_CAST")
    val uiState = combine(
        _isLoading,//0
        _message,//1
        _isInitComplete,//2
        _isEditMode,//3
        _stationJobs,//4
        _jobs,//5
        _stationJobAsync,//6
        _isStationJobsVisible,
        _isJobsVisible,
        _isDetailsView
    ) { args ->
        StationJobsUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            isInitComplete = args[2] as Boolean,
            isEditMode = args[3] as Boolean,
            stationJobs = args[4] as List<StationJob>,
            jobs = args[5] as List<Job>,
            isStationJobsVisible = args[7] as Boolean,
            isJobsVisible = args[8] as Boolean,
            isDetailsView = args[9] as Boolean
        ).also { state ->
            if (_isInitComplete.value && _jobs.value.isEmpty())
                initRepo.jobs().also { results ->
                    when (results) {
                        is Failure -> {
                            onMessageChange(null)
                        }
                        is Success -> {
                            _jobs.value =
                                results.data.map { Job.defaultJobs[it.id]!! }
                            Log.d("Jobs", _jobs.value.map { it.name }.toString())
                            _isInitComplete.value = true
                            onLoading(false)
                        }
                    }
                }
        }
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = StationJobsUiState()
    )

    val detailUiState = combine(
        _currentStationJob,
        _nameStr,
        _salaryStr,
        _isScheduleExpanded,
        _isDetailComplete,
        _selectedSchedule
    ) {
        StationJobUiState(
            stationJob = it[0] as StationJob?,
            nameStr = it[1] as String,
            salaryStr = it[2] as String,
            isScheduleExpanded = it[3] as Boolean,
            isDetailViewComplete = it[4] as Boolean,
            selectedSchedule = it[5] as PaymentSchedule
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StationJobUiState()
    )

    fun onMessageChange(new: Int?) {
        _message.value = new
    }

    fun onStationJobsVisibilityChange(new: Boolean) {
        _isStationJobsVisible.value = new
    }

    fun onJobsVisibilityChange(new: Boolean) {
        _isJobsVisible.value = new
    }

    fun onNameChange(new: String) {
        _nameStr.value = new
        _currentStationJob.value = _currentStationJob.value!!.copy(name = new)
    }

    val namePlaceHolder get() = if (_nameStr.value.isBlank()) _currentStationJob.value!!.job!!.name else null

    fun onSalaryChange(new: String) {
        _salaryStr.value = new
        _currentStationJob.value = _currentStationJob.value!!.copy(salary = new.toLongOrNull())
    }

    fun salaryErrorText(value: Long? = _currentStationJob.value?.salary) = when {
        value == null -> R.string.msg_required_field
        value < 0L -> R.string.msg_invalid_field
        else -> null
    }

    fun onScheduleExpansionChange(new: Boolean) {
        _isScheduleExpanded.value = new
    }

    fun onSelectedExpansionChange(schedule: PaymentSchedule) {
        _selectedSchedule.value = schedule
    }

    val isNoError get() = salaryErrorText() == null
    fun saveCurrentStationJob() {
        if (isNoError) viewModelScope.launch {
            onLoading(true)
            repo.saveStationJobs(listOf(_currentStationJob.value!!))
            destructStationJobView()
            onLoading(true)
        }
        else onMessageChange(R.string.msg_fields_contain_errors)
    }

    /**called when we want are through with editing the details of a station job*/
    fun destructStationJobView() {
        _isDetailsView.value = false
        _currentStationJob.value = null
        _nameStr.value = ""
        _salaryStr.value = ""
        _isScheduleExpanded.value = false
        _isDetailComplete.value = true
    }

    /**
     * Called when we want to start to edit the details of a  Job*/
    fun constructStationJobView(jobId: String) {
        _isDetailsView.value = true
        _isDetailComplete.value = false
        _currentStationJob.value = _stationJobs.value.find { it.jobId == jobId }!!.apply {
            job = _jobs.value.find { it.id == jobId }
        }
    }

    fun addJobToStation(job: Job) = viewModelScope.launch {
        onLoading(true)
        repo.saveStationJobs(
            listOf(
                StationJob(
                    jobId = job.id,
                    station = savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!,
                    salary = null,
                    name = null,
                    paymentSchedule = null,
                    timestamp = Date().time
                )
            )
        )
        onMessageChange(R.string.msg_success_adding_job)
        onLoading(false)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StationJobs(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: StationJobsVM = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onStationJobClick: (id: String) -> Unit,
) {
    val uis by vm.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it == ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = false
    )
    var isSheetFullScreen by remember { mutableStateOf(false) }

    val roundedCornerRadius = if (isSheetFullScreen) 12.dp else 12.dp
    val modifier = if (isSheetFullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.lb_jobs).titleCase) },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { paddingValues ->
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(
                topStart = roundedCornerRadius,
                topEnd = roundedCornerRadius
            ),
            sheetContent = {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            isSheetFullScreen = !isSheetFullScreen
                        }
                    ) {
                        Text(text = "Hide Sheet")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
            ) {
                //This is the station jobs and below we have the jobs which can be further added by the scanner
                item {
                    ListHeader(
                        text = stringResource(R.string.lb_added_jobs).titleCase,
                        logoIcon = Icons.Default.CheckCircle,
                        modifier = Modifier.padding(2.dp),
                        onVisibilityClick = {
                            vm.onStationJobsVisibilityChange(!it)
                        },
                        isVisible = uis.isStationJobsVisible
                    )
                }
                if (uis.stationJobs.isEmpty()) item {
                    Text(
                        stringResource(R.string.msg_no_jobs_yet),
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else items(
                    uis.stationJobs,
                    key = { item: StationJob -> "station_${item.jobId}" }) {
                    AnimatedVisibility(
                        visible = uis.isStationJobsVisible, modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        StationJobItem(
                            stationJob = it.apply {
                                job = uis.jobs.find { job -> job.id == it.jobId }
                            }, errorText = {
                                if (it.salary == null || it.paymentSchedule == null)
                                    stringResource(id = R.string.msg_job_not_initialized).caps
                                else null
                            }
                        ) {
                            coroutineScope.launch { sheetState.show() }
                            // We prepare the detail view before navigating away
                            vm.constructStationJobView(it.jobId)
                            onStationJobClick(it.jobId)
                        }
                    }
                }
                item {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                //This is the job which can be added at will by the scanner
                item {
                    ListHeader(
                        text = stringResource(R.string.lb_available_jobs).titleCase,
                        modifier = Modifier.padding(bottom = 2.dp),
                        logoIcon = Icons.Default.Work,
                        onVisibilityClick = {
                            vm.onJobsVisibilityChange(!it)
                        },
                        isVisible = uis.isJobsVisible
                    )
                }
                if (uis.stationJobs.size == uis.jobs.size) item {
                    Text(
                        stringResource(R.string.msg_no_jobs_left),
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else items(
                    uis.jobs.filter { job -> uis.stationJobs.find { it.jobId == job.id } == null },
                    key = { item: Job -> item.id }) {
                    AnimatedVisibility(
                        visible = uis.isJobsVisible, modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        JobItem(job = it, modifier = Modifier.padding(4.dp)) {
                            vm.addJobToStation(it)
                        }
                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StationJobDetailsBottomSheet(
    onNavigateBack: () -> Unit,
    vm: StationJobsVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),

    ) {

    val uis by vm.detailUiState.collectAsState()

    if (uis.isDetailViewComplete) onNavigateBack()

    val fieldPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    val focusManager = LocalFocusManager.current
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(uis.nameStr.ifBlank { stringResource(id = uis.stationJob!!.job!!.name!!) }.titleCase) },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = {
                        vm.destructStationJobView()
                        onNavigateBack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    // Save the work done
                    IconButton(onClick = { vm.saveCurrentStationJob() }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
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
                .verticalScroll(rememberScrollState())
        ) {
            // Name
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.stationJob!!.name ?: "",
                errorText = { null },
                onValueChange = { vm.onNameChange(it) },
                trailingIcon = {
                    if (!uis.stationJob!!.name.isNullOrBlank())
                        IconButton(onClick = { vm.onNameChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = ""
                            )
                        }
                },
                leadingIcon = { Icon(imageVector = Icons.Default.Tag, contentDescription = "") },
                label = { Text(stringResource(R.string.lb_name).caps) },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                placeholder = { vm.namePlaceHolder }
            )

//        Salary
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.salaryStr,
                errorText = { vm.salaryErrorText(it.toLongOrNull()) },
                onValueChange = { vm.onSalaryChange(it) },
                trailingIcon = {
                    if (uis.salaryStr.isNotBlank())
                        IconButton(onClick = { vm.onSalaryChange("") }) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "")
                        }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Money, contentDescription = "")
                },
                label = { Text(stringResource(R.string.lb_salary).caps) },
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Decimal
                )
            )

//      Payment Schedule
            ExposedDropdownMenuBox(
                expanded = uis.isScheduleExpanded,
                onExpandedChange = { vm.onScheduleExpansionChange(it) },
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth()
            ) {
                OutTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = stringResource(id = uis.selectedSchedule.str).caps,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = uis.isScheduleExpanded
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = "")
                    },
                    label = { Text(stringResource(R.string.lb_payment_schedule).caps) },
                    onValueChange = {},
                    readOnly = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions.Default,
                    singleLine = true,
                )
                ExposedDropdownMenu(
                    expanded = uis.isScheduleExpanded,
                    onDismissRequest = { vm.onScheduleExpansionChange(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PaymentSchedule.values().forEach { schedule ->
                        DropdownMenuItem(onClick = {
                            vm.onSelectedExpansionChange(schedule)
                            vm.onScheduleExpansionChange(false)
                        }) {
                            Text(text = stringResource(id = schedule.str).caps)
                        }
                    }
                }
            }


            // Wiki
            Card(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
            ) {
                Column {
                    Text(
                        text = stringResource(uis.stationJob!!.job!!.shortWiki!!),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.lb_wiki),
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


//fun Auth(app: String, slogan: String, appelerMoiQuandOnVeutChangeriUser: (nouvelValeur: Boolean) -> Unit){
//    /* ...................*/
//    appelerMoiQuandOnVeutChangeriUser(true)
//    /* ...................*/
//
//}
//@Composable
//fun AppTrip(app: String, slogan: String, modifier: Modifier = Modifier){
//    var isUser = remember{
//        mutableStateOf(false)
//    }
//
//    /* ................................. */
//
//    if(isUser.value){/*  ......  */}
//    else {
//        Auth(app = app, slogan = slogan, onUserStateChange =  { nouvelValeur ->
//            isUser = nouvelValeur
//        })
//    }
//}


