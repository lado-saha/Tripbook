package tech.xken.tripbook.ui.screens.universe_editor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.ui.components.SearchBar
import java.util.*

@Composable
fun UniverseSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: TownSearchVM = hiltViewModel(),
    onNavigateBack: (Pair<Int, List<String>>?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.message.isNotBlank()) Log.d("Towns", uiState.message)
    // We run only once since it's expensive
    if (viewModel.allTownNames.isEmpty()) viewModel.getAutoCompleteOptions()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(brush = Brush.linearGradient(listOf(Color.Green, Color.Red, Color.Yellow)), alpha = .2f)
        .padding(4.dp)
    ) {
        SearchBar(searchText = uiState.searchStr,
            onSearchTextChange = { viewModel.searchStrChange(it) },
            fieldsStrRes = uiState.searchFields,
            onFieldSelected = { viewModel.searchFieldChange(it) },
            //We leave without any result propagation
            onBackClick = { onNavigateBack(null) },
            placeHolderResId = uiState.searchPlaceHolderStrRes,
            onClearTextClick = { viewModel.searchStrChange() },
            isError = { uiState.isError })
        Card(
            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth(),
            shape = if (uiState.isError) RoundedCornerShape(100f) else RoundedCornerShape(40f),
            elevation = if (uiState.searchStr.isBlank()) 1.5.dp else 5.dp,
        ) {
            LazyColumn(contentPadding = PaddingValues(2.dp)) {
                if (uiState.searchStr.isBlank()) item {
                    TextButton(onClick = { viewModel.searchStrChange(TownSearchVM.TOWN_SEARCH_LIST_ALL_CHAR) },
                        modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null)
                        Text(text = stringResource(R.string.lb_see_all_options).caps,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 4.dp))
                    }
                }
                else if (uiState.isError) item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center) {
                        Text(text = stringResource(R.string.lb_no_results).caps)
                    }
                }
                else {
                    items(items = uiState.searchOptions, key = { item -> item }) { option ->
                        Text(text = option.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }, modifier = Modifier
                            .clickable {
                                viewModel.searchStrChange(option)
                            }
                            .padding(8.dp)
                            .fillMaxWidth())
                    }
                    item {
                        Divider(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp, top = 2.dp))
                        TextButton(onClick = {
                            //We leave while any result propagation
//                            viewModel.propagateResultsBeforeNavigateAway()
                            onNavigateBack(uiState.searchResults)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                            Text(text = stringResource(id = uiState.addTownStrRes).caps,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
                item {
                    Text(text = stringResource(R.string.lb_src_cmr),
                        style = MaterialTheme.typography.caption.copy(textAlign = TextAlign.End,
                            fontStyle = FontStyle.Italic),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .fillMaxWidth())
                }
            }
        }

    }

}
