package tech.xken.tripbook.ui.screens.agency.station

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Job.Companion.DefaultJobs
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.data.sources.init.InitRepository
import tech.xken.tripbook.domain.Async
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.ui.navigation.AgencyArgs
import java.util.*
import javax.inject.Inject

data class StationDashboardUiState(
    val station: Station = Station.new(),
    val isLoading: Boolean = false,
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val photoBitmap: Bitmap? = null,
    val results: List<UnivSelection> = listOf(),
    val isEditMode: Boolean = false,
)

@HiltViewModel
class StationDashboardVM @Inject constructor(
    private val repo: AgencyRepository,
    private val cachesRepo: CachesRepository,
    private val savedStateHandle: SavedStateHandle,
    private val initRepo: InitRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)

    private val _stationAsync =
        repo.observeStationsFromIds(
            listOf(savedStateHandle[AgencyArgs.AGENCY_STATION_ID] ?: ("${NEW_ID}12"))
        ).map { handleResult(it) }.onStart {
            Async.Loading
        }.onEach { }

    private fun handleResult(selection: Results<List<Station>>): Async<Station?> =
        if (selection is Results.Success) {
            _isInitComplete.value = true
            _photoBitmap.value = null
            Async.Success(
                selection.data.getOrElse(0) {
                    Station.new(savedStateHandle[AgencyArgs.AGENCY_STATION_ID] ?: ("${NEW_ID}12"))
                }
            )
        } else Async.Success(null)

    val uiState = combine(
        _isLoading,//0
        _message,//1
        _photoBitmap,//2
        _isInitComplete,//3
        _isComplete,//4
        _stationAsync//5
    ) { args ->
        when (val station = args[5] as Async<Station>) {
            Async.Loading -> {
                StationDashboardUiState(isLoading = true)
            }
            is Async.Success -> {
                StationDashboardUiState(
                    isLoading = args[0] as Boolean,
                    message = args[1] as Int?,
                    station = station.data,
                    photoBitmap = args[2] as Bitmap?,
                    isInitComplete = args[3] as Boolean,
                    isComplete = args[4] as Boolean,
                    isEditMode = station.data.name != null,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StationDashboardUiState()
    )

    fun uploadDefaultJobs() {
        viewModelScope.launch {
            initRepo.saveJobs(
                listOf(
                    DefaultJobs.SCANNER,
                    DefaultJobs.LUGGAGE_MASTER,
                    DefaultJobs.MANAGER,
                    DefaultJobs.JANITOR,
                    DefaultJobs.DRIVER
                ).map { it.model })
            onMessageChange(R.string.msg_done)
        }
    }

    fun loadPhotoBitmap(context: Context) {
        val photoUri = uiState.value.station.photoUrl?.toUri()
        if (_photoBitmap.value == null && photoUri != null) try {
            photoUri.let {
                _photoBitmap.value = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        } catch (e: Exception) {
            //TODO: Handle Exception
        }
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }


    fun onMessageChange(new: Int?) {
        _message.value = new
    }

    val profileErrorText
        get() = when (!uiState.value.isEditMode) {
            true -> R.string.msg_add_station_profile
            else -> null
        }

    val personnelErrorText
        get() = when (!uiState.value.isEditMode) {
            true -> R.string.msg_add_station_personnel
            else -> null
        }

    val scheduleErrorText
        get() = when (!uiState.value.isEditMode) {
            true -> R.string.msg_add_station_schedules
            else -> null
        }

    val vehiclesErrorText
        get() = when (!uiState.value.isEditMode) {
            true -> R.string.msg_add_station_vehicles
            else -> null
        }

    val locationErrorText
        get() = when (!uiState.value.isEditMode || uiState.value.station.lat == null || uiState.value.station.lon == null) {
            true -> R.string.msg_locate_station
            else -> null
        }

    val isNoError
        get() = (profileErrorText == null && personnelErrorText == null && scheduleErrorText == null && vehiclesErrorText == null)

}