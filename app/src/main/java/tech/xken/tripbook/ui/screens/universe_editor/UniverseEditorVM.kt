package tech.xken.tripbook.ui.screens.universe_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.models.uistates.UniverseEditorUiS
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.parseStrRoads
import tech.xken.tripbook.domain.parseStrTowns
import javax.inject.Inject


/**
 * Uistate for the EditingScreen
 */
data class UniverseEditorUiState(
    val isLoading: Boolean = false,
    val percentProgression: Double = Double.NaN,
    val currentItem: String = "",
    val message: String? = null,
    val towns: List<Town> = listOf(),
    val roads: List<Road> = listOf(),
)

@HiltViewModel
class UniverseEditorVM @Inject constructor(
    private val repository: UniverseRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _percentProgression = MutableStateFlow(Double.NaN)
    private val _currentItem = MutableStateFlow("")
    private val _message = MutableStateFlow<String?>(null)
    private val _towns = MutableStateFlow(listOf<Town>())
    private val _roads = MutableStateFlow(listOf<Road>())

    /**
     * We initialise the uistate using the backend private and mutable states
     * Note the initial value, started and scope in the [stateIn] function
     */
    @Suppress("UNCHECKED_CAST")
    val uiState = combine(
        _isLoading, _currentItem, _percentProgression, _message, _towns, _roads
    ) { it -> //Respect order of parameter
        UniverseEditorUiState(
            isLoading = it[0]!! as Boolean,
            currentItem = it[1]!! as String,
            percentProgression = it[2]!! as Double,
            message = it[3] as String?,
            towns = it[4]!! as List<Town>,
            roads = it[5]!! as List<Road>
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = UniverseEditorUiState()
    )

    /**
     * Manages the selection of towns. Note that a town is marked when long clicked
     * Marks town if not found in []
     */
    fun markSelectedTown(id: String){

    }

    /**
     * Admin function called to fill the local database with towns
     * @param strTowns is to be from the resource containing all the towns
     */
    fun addTowns(strTowns: Array<String>) {
        started("Started adding towns")
        val total = strTowns.size
        viewModelScope.launch {
            val towns = parseStrTowns(strTowns) { _, _ -> }
            repository.saveTowns(towns)
            finished("Finished adding towns. Added $total Towns")
        }
    }

    /**
     * Admin function called to fill the local database with Roads and itinerary
     * @param strRoads is to be from the resource containing all the roads details
     */
    fun addRoads(strRoads: Array<String>) {
        started("Started adding Roads")
        val total = strRoads.size
        viewModelScope.launch {
            val roads = parseStrRoads(strRoads, _towns.value) { _, _ -> }
            repository.saveRoads(roads)
            val allTownPairs = mutableListOf<TownPair>()
            roads.forEachIndexed { _, road ->
                allTownPairs += TownPair.townPairsFromTownsIds(road.itineraryTownsIds, road.id)
            }
            repository.saveTownPairs(allTownPairs)
            finished("Finished: added $total roads, and ${allTownPairs.size} Town pairs")
        }
    }

    fun getTowns() {
        started()
        viewModelScope.launch {
            repository.towns().also {
                if (it.succeeded) {
                    _towns.value = it.data
                    finished("Got ${_towns.value.size} towns")
                } else finished("Error: ${it.exception.message}")
            }
        }
    }

    fun getRoads() {
        started()
        viewModelScope.launch {
            repository.roads().also {
                if (it.succeeded) {
                    _roads.value = it.data
                    finished("Got ${_roads.value.size} roads")
                } else finished("Error: ${it.exception.message}")
            }
        }
    }

    /**
     * Called when we want to start a blocking operation
     * @param msg Message to show went the operation is started
     * @param percentCompletion is the value of the progress at the beginning of the operation. Default is [Double.NaN] if the operation
     * is of indeterminate progress
     */
    private fun started(msg: String = "", percentCompletion: Double = Double.NaN) {
        _isLoading.value = true
        _message.value = msg.ifBlank { "Operation Started!!" }
        if (!percentCompletion.isNaN()) _percentProgression.value = percentCompletion
    }

    /**
     * Called when we have finished a blocking operation
     */
    private fun finished(msg: String = "") {
        _currentItem.value = ""
        _message.value = msg.ifBlank { "Operation Completed!!" }
        _isLoading.value = false
        _percentProgression.value = Double.NaN
    }

    fun snackbarMessageShown() {
        _message.value = null
    }

    private fun showSnackbarMessage(message: String) {
        _message.value = message
    }

}
/**
 * The [UniverseEditorUiS] ui state
var uiS by mutableStateOf(UniverseEditorUiS())
private set

lateinit var repo: Repository
fun initRepo(appContext: Context) {
repo = Repository(
localDataSource = UniverseLocalDataSourceImpl.instance(appContext),
dispatcher = Dispatchers.IO
)
}

 */
/**
 * A function to edit and modify the [uiS]
 *//*
    fun updateUiS(update: (oldUis: UniverseEditorUiS) -> UniverseEditorUiS) {
        uiS = update(uiS)
    }

    */
/**
 * Used by views and composable to update the [MasterUI]
 * @param update is a lambda which returns the new [MasterUI] from the old one
 *//*
    fun updateMasterUi(update: (oldMaster: MasterUI) -> MasterUI) {
        uiS = uiS.copy(masterUI = update(uiS.masterUI))
    }*/
