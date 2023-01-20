package tech.xken.tripbook.ui.screens.universe_editor

import android.util.Log
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
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.strAreTheSame
import tech.xken.tripbook.ui.navigation.UniverseDestinationArgs
import tech.xken.tripbook.ui.navigation.UniverseDestinationArgs.TOWNS_TO_EXCLUDE_BY_IDS
import tech.xken.tripbook.ui.navigation.UniverseDestinationArgs.UNIV_SEARCH_RESULT
import java.util.*
import javax.inject.Inject

data class TownSearchUiState(
    val message: String = "",
    val towns: List<Town> = listOf(),
    val searchStr: String = "",
    val searchFields: List<Int> = listOf(R.string.lb_town, R.string.lb_division, R.string.lb_region),
    val searchOptions: List<String> = listOf(),
) {
    val addTownStrRes
        get() = when (searchFields[0]) {
            R.string.lb_region -> R.string.lb_add_towns_from_regions
            R.string.lb_division -> R.string.lb_add_towns_from_division
            /*R.string.lb_town*/
            else -> R.string.lb_add_towns
        }
    val searchPlaceHolderStrRes
        get() = when (searchFields[0]) {
            R.string.lb_region  -> R.string.lb_region_name
            R.string.lb_division -> R.string.lb_division_name
            /*R.string.lb_town*/
            else -> R.string.lb_town_name
        }
    val isError = searchOptions.isEmpty() && searchStr.isNotBlank()
    val searchResults = searchFields[0] to searchOptions
}

/**
 * For searching for towns
 */
@HiltViewModel
class TownSearchVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: UniverseRepository,
) : ViewModel() {
    var allRegionsNames: List<String> = listOf()
    var allTownNames: List<String> = listOf()
    var allDivisionNames: List<String> = listOf()

    /**
     * These are town ids which have already been added to the or which we do not want to show in the results
     */
    private val _townsToExclude = savedStateHandle[TOWNS_TO_EXCLUDE_BY_IDS] ?: listOf<String>()

    private val _message = MutableStateFlow("")
    private val _searchStr = MutableStateFlow("")
    private val _searchFields = MutableStateFlow(listOf(R.string.lb_town, R.string.lb_division, R.string.lb_region))
    private val _searchOptions = MutableStateFlow(listOf<String>())
    private val _dismiss = MutableStateFlow(false)

    @Suppress("UNCHECKED_CAST")
    val uiState = combine(
        _searchStr,
        _searchFields,
        _searchOptions,
        _message,
        _dismiss
    ) { args ->
        TownSearchUiState(
            searchStr = args[0] as String,
            searchFields = args[1] as List<Int>,
            searchOptions = args[2] as List<String>,
            message = args[3] as String,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TownSearchUiState()
    )


    /**
     * Provides the autocompletion arrays for the search bar
     */
    fun getAutoCompleteOptions() = viewModelScope.launch {
        repo.townNames(_townsToExclude).also {
            if (it.succeeded) {
                allTownNames = it.data
            } else _message.value = "Error: ${it.exception.message}"
        }
        repo.townRegionNames(_townsToExclude).also {
            if (it.succeeded) {
                allRegionsNames = it.data
            } else _message.value = "Error: ${it.exception.message}"
        }
        repo.townDivisionsNames(_townsToExclude).also {
            if (it.succeeded) {
                allDivisionNames = it.data
            } else _message.value = "Error: ${it.exception.message}"
        }
    }

    private fun originalSearchOptions() =
        //Updating autoComplete options
        when (_searchFields.value[0]) {
            R.string.lb_region-> allRegionsNames
            R.string.lb_division -> allDivisionNames
            /*R.string.lb_town*/
            else -> allTownNames
        }

    /**
     * When the search field is changed by the user
     * @param newFieldIndex Is the index of the newly selected field and is always different from 0
     */
    fun searchFieldChange(newFieldIndex: Int) {
        //Swapping
        _searchFields.value.toMutableList().apply {
            val temp = this[0]
            this[0] = this[newFieldIndex]
            this[newFieldIndex] = temp
        }.also { _searchFields.value = it }
        //Updating autoComplete options
    }

    /**
     * When a user types a new letter in the search bar we filter out all the options which donot match the
     * query and leave those who match using the function [strAreTheSame]
     */
    fun searchStrChange(newString: String = "") {
        _searchStr.value = newString
        _searchOptions.value = when (_searchStr.value) {
            "" -> listOf()
            TOWN_SEARCH_LIST_ALL_CHAR -> originalSearchOptions()
            else -> {
                originalSearchOptions().filter { strAreTheSame(_searchStr.value, it) }
            }
        }

    }

    /**
     * We propagate the search results to the caller
     * The result is a pair with the first being the search field (e.g town name, town regions etc) and
     * the second is the list which was selected from the search
     */
    fun propagateResultsBeforeNavigateAway() {
        Log.d("AddTowns", "${uiState.value.searchResults}")
        savedStateHandle[UNIV_SEARCH_RESULT] = uiState.value.searchResults
        Log.d("AddTowns", (savedStateHandle.contains(UNIV_SEARCH_RESULT)).toString())
    }

    companion object {
        /**
         * This is used to list all possible results in search
         */
        const val TOWN_SEARCH_LIST_ALL_CHAR = "?"
    }

}