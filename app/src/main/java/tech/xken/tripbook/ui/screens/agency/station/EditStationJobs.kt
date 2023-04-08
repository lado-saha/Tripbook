package tech.xken.tripbook.ui.screens.agency.station

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import tech.xken.tripbook.data.models.StationJob
import tech.xken.tripbook.domain.WhileUiSubscribed
import javax.inject.Inject
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.ui.navigation.AgencyArgs
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Job
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.sources.init.InitRepository
import tech.xken.tripbook.domain.Async
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.JobItem
import tech.xken.tripbook.ui.components.ListHeader
import tech.xken.tripbook.ui.components.StationJobItem

data class StationJobsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val stationJobs: List<StationJob> = listOf(),
    val jobs: List<Job> = listOf(),
    val isStationJobsVisible: Boolean = true,
    val isJobsVisible: Boolean = true,
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
        _isJobsVisible
    ) { args ->
        StationJobsUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            isInitComplete = args[2] as Boolean,
            isEditMode = args[3] as Boolean,
            stationJobs = args[4] as List<StationJob>,
            jobs = args[5] as List<Job>,
            isStationJobsVisible = args[7] as Boolean,
            isJobsVisible = args[8] as Boolean
        ).also { state ->
            if (_jobs.value.isEmpty())
                initRepo.jobs().also {
                    when (it) {
                        is Failure -> onMessageChange(null)
                        is Success -> {
                            _jobs.value = it.data
                            Log.d("INIT_REPO", it.data.toString())
                            _isInitComplete.value = true
                            onLoading(false)
                        }
                    }
                }
        }
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = StationJobsUiState()
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

    //val isNoError get() = latErrorText() == null && lonErrorText() == null
}

@Composable
fun StationJobs(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: StationJobsVM = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onJobClick: (id: String) -> Unit,
) {
    val uis by vm.uiState.collectAsState()
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
                actions = {
                    // Save the work done
                    IconButton(onClick = { /*TODO*/ }) {
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
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState()
        ) {
            //This is the station jobs and below we have the jobs which can be further added by the scanner
            item {
                ListHeader(
                    text = stringResource(R.string.lb_added_jobs).titleCase,
                    logoIcon = null,
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
                    onVisibilityClick = {
                        vm.onStationJobsVisibilityChange(!it)
                    }, isVisible = uis.isStationJobsVisible)
            }
            if (uis.stationJobs.isEmpty()) item {
                Text(
                    stringResource(R.string.msg_no_jobs_yet),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else items(uis.stationJobs, key = { item: StationJob -> item.jobId }) {
                AnimatedVisibility(
                    visible = uis.isStationJobsVisible, modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    StationJobItem(
                        stationJob = it.apply {
                            job = uis.jobs.find { job -> job.id == it.jobId }
                        }
                    ) {
                        //Todo on job station Item click
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
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
                    logoIcon = null,
                    onVisibilityClick = {
                        vm.onJobsVisibilityChange(!it)
                    },
                    isVisible = uis.isJobsVisible
                )
            }
            items(uis.jobs, key = { item: Job -> item.id }) {
                AnimatedVisibility(
                    uis.isJobsVisible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp), enter = shrinkOut(), exit =
                ) {
                    JobItem(job = it) {
                        //Todo on Job Item click
                    }
                }
            }
        }
    }
}

/**
@Composable
fun StationJobDetail(
onNavigateBack: () -> Unit,
vm: StationJobsVM = hiltViewModel(),
scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
Scaffold(
scaffoldState = scaffoldState,
) {

}
}
 */