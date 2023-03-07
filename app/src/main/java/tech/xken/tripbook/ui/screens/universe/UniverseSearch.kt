@file:OptIn(ExperimentalFoundationApi::class)

package tech.xken.tripbook.ui.screens.universe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.SearchBar
import tech.xken.tripbook.ui.components.SearchResultItem

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
            FloatingActionButton(
                onClick = {
                    vm.saveSelectionBeforeNav()
                    onNavigateBack(true)
                }, shape = RoundedCornerShape(30f)
            ) {
                Icon(imageVector = Icons.Outlined.ArrowForward, null)
            }
        }
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
                                    vm.onResultSelection(
                                        place.first,
                                    )
                                },
                                onClick = { vm.onQueryChange(place.second) },
                                onLongClick = { vm.onResultSelection(place.first) },
                                isSelected = vm.isSelected(place.first),
                                isParentSelected = vm.isParentSelected(place.first)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uis.isSelectionMode) {
                    LazyColumn(
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        items(
                            items = vm.searchResults().filter { vm.isSelected(it.key) }
                                .map { Pair(it.key, it.value.name!!) }
                        ) { place ->
                            SearchResultItem(
                                id = place.first,
                                name = place.second.titleCase,
                                onCheckedChange = {
                                    vm.onResultSelection(
                                        place.first,
                                    )
                                },
                                onClick = { vm.onQueryChange(place.second) },
                                onLongClick = { vm.onResultSelection(place.first) },
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