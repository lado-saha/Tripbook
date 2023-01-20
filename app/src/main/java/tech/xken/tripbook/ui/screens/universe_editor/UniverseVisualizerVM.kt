package tech.xken.tripbook.ui.screens.universe_editor

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.ui.navigation.UniverseDestinationArgs.UNIV_SEARCH_RESULT
import javax.inject.Inject

data class UniverseVisualizerUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val towns: Map<String, TownNode> = mapOf(),
    val roads: Map<String, Road> = mapOf(),
    val isSearching: Boolean = false,
    // Is empty when the highlighted road is not
    val selectedTowns: List<String> = listOf(),
    val translation: Offset = Offset.Zero,
    val isToolboxVisible: Boolean = true,
    val townFocus: String = "",
) {
    val isFabResetPosition get() = translation != Offset.Zero && isToolboxVisible
    val isFabTownAddVisible get() = isToolboxVisible
    val isFabRoadAddVisible get() = selectedTowns.size >= 2 && isToolboxVisible
    val isFabDetailsVisible get() = selectedTowns.size == 1 && isToolboxVisible
    val isFabDeleteVisible get() = selectedTowns.isNotEmpty() && isToolboxVisible
    val isFabHelpVisible get() = isToolboxVisible
}

@HiltViewModel
class UniverseVisualizerVM @Inject constructor(
    private val repo: UniverseRepository,
    var savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _translation = MutableStateFlow(Offset.Zero)
    private val _message = MutableStateFlow("")
    private val _towns = MutableStateFlow(mapOf<String, TownNode>())
    private val _roads = MutableStateFlow(mapOf<String, Road>())
    private val _isSearching = MutableStateFlow(false)

    private val _selectedTowns = MutableStateFlow(listOf<String>())
    private val _isToolboxVisible = MutableStateFlow(true)
    private val _townFocus = MutableStateFlow("")



    @Suppress("UNCHECKED_CAST")
    val uiState = combine(
        _isLoading,//0
        _message,//1
        _towns,//2
        _roads,//3
        _isSearching,//4
        _selectedTowns,//6
        _townFocus,//7
        _isToolboxVisible,//8
        _translation,//9
    ) { args ->
        UniverseVisualizerUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as String,
            towns = args[2] as Map<String, TownNode>,
            roads = args[3] as Map<String, Road>,
            isSearching = args[4] as Boolean,
            selectedTowns = args[5] as List<String>,
            townFocus = args[6] as String,
            isToolboxVisible = args[7] as Boolean,
            translation = args[8] as Offset,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = UniverseVisualizerUiState()
    )

    /**
     * Used for clearing and or updating the focus town
     * @param id Is the id of a town
     */
    fun onNodeClicked(id: String) {
        if (_selectedTowns.value.isEmpty())
            _townFocus.value = if (_townFocus.value != id) id else ""
        else
            onNodeLongClicked(id)
    }

    fun onNodeLongClicked(id: String) {
        if (_selectedTowns.value.contains(id)) _selectedTowns.value -= id
        else _selectedTowns.value += id
    }

    /**
     * Manages the scaling and translations of the background grid
     */
    fun onScreenDragged(pan: Offset) {
        _translation.value += pan
    }

    fun toggleToolboxVisibility() {
        _isToolboxVisible.value = !_isToolboxVisible.value
    }

    fun isTownMarked(id: String) = _selectedTowns.value.contains(id) || _townFocus.value == id

    /**
     * We clear all the
     */
    fun clearSelectedTowns() {
        _selectedTowns.value = emptyList()
    }

    fun clearTownFocus() {
        _townFocus.value = ""
    }

    /**
     * Deletes highlighted Towns from the graph
     */
    fun deleteSelectedTowns() {
        _selectedTowns.value.forEach { townId ->
            //We first remove the town
            _towns.value -= townId

            // Get all roads containing the town and remove them
            val concernedRoads =
                _roads.value.values.filter { it.town1 == townId || it.town2 == townId }
            concernedRoads.forEach { road ->
                _roads.value -= road.id
            }
        }
        //  Lastly we clear the highlighted towns
        clearSelectedTowns()
    }


    /**
     * We obtain the results after a search and get the required towns
     * [_searchResults] is a pair with 1 - Being the field(region, town or division) and 2 - The corresponding value(s)(list of regions, list of towns names etc)
     * We get all the towns in each case e.g all towns in each region or division OR all towns from their town names
     */
    fun addTowns() = viewModelScope.launch {
        savedStateHandle.getStateFlow<Pair<Int, List<String>>?>(UNIV_SEARCH_RESULT, null)
            .collectLatest { searchResults ->
                Log.d("UNIV_SEARCH_RESULT", searchResults.toString())
                if (searchResults != null) {
                    _isLoading.value = true
                    when (searchResults.first) {
                        R.string.lb_town -> {
                            val townsNames = searchResults.second
                            repo.townsFromNames(townsNames).also { results ->
                                if (results.succeeded) {
                                    // We convert all town objects into TownNodes objects ready for ui consumption
                                    _towns.value += results.data.map { TownNode(town = it) }
                                        .associateBy { it.town.id }
                                    Log.d("AddTowns", "${_towns.value.entries}")
                                } else _message.value = "${results.exception}"
                            }
                        }
                        R.string.lb_region -> {
                            val regions = searchResults.second
                            regions.forEach { region ->
                                repo.townsFromRegion(region).also { results ->
                                    if (results.succeeded) {
                                        _towns.value +=
                                            results.data.map { TownNode(town = it) }
                                                .associateBy { it.town.id }
                                    } else _message.value = "${results.exception}"
                                }
                            }
                        }
                        R.string.lb_division -> {
                            val divisions = searchResults.second
                            divisions.forEach { division ->
                                repo.townsFromDivision(division).also { results ->
                                    if (results.succeeded) {
                                        _towns.value +=
                                            results.data.map { TownNode(town = it) }
                                                .associateBy { it.town.id }
//                                addTownToMasterUi(idToTowns.keys)
                                    } else _message.value = "${results.exception}"
                                }
                            }
                        }
                    }
                    _isLoading.value = false
                }
            }
    }


    /**
     * We connect the highlighted towns or in other words, we add the towns between then in order i.e
     * From the highlighted town list, we get the road from elements: n to n+1 (with n >= 0) e.g 0->1 , 1->2, 2-> ..
     * NB: size of highlighted towns >=2
     */
    fun connectHighlightedTownsInOrderOfSelection() {
        _isLoading.value = true
        viewModelScope.launch {
            val n = _selectedTowns.value.size - 1
            (0 until n).forEach { i ->
                repo.roadBtn2TownsFromIds(
                    id1 = _selectedTowns.value[i],
                    id2 = _selectedTowns.value[i + 1]
                ).also { results ->
                    if (results.succeeded) {
                        _roads.value += (results.data.id to results.data)
                    } else _message.value = "${results.exception}"
                }
            }
            _isLoading.value = false
        }
    }

    //Called to update the from btn position of a town node
    fun onPinPointPositioned(id: String, newPosition: Offset) {
        _towns.value += id to _towns.value[id]!!.copy(pinPoint = _towns.value[id]!!.pinPoint + newPosition)
    }


    //Called when the node is dragged
    fun updateNodePosition(id: String, newPosition: Offset) {
        _towns.value += id to _towns.value[id]!!.copy(offset = _towns.value[id]!!.offset + newPosition)
    }

    fun clearMessage() {
        _message.value = ""
    }
}


