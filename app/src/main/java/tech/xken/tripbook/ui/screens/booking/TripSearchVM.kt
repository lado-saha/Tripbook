package tech.xken.tripbook.ui.screens.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Trip
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.models.exception
import tech.xken.tripbook.data.models.succeeded
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import javax.inject.Inject

data class TripSearchUiState(
    val from: String = "",
    val to: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val message: Int? = null,
    val townNames: List<String> = listOf(),
    val isShowingResults: Boolean = false,
    val tripSearchResults: List<Trip> = listOf(),
    val fromFilteredNames: List<String> = listOf(),
    val toFilteredNames: List<String> = listOf(),
    val isFromExpanded: Boolean = false,
    val isToExpanded: Boolean = false,
)

@HiltViewModel
class TripSearchVM @Inject constructor(
    private val repo: UniverseRepository,
) : ViewModel() {
    private val _from = MutableStateFlow("")
    private val _to = MutableStateFlow("")
    private val _message = MutableStateFlow<Int?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _townNames = MutableStateFlow(listOf<String>())
    private val _isShowingResults = MutableStateFlow(false)
    private val _tripSearchResults = MutableStateFlow<List<Trip>>(listOf())
    private val _isFromExpanded = MutableStateFlow(false)
    private val _isToExpanded = MutableStateFlow(false)
/*    private val _fromFilteredNames = MutableStateFlow<List<String>>(listOf())
    private val _toFilteredNames = MutableStateFlow<List<String>>(listOf())*/

    init {
        initTripSearch()
    }

    val uiState = combine(
        _from,//0
        _to,//1
        _isLoading,//2
        _isComplete,//3
        _message,//4
        _townNames,//5,
        _isShowingResults,//6
        _tripSearchResults,//7
        _isFromExpanded,//8
        _isToExpanded//9
    ) {
        TripSearchUiState(
            from = it[0] as String,
            to = it[1] as String,
            isLoading = it[2] as Boolean,
            isComplete = it[3] as Boolean,
            message = it[4] as Int?,
            townNames = it[5] as List<String>,
            isShowingResults = it[6] as Boolean,
            tripSearchResults = it[7] as List<Trip>,
            fromFilteredNames = filterNamesFrom(it[0] as String),
            toFilteredNames = filterNamesFrom(it[1] as String),
            isFromExpanded = it[8] as Boolean,
            isToExpanded = it[9] as Boolean
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = TripSearchUiState()
    )

    private fun initTripSearch() = viewModelScope.launch {
        onLoading(true)
        repo.townNames(listOf()).also {
            if (it.succeeded) {
                _townNames.value = it.data
            } else {
                Log.e("TRIP SEARCH", "Error: ${it.exception.message}")
                _message.value = R.string.msg_unexpexted_error
            }
        }
        onLoading(false)
    }

    fun filterNamesFrom(str: String) = when {
        str.isBlank() -> _townNames.value
        else -> _townNames.value.filter { it.contains(str.trim(), true) }
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onFromChange(new: String) {
        _from.value = new
    }

    fun onFromExpandedChange(new: Boolean) {
        _isFromExpanded.value = new
    }

    fun onToExpandedChange(new: Boolean) {
        _isToExpanded.value = new
    }

    fun onToChange(new: String) {
        _to.value = new
    }

    val isFromError = when {
        _from.value == "" -> R.string.msg_required_field
        _townNames.value.binarySearch(_from.value.lowercase()) == -1 -> R.string.msg_unknown_town_error
        else -> null
    }

    val isToError = when {
        _to.value == "" -> R.string.msg_required_field
        _townNames.value.binarySearch(
            _to.value.trim().lowercase()
        ) == -1 -> R.string.msg_unknown_town_error
        _from.value.trim().lowercase() == _to.value.trim()
            .lowercase() -> R.string.msg_from_to_town_same_error
        else -> null
    }
}