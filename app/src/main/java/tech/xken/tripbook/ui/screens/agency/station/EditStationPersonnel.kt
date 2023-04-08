package tech.xken.tripbook.ui.screens.agency.station

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.booking.BookingRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.ui.components.Filter
import tech.xken.tripbook.ui.navigation.AgencyArgs
import javax.inject.Inject

data class StationPersonnelUiState(
    val bookers: Map<String, Booker> = mapOf(),
    val allBookers: Map<String, Booker> = mapOf(),
    val allScanners: Map<String, Scanner> = mapOf(),
    val scanners: Map<String, Scanner> = mapOf(),

    val selectedScanners: Map<String, Boolean> = mapOf(),
    val recruitedScanners: Map<String, Boolean> = mapOf(),

    val isLoading: Boolean = false,
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val query: String = "",
    val isToolboxVisible: Boolean = false,
    val isError: Boolean = false,
    val queryFields: List<Int> = listOf(),
    val filters: List<Filter> = listOf(),
    val isEmptyResults: Boolean = false,
    val isFiltersVisible: Boolean = false,
) {
    val isRecruitionMode get() = !isError && filters.find { it.name == R.string.lb_recruited }!!.isSelected
    val isSelectionMode get() = !isError && filters.find { it.name == R.string.lb_selected }!!.isSelected
    val isNormalMode get() = !isSelectionMode && !isError && !isRecruitionMode
}

@HiltViewModel
class EditStationPersonnelVM @Inject constructor(
    private val repo: AgencyRepository,
    private val bookingRepo: BookingRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _scanners = MutableStateFlow(mapOf<String, Scanner>())
    private val _allScanners = MutableStateFlow(mapOf<String, Scanner>())
    private val _selectedScanners = MutableStateFlow(mapOf<String, Boolean>())
    private val _recruitedScanners = MutableStateFlow(mapOf<String, Boolean>())

    private val _queryFields = MutableStateFlow(listOf<Int>())
    private val _isLoading = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    private val _isToolboxVisible = MutableStateFlow(false)
    private val _filters = MutableStateFlow(
        listOf(
            Filter(R.string.lb_suspended),
            Filter(R.string.lb_recruited),
            Filter(R.string.lb_selected)
        )
    )
    private val _message = MutableStateFlow<Int?>(null)
    private val _isFiltersVisible = MutableStateFlow(true)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _isError = MutableStateFlow(false)
    private val _isEmptyResult = MutableStateFlow(false)

    val uiState = combine(
        _query,
        _queryFields,
        _message,
        _isInitComplete,
        _isLoading,
        _isComplete,
        _filters,
        _isFiltersVisible,
        _isError,
        _isEmptyResult,
        _isToolboxVisible,
        _scanners,
        _selectedScanners,
        _recruitedScanners
    ) {
        StationPersonnelUiState(
            query = it[0] as String,
            queryFields = it[1] as List<Int>,
            message = it[2] as Int?,
            isInitComplete = it[3] as Boolean,
            isLoading = it[18] as Boolean,
            filters = it[20] as List<Filter>,
            isFiltersVisible = it[21] as Boolean,
            isError = it[22] as Boolean,
            isEmptyResults = it[23] as Boolean,
            isToolboxVisible = it[31] as Boolean,
            recruitedScanners = it[2] as Map<String, Boolean>
        ).also { initSearch() }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StationPersonnelUiState()
    )

    /**
     * Get the [StationScannerMap]s and from the scanners id, we get the corresponding [Scanner] and asscociate each with
     * its corresponding [Booker] info
     */
    private fun initSearch() {
        if (!_isInitComplete.value) viewModelScope.launch {
            onLoading(true)
            when (val results = repo.scanners(
                agency = savedStateHandle[AgencyArgs.AGENCY_ID]!!,
                station = savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!,
                getBookers = true,
//                getPermissions = true,
                getJobs = true
            )) {
                is Failure -> Log.e("Personnel", results.exception.toString())
                is Success -> _allScanners.value = results.data
            }

            //We further get the recruited ones
            when (val maps =
                repo.stationScannerMaps(savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!, listOf())) {
                is Failure -> Log.e(
                    "EditPersonnel",
                    "StationScannerMap: ${maps.exception}"
                )
                is Success -> _recruitedScanners.value =
                    maps.data.map { it.scanner }.associateWith { true }
            }

            /**repo.scanners(savedStateHandle[AgencyArgs.AGENCY_ID]!!).also { scanners ->
            val tempScanners: Map<String, Scanner>
            var tempBookers = mapOf<String, Booker>()
            var tempStationScannerIds = listOf<String>()
            when (scanners) {
            is Results.Failure ->
            Log.e("EditPersonnel", "Scanners: ${scanners.exception}")
            is Results.Success -> {
            tempScanners = scanners.data.associateBy { it.bookerID }//Scanners to ids
            bookingRepo.bookersFromIds(tempScanners.keys.toList()).also { bookers ->
            when (bookers) {
            is Results.Failure -> Log.e(
            "EditPersonnel",
            "Scanners: ${bookers.exception}"
            )
            is Results.Success -> {
            tempBookers = bookers.data.associateBy { it.id }
            }
            }
            }//Bookers with ids
            /*------GENERAL INIT--------*/
            _scanners.value = tempScanners.keys.associateWith {
            tempScanners[it]!!.apply { booker = tempBookers[it]!! }
            }
            _recruitedScanners.value = tempStationScannerIds.associateWith { true }
            }
            }
            }*/
            onLoading(false)
            _isInitComplete.value = true
        }
    }

    fun onToolboxVisibilityChange(new: Boolean) {
        _isToolboxVisible.value = new
    }

    fun onErrorChange(new: Boolean) {
        _isError.value = new
    }

    fun onQueryChange(query: String) = viewModelScope.launch {
        _isToolboxVisible.value = false
        _query.value = query.trim().lowercase()
        //Updating autoComplete options

        _scanners.value = _allScanners.value.filter {
            val scanner = it.value
            when (_queryFields.value.first()) {
                R.string.lb_name -> scanner.booker.name!!.contains(_query.value)
                R.string.lb_email -> scanner.booker.email!!.contains(_query.value)
                R.string.lb_phone -> "${scanner.booker.phoneCode}${scanner.booker.phone}".contains(
                    _query.value
                )
                R.string.lb_gender -> scanner.booker.genderID!!.contains(_query.value)
                else -> false
            }
        }
        onErrorChange(_scanners.value.isEmpty())
    }

    fun selectAll() {
        viewModelScope.launch {
            _selectedScanners.value = _selectedScanners.value.toMutableMap().apply {
                _scanners.value.keys.forEach { this[it] = true }
            }.toMap()
        }
    }

    fun deselectAll() {
        viewModelScope.launch {
            _selectedScanners.value = _selectedScanners.value.toMutableMap().apply {
                _scanners.value.keys.forEach { remove(it) }
            }.toMap()
        }
    }

    fun onFieldChange(@StringRes new: Int) {
        _queryFields.value.toMutableList().apply {
            val i = indexOf(new)
            val temp = first()
            set(0, new)
            set(i, temp)
        }.also {
            _queryFields.value = it
            viewModelScope.launch {
                onQueryChange("")
            }
        }
    }

    fun onFilterClick(filter: Filter) {
        val i = _filters.value.indexOfFirst { it.name == filter.name }
        _filters.value = _filters.value.toMutableList().apply {
            this[i] = this[i].copy(isSelected = !filter.isSelected)
        }.sortedBy { !it.isSelected }.toList()
    }

    fun isSelected(id: String) = _selectedScanners.value.contains(id)

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }
}

@Composable
fun EditStationPersonnel(
    vm: EditStationPersonnelVM = hiltViewModel(),
    navigateBack: () -> Unit,
    onComplete: () -> Unit,
) {

}