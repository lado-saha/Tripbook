package tech.xken.tripbook.ui.screens.universe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.SearchBar
import tech.xken.tripbook.ui.components.SearchResultItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UniverseSearch(
    modifier: Modifier = Modifier,
    vm: UniverseSearchVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onNavigateBack: (Boolean) -> Unit,
) {
    val uis by vm.uiState.collectAsState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AnimatedVisibility(
                visible = uis.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { LinearProgressIndicator() }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(uis.isToolboxVisible) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ExtendedFloatingActionButton(
                            text = { Text(stringResource(R.string.lb_save_selection).titleCase) },
                            onClick = { vm.saveSelectionBeforeNav{ onNavigateBack(true) } },
                            icon = { Icon(imageVector = Icons.Outlined.SaveAlt, null) },
                            shape = RoundedCornerShape(30f),
                            modifier = Modifier.padding(2.dp)
                        )

                        ExtendedFloatingActionButton(
                            text = { Text(stringResource(R.string.lb_deselect_all).titleCase) },
                            onClick = { vm.deselectAll() },
                            icon = { Icon(imageVector = Icons.Outlined.Deselect, null) },
                            shape = RoundedCornerShape(30f), modifier = Modifier.padding(2.dp)
                        )

                        ExtendedFloatingActionButton(
                            text = { Text(stringResource(R.string.lb_select_all).titleCase) },
                            onClick = { vm.selectAll() },
                            icon = { Icon(imageVector = Icons.Outlined.SelectAll, null) },
                            shape = RoundedCornerShape(30f), modifier = Modifier.padding(2.dp)
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { vm.onToolboxVisibilityChange(!uis.isToolboxVisible) },
                    modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(30f)
                ) {
                    Crossfade(targetState = uis.isToolboxVisible) {
                        when (it) {
                            false -> Icon(imageVector = Icons.Outlined.Menu, null)
                            else -> Icon(imageVector = Icons.Outlined.Close, null)
                        }
                    }
                }

            }

        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .animateContentSize()
                .fillMaxSize()
        ) {
            SearchBar(
                query = uis.query,
                onQueryChange = { vm.onQueryChange(it) },
                queryFields = uis.queryFields,
                onFieldClick = { vm.onFieldChange(it) },
                //We leave without any result propagation
                onBackClick = { onNavigateBack(false) },
                queryPlaceholder = R.string.lb_search,
                onClearQueryClick = { vm.onQueryChange("") },
                isError = { uis.isError },
                isFiltersVisible = uis.isFiltersVisible,
                filters = uis.filters,
                onFilterClick = { vm.onFilterClick(it) },
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
            Card(
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(40f)
            ) {
                AnimatedVisibility(visible = uis.isNormalMode) {
                    LazyColumn(
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        items(
                            items = vm.searchResults().map {
                                Pair(it.key, it.value.name!!)
                            }) { place ->
                            SearchResultItem(
                                id = place.first,
                                name = place.second.titleCase,
                                onCheckedChange = {
                                    vm.onResultsSelected(
                                        listOf(place.first)
                                    )
                                },
                                onClick = { vm.onQueryChange(place.second) },
                                onLongClick = { vm.onResultsSelected(listOf(place.first)) },
                                isSelected = vm.isSelected(place.first),
                                isParentSelected = vm.isParentSelected(place.first)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uis.isSelectionMode || uis.isParentSelectionMode) {
                    LazyColumn(
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        items(
                            items = vm.searchResults().filter {
                                when {
                                    uis.isSelectionMode && uis.isParentSelectionMode -> vm.isSelected(
                                        it.key
                                    ) && vm.isParentSelected(it.key)
                                    uis.isSelectionMode && !uis.isParentSelectionMode -> vm.isSelected(
                                        it.key
                                    )
                                    !uis.isSelectionMode && uis.isParentSelectionMode -> vm.isParentSelected(
                                        it.key
                                    )
                                    else -> false
                                }
                            }.map { Pair(it.key, it.value.name!!) }
                        ) { place ->
                            SearchResultItem(
                                id = place.first,
                                name = place.second.titleCase,
                                onCheckedChange = {
                                    vm.onResultsSelected(
                                        listOf(place.first),
                                    )
                                },
                                onClick = { vm.onQueryChange(place.second) },
                                onLongClick = { vm.onResultsSelected(listOf(place.first)) },
                                isSelected = vm.isSelected(place.first),
                                isParentSelected = vm.isParentSelected(place.first)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uis.isError) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(R.string.lb_no_results).caps)
                    }
                }
            }
        }
    }
}