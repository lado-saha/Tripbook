package tech.xken.tripbook.ui.screens.universe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.domain.*
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
    private val repo: UniverseRepository,
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
     * Admin function called to fill the local database with towns
     * @param univStrings is to be from the resource containing all the towns
     */
    fun saveUniverse(univStrings: Array<String>) {
        started("Started saving universe")
        viewModelScope.launch {
            started(percentCompletion = 0.0)
            repo.savePlanets(parsePlanets())
            started(percentCompletion = 20.0)
            Log.d("Universe", "Universe Starting ....")
            repo.saveContinents(parseContinents {
                Log.d("Parser", it)
                repo.planetsFromNames(listOf(it)).data.first()
            })
            started(percentCompletion = 40.0)
            Log.d("Universe", "Universe FINISH")
            Log.d("Universe", "Universe Starting ....")
            repo.saveCountries(parseCountries {
                Log.d("Parser", it)
                repo.continentsFromNames(listOf(it)).data.first()
            })
            started(percentCompletion = 60.0)
            Log.d("Universe", "Universe  FINISH")
            Log.d("Universe", "Universe Starting ....")
            repo.saveRegions(parseRegions(univStrings) {
                Log.d("Parser", it)
                repo.countriesFromNames(listOf(it)).data.first()
            })
            started(percentCompletion = 70.0)
            Log.d("Universe", "Universe  FINISH")
            Log.d("Universe", "Universe Starting ....")
            repo.saveDivisions(parseDivisions(univStrings) {
                Log.d("Parser", it)
                repo.regionsFromNames(listOf(it)).data.first()
            })
            started(percentCompletion = 75.0)
            Log.d("Universe", "Universe  FINISH")
            Log.d("Universe", "Universe Starting ....")
            repo.saveSubdivisions(parseSubdivisions(univStrings) {
                Log.d("Parser", it)
                repo.divisionsFromNames(listOf(it)).data.first()
            })
            started(percentCompletion = 80.0)
            Log.d("Universe", "Universe  FINISH")
            Log.d("Universe", "Universe Starting ....")
            repo.saveTowns(parseTowns(univStrings) {
                Log.d("Parser", it)
                repo.subdivisionsFromNames(listOf(it)).data.first()
            })
            Log.d("Universe", "Universe  FINISH")
            started(percentCompletion = 100.0)
            finished("Finished")
            _isLoading.value = false
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
            repo.saveRoads(roads)
            val allTownPairs = mutableListOf<TownPair>()
            roads.forEachIndexed { _, road ->
                allTownPairs += TownPair.townPairsFromTownsIds(road.itineraryTownsIds, road.id)
            }
            repo.saveTownPairs(allTownPairs)
            finished("Finished: added $total roads, and ${allTownPairs.size} Town pairs")
        }
    }

    fun getTowns() {
        started()
        viewModelScope.launch {
            repo.towns().also { towns ->
                if (towns.succeeded) {
                    _towns.value = towns.data.sortedBy { it.subdivision }
                    finished("Got ${_towns.value.size} towns")
                } else finished("Error: ${towns.exception.message}")
            }
        }
    }

    fun getRoads() {
        started()
        viewModelScope.launch {
            repo.roads().also {
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
