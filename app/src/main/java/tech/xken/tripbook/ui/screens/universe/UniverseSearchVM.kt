package tech.xken.tripbook.ui.screens.universe

import androidx.annotation.StringRes
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
import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.ui.components.Filter
import tech.xken.tripbook.ui.navigation.UniverseArgs
import java.util.*
import javax.inject.Inject

data class UniverseSearchUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val query: String = "",
    val queryFields: List<Int> = listOf(
//        R.string.lb_town,
        R.string.lb_planet,
//        R.string.lb_continent,
//        R.string.lb_country,
//        R.string.lb_region,
//        R.string.lb_division,
//        R.string.lb_subdivision
    ),
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,

    val allPlanets: Map<String, Planet> = mapOf(),
    val allContinents: Map<String, Continent> = mapOf(),
    val allCountries: Map<String, Country> = mapOf(),
    val allRegions: Map<String, Region> = mapOf(),
    val allDivisions: Map<String, Division> = mapOf(),
    val allSubdivision: Map<String, Subdivision> = mapOf(),
    val allTowns: Map<String, Town> = mapOf(),

    val planets: Map<String, Planet> = mapOf(),
    val continents: Map<String, Continent> = mapOf(),
    val countries: Map<String, Country> = mapOf(),
    val regions: Map<String, Region> = mapOf(),
    val divisions: Map<String, Division> = mapOf(),
    val subdivision: Map<String, Subdivision> = mapOf(),
    val towns: Map<String, Town> = mapOf(),
    val filters: List<Filter> = listOf(
        Filter(R.string.lb_selected), Filter(R.string.lb_all_selected)
    ),
    val isFiltersVisible: Boolean = true,
    val isError: Boolean = false,
    val isEmptyResults: Boolean = false,
    //Map<id, parentId>
    val selectedPlanets: Map<String, Boolean> = mapOf(),
    val selectedContinents: Map<String, Boolean> = mapOf(),
    val selectedCountries: Map<String, Boolean> = mapOf(),
    val selectedRegions: Map<String, Boolean> = mapOf(),
    val selectedDivisions: Map<String, Boolean> = mapOf(),
    val selectedSubdivisions: Map<String, Boolean> = mapOf(),
    val selectedTowns: Map<String, Boolean> = mapOf(),
) {
    val isSelectionMode get() = !isError && filters.find { it.name == R.string.lb_selected }!!.isSelected
    val isNormalMode get() = !isSelectionMode && !isError
}

/**
 * For searching for towns
 */
@HiltViewModel
class UniverseSearchVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: UniverseRepository,
    private val cachesRepo: CachesRepository
) : ViewModel() {
    private val _queryFields = MutableStateFlow(
        (savedStateHandle[UniverseArgs.UNIV_SEARCH_FIELDS] ?: "${R.string.lb_town}").trim()
            .split(" ")
            .map { it.toInt() }

    )
    private val _allPlanets = MutableStateFlow(mapOf<String, Planet>())
    private val _allContinents = MutableStateFlow(mapOf<String, Continent>())
    private val _allCountries = MutableStateFlow(mapOf<String, Country>())
    private val _allRegions = MutableStateFlow(mapOf<String, Region>())
    private val _allDivisions = MutableStateFlow(mapOf<String, Division>())
    private val _allSubdivisions = MutableStateFlow(mapOf<String, Subdivision>())
    private val _allTowns = MutableStateFlow(mapOf<String, Town>())

    private val _selectedPlanets = MutableStateFlow(mapOf<String, Boolean>())
    private val _selectedContinents = MutableStateFlow(mapOf<String, Boolean>())
    private val _selectedCountries = MutableStateFlow(mapOf<String, Boolean>())
    private val _selectedRegions = MutableStateFlow(mapOf<String, Boolean>())
    private val _selectedDivisions = MutableStateFlow(mapOf<String, Boolean>())
    private val _selectedSubdivisions = MutableStateFlow(mapOf<String, Boolean>())
    private val _selectedTowns = MutableStateFlow(mapOf<String, Boolean>())

    private val _planets = MutableStateFlow(mapOf<String, Planet>())
    private val _continents = MutableStateFlow(mapOf<String, Continent>())
    private val _countries = MutableStateFlow(mapOf<String, Country>())
    private val _regions = MutableStateFlow(mapOf<String, Region>())
    private val _divisions = MutableStateFlow(mapOf<String, Division>())
    private val _subdivisions = MutableStateFlow(mapOf<String, Subdivision>())
    private val _towns = MutableStateFlow(mapOf<String, Town>())

    /**
     * These are town ids which have already been added to the or which we do not want to show in the results
     */
    private val _message = MutableStateFlow<Int?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    private val _filters = MutableStateFlow(
        listOf(
            Filter(R.string.lb_selected), Filter(R.string.lb_all_selected)
        )
    )
    private val _isFiltersVisible = MutableStateFlow(true)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _isError = MutableStateFlow(false)
    private val _isEmptyResult = MutableStateFlow(false)

    @Suppress("UNCHECKED_CAST")
    val uiState = combine(
        _query,
        _queryFields,
        _message,
        _isInitComplete,
        _allPlanets,
        _allContinents,
        _allCountries,
        _allRegions,
        _allDivisions,
        _allSubdivisions,
        _allTowns,
        _planets,
        _continents,
        _countries,
        _regions,
        _divisions,
        _subdivisions,
        _towns,
        _isLoading,
        _isComplete,
        _filters,
        _isFiltersVisible,
        _isError,
        _isEmptyResult,
        _selectedPlanets,
        _selectedContinents,
        _selectedCountries,
        _selectedRegions,
        _selectedDivisions,
        _selectedSubdivisions,
        _selectedTowns
    ) {
        UniverseSearchUiState(
            query = it[0] as String,
            queryFields = it[1] as List<Int>,
            message = it[2] as Int?,
            isInitComplete = it[3] as Boolean,
            allPlanets = it[4] as Map<String, Planet>,
            allContinents = it[5] as Map<String, Continent>,
            allCountries = it[6] as Map<String, Country>,
            allRegions = it[7] as Map<String, Region>,
            allDivisions = it[8] as Map<String, Division>,
            allSubdivision = it[9] as Map<String, Subdivision>,
            allTowns = it[10] as Map<String, Town>,
            planets = it[11] as Map<String, Planet>,
            continents = it[12] as Map<String, Continent>,
            countries = it[13] as Map<String, Country>,
            regions = it[14] as Map<String, Region>,
            divisions = it[15] as Map<String, Division>,
            subdivision = it[16] as Map<String, Subdivision>,
            towns = it[17] as Map<String, Town>,
            isLoading = it[18] as Boolean,
            isComplete = it[19] as Boolean,
            filters = it[20] as List<Filter>,
            isFiltersVisible = it[21] as Boolean,
            isError = it[22] as Boolean,
            isEmptyResults = it[23] as Boolean,
            selectedPlanets = it[24] as Map<String, Boolean>,
            selectedContinents = it[25] as Map<String, Boolean>,
            selectedCountries = it[26] as Map<String, Boolean>,
            selectedRegions = it[27] as Map<String, Boolean>,
            selectedDivisions = it[28] as Map<String, Boolean>,
            selectedSubdivisions = it[29] as Map<String, Boolean>,
            selectedTowns = it[30] as Map<String, Boolean>,
        ).also { initSearch() }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = UniverseSearchUiState()
    )

    private fun initSearch() {
        if (!_isInitComplete.value)
            viewModelScope.launch {
                onLoading(true)
                if (_queryFields.value.contains(R.string.lb_planet))
                    repo.planets().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allPlanets.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }

                if (_queryFields.value.contains(R.string.lb_continent))
                    repo.continents().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allContinents.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }

                if (_queryFields.value.contains(R.string.lb_country))
                    repo.countries().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allCountries.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }

                if (_queryFields.value.contains(R.string.lb_region))
                    repo.regions().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allRegions.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }

                if (_queryFields.value.contains(R.string.lb_division))
                    repo.divisions().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allDivisions.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }

                if (_queryFields.value.contains(R.string.lb_subdivision))
                    repo.subdivisions().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allSubdivisions.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }

                if (_queryFields.value.contains(R.string.lb_town))
                    repo.towns().also { data ->
                        when (data) {
                            is Failure -> {}
                            is Success -> _allTowns.value =
                                data.data.sortedBy { it.name }.associateBy { it.id }
                        }
                    }
                onQueryChange("")
                _isInitComplete.value = true
                onLoading(false)
            }
    }

    fun onErrorChange(new: Boolean) {
        _isError.value = new
    }


    fun onResultSelection(id: String) = viewModelScope.launch {
        when (_queryFields.value.first()) {
            R.string.lb_planet -> {
                _selectedPlanets.value = _selectedPlanets.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
            R.string.lb_continent -> {
                _selectedContinents.value = _selectedContinents.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
            R.string.lb_country -> {
                _selectedCountries.value = _selectedCountries.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
            R.string.lb_region -> {
                _selectedRegions.value = _selectedRegions.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
            R.string.lb_division -> {
                _selectedDivisions.value = _selectedDivisions.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
            R.string.lb_subdivision -> {
                _selectedSubdivisions.value = _selectedSubdivisions.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
            R.string.lb_town -> {
                _selectedTowns.value = _selectedTowns.value.toMutableMap().apply {
                    if (this[id] == null) this[id] = true else remove(id)
                }.toMap()
            }
        }
    }

    fun onQueryChange(query: String) = viewModelScope.launch {
        _query.value = query
        //Updating autoComplete options
        when (_queryFields.value.first()) {
            R.string.lb_planet -> _planets.value =
                _allPlanets.value.filter { it.value.name!!.contains(query, true) }
            R.string.lb_continent -> _continents.value =
                _allContinents.value.filter { it.value.name!!.contains(query, true) }
            R.string.lb_country -> _countries.value =
                _allCountries.value.filter { it.value.name!!.contains(query, true) }
            R.string.lb_region -> _regions.value =
                _allRegions.value.filter { it.value.name!!.contains(query, true) }
            R.string.lb_division -> _divisions.value =
                _allDivisions.value.filter { it.value.name!!.contains(query, true) }
            R.string.lb_subdivision -> _subdivisions.value =
                _allSubdivisions.value.filter { it.value.name!!.contains(query, true) }
            R.string.lb_town -> _towns.value =
                _allTowns.value.filter { it.value.name!!.contains(query, true) }
        }
        onErrorChange(searchResults().isEmpty())
    }

    fun searchResults() = when (_queryFields.value.first()) {
        R.string.lb_planet -> _planets.value
        R.string.lb_continent -> _continents.value
        R.string.lb_country -> _countries.value
        R.string.lb_region -> _regions.value
        R.string.lb_division -> _divisions.value
        R.string.lb_subdivision -> _subdivisions.value
        //Towns
        else -> _towns.value
    }

    /**
     * When the search field is changed by the user
     * @param new Is the index of the newly selected field and is always different from 0
     */
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

    /**
     * @param id Is the id for the current place
     * @param id Is the id of the parent of the child place e.g placeId for Town and parentId for subdivision
     */
    fun isParentSelected(
        id: String,
        queryField: Int = _queryFields.value.first(),
    ): Boolean =
        when (queryField) {
            R.string.lb_planet -> false
            R.string.lb_continent -> _allContinents.value[id]?.planet?.let {
                _selectedPlanets.value.contains(it)
            }
            R.string.lb_country -> _allCountries.value[id]?.continent?.let {
                _selectedContinents.value.contains(it) || isParentSelected(
                    it,
                    R.string.lb_continent
                )
            }
            R.string.lb_region -> _allRegions.value[id]?.country?.let {
                _selectedCountries.value.contains(it) || isParentSelected(it, R.string.lb_country)
            }
            R.string.lb_division -> _allDivisions.value[id]?.region?.let {
                _selectedRegions.value.contains(it) || isParentSelected(it, R.string.lb_region)
            }
            R.string.lb_subdivision -> _allSubdivisions.value[id]?.division?.let {
                _selectedDivisions.value.contains(it) || isParentSelected(it, R.string.lb_division)
            }
            else -> _allTowns.value[id]?.subdivision?.let {
                _selectedSubdivisions.value.contains(it) || isParentSelected(
                    it,
                    R.string.lb_subdivision
                )
            }
        } ?: false

    fun isSelected(
        id: String,
        queryField: Int = _queryFields.value.first(),
    ) = when (queryField) {
        R.string.lb_planet -> _selectedPlanets.value.contains(id)
        R.string.lb_continent -> _selectedContinents.value.contains(id)
        R.string.lb_country -> _selectedCountries.value.contains(id)
        R.string.lb_region -> _selectedRegions.value.contains(id)
        R.string.lb_division -> _selectedDivisions.value.contains(id)
        R.string.lb_subdivision -> _selectedSubdivisions.value.contains(id)
        else -> _selectedTowns.value.contains(id)
    }


    /**
     * We propagate the search results to the caller
     * The result is a pair with the first being the search field (e.g town name, town regions etc) and
     * the second is the list which was selected from the search
     */
    fun saveSelectionBeforeNav() {
        viewModelScope.launch {
            val selections = mapOf(
                R.string.lb_planet to _selectedPlanets.value.keys.fold("") { acc, i -> "$acc $i" },
                R.string.lb_continent to _selectedContinents.value.keys.fold("") { acc, i -> "$acc $i" },
                R.string.lb_country to _selectedCountries.value.keys.fold("") { acc, i -> "$acc $i" },
                R.string.lb_region to _selectedRegions.value.keys.fold("") { acc, i -> "$acc $i" },
                R.string.lb_division to _selectedDivisions.value.keys.fold("") { acc, i -> "$acc $i" },
                R.string.lb_subdivision to _selectedSubdivisions.value.keys.fold("") { acc, i -> "$acc $i" },
                R.string.lb_town to _selectedTowns.value.keys.fold("") { acc, i -> "$acc $i" },
            ).filter { it.key in _queryFields.value }.map {
                UnivSelection(
                    place = it.key,
                    selections = it.value,
                    callerRoute = savedStateHandle[UniverseArgs.UNIV_SEARCH_CALLER_ROUTE] ?: ""
                )
            }
            cachesRepo.saveUnivSelections(selections)
        }
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }
}