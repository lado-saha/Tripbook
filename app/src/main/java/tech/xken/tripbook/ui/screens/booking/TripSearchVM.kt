package tech.xken.tripbook.ui.screens.booking

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.univ.UniverseRepository
import tech.xken.tripbook.domain.DATE_NOW
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.format
import tech.xken.tripbook.ui.components.RoadItemUiState
import javax.inject.Inject

data class TripSearchUiState(
    val from: String = "",
    val to: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val message: Int? = null,
    val isFromExpanded: Boolean = false,
    val isToExpanded: Boolean = false,
    val departureDate: LocalDate = DATE_NOW,
    val fromFilteredNames: List<String> = listOf(),
    val toFilteredNames: List<String> = listOf(),
    val townNames: List<String> = listOf(),
    val roadUis: RoadItemUiState? = null,
    val isShowingRoad: Boolean = false
) {
    val formattedDepartureDate = departureDate.format()

}

@HiltViewModel
class TripSearchVM @Inject constructor(
    private val univRepo: UniverseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _from = MutableStateFlow("")
    private val _to = MutableStateFlow("")
    private val _message = MutableStateFlow<Int?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _isFromExpanded = MutableStateFlow(false)
    private val _isToExpanded = MutableStateFlow(false)
    private val _departureDate = MutableStateFlow(DATE_NOW)
    private val _fromFilteredNames = MutableStateFlow(listOf<String>())
    private val _toFilteredNames = MutableStateFlow(listOf<String>())
    private val _townNames = MutableStateFlow(listOf<String>())
    private val _roadUis = MutableStateFlow<RoadItemUiState?>(null)
    private val _isShowingRoad = MutableStateFlow(false)


    @Suppress("UNCHECKED_CAST")
    val uiState = combine(
        _from,//0
        _to,//1
        _isLoading,//2
        _isComplete,//3
        _message,//4
//        _tripSearchResults,//5
        _isFromExpanded,//5
        _isToExpanded,//6
        _departureDate,//7
        _fromFilteredNames,
        _toFilteredNames,
        _townNames,
        _roadUis,
        _isShowingRoad

    ) {
        TripSearchUiState(
            from = it[0] as String,
            to = it[1] as String,
            isLoading = it[2] as Boolean,
            isComplete = it[3] as Boolean,
            message = it[4] as Int?,
//            tripSearchResults = it[5] as MutableStateFlow<>
            isFromExpanded = it[5] as Boolean,
            isToExpanded = it[6] as Boolean,
            departureDate = it[7] as LocalDate,
            fromFilteredNames = it[8] as List<String>,
            toFilteredNames = it[9] as List<String>,
            townNames = it[10] as List<String>,
            roadUis = it[11] as RoadItemUiState?,
            isShowingRoad = it[12] as Boolean
        )


    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = TripSearchUiState()
    )

//    private fun initTripSearch() = viewModelScope.launch {
//        onLoading(true)
//        repo.townNames(listOf()).also {
//            if (it.succeeded) {
//                _townNames.value = it.data
//            } else {
//                Log.e("TRIP SEARCH", "Error: ${it.exception.message}")
//                _message.value = R.string.msg_unexpexted_error
//            }
//        }
//        onLoading(false)
//    }

    fun onRoadPathUisChange(new: RoadItemUiState?) {
        _roadUis.value = new
    }

    fun onShowRoadChange(new: Boolean) {
        _isShowingRoad.value = new
    }

    fun initTownNames(names: Array<String>) {
        _townNames.value = names.toList()
    }

    private fun filterFromNames(str: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (str.length >= 2)
                _fromFilteredNames.value = _townNames.value.filter { it.contains(str.trim(), true) }
            else
                _fromFilteredNames.value = listOf()
        }
    }

    private fun filterToNames(str: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (str.length >= 2)
                _toFilteredNames.value = _townNames.value.filter { it.contains(str.trim(), true) }
            else
                _toFilteredNames.value = listOf()
        }
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onFromChange(new: String) {
        _from.value = new
        filterFromNames(new)
    }

    fun onFromExpandedChange(new: Boolean) {
        _isFromExpanded.value = new
    }

    fun onToExpandedChange(new: Boolean) {
        _isToExpanded.value = new

    }

    fun onToChange(new: String) {
        _to.value = new
        filterToNames(new)
    }

    fun onDepartureDateChange(new: LocalDate) {
        _departureDate.value = new
    }

    fun isDepartureDateError(departureDate: LocalDate = _departureDate.value) = when {
        departureDate < DATE_NOW -> R.string.msg_invalid_departure_date_in_past
        departureDate > DATE_NOW.plus(
            30,
            DateTimeUnit.DAY
        ) -> R.string.msg_invalid_departure_date_in_future

        else -> null
    }

    fun clearResults() {
        _isShowingRoad.value = false
        onRoadPathUisChange(null)
    }

    fun isFromError(str: String = _from.value) = when {
        str == "" -> R.string.msg_required_field
        _fromFilteredNames.value.indexOf(
            str.trim().lowercase()
        ) == -1 -> R.string.msg_unknown_town_error

        else -> null
    }

    fun isToError(str: String = _to.value) = when {
        str == "" -> R.string.msg_required_field
        _toFilteredNames.value.indexOf(
            str.trim().lowercase()
        ) == -1 -> R.string.msg_unknown_town_error

        str.trim().lowercase() == _from.value.trim()
            .lowercase() -> R.string.msg_from_to_town_same_error

        else -> null
    }

    fun swapTrip() {
        val temp = _from.value
        onFromChange(_to.value)
        onToChange(temp)
    }

    fun onMessageChange(new: Int? = _message.value) {
        _message.value = new
    }

    fun search(
        doOnStart: () -> Unit = {},
        doOnFinish: () -> Unit = {}
    ) {
        if ( isFromError() == null && isToError() == null && isDepartureDateError() == null)
            viewModelScope.launch {
                onLoading(true)
                doOnStart()
                univRepo.fullRoadFromTownNames(_from.value.lowercase(), _to.value.lowercase())
                    .also {
                        when (it) {
                            is Failure -> {
                                Log.e("Road", "${it.exception}")
                            }

                            is Success -> {
                                onRoadPathUisChange(
                                    RoadItemUiState(
                                        road = it.data,
                                        isSelected = true,
                                        showPath = true,
                                        isReversed = false
                                    )
                                )
                                onShowRoadChange(true)
                            }
                        }
                    }
                onLoading(false)
            }
        else onMessageChange(R.string.msg_fields_contain_errors)
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

}