package tech.xken.tripbook.ui.screens.booking


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Gender
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.ui.components.OutTextField

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripSearch(
    vm: TripSearchVM = hiltViewModel(),
    scaffoldState: BackdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed),
) {
    val uis by vm.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)

    BackdropScaffold(
        scaffoldState = scaffoldState,
        frontLayerContent = {

        },
        backLayerContent = {
            /**
             * Trip Search view
             */
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Origin
                ExposedDropdownMenuBox(
                    expanded = uis.isFromExpanded,
                    onExpandedChange = { vm.onFromExpandedChange(it) },
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth()
                ) {
                    OutTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uis.from,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = uis.isFromExpanded
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.TripOrigin, contentDescription = "")
                        },
                        label = { Text(stringResource(R.string.lb_from).caps) },
                        onValueChange = { vm.onFromChange(it) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        keyboardActions = KeyboardActions.Default,
                        singleLine = true,
                    )
                    if (uis.fromFilteredNames.isNotEmpty())
                        ExposedDropdownMenu(
                            expanded = uis.isFromExpanded,
                            onDismissRequest = { vm.onFromExpandedChange(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uis.fromFilteredNames.forEach {
                                DropdownMenuItem(onClick = {
                                    vm.onFromChange(it)
                                    vm.onFromExpandedChange(false)
                                }) { Text(text = it) }
                            }
                        }
                }
                //Destination
                ExposedDropdownMenuBox(
                    expanded = uis.isToExpanded,
                    onExpandedChange = { vm.onToExpandedChange(it) },
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth()
                ) {
                    OutTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uis.to,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = uis.isFromExpanded
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.TripOrigin, contentDescription = "")
                        },
                        label = { Text(stringResource(R.string.lb_to).caps) },
                        onValueChange = { vm.onToChange(it) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        keyboardActions = KeyboardActions.Default,
                        singleLine = true,
                    )
                    if (uis.toFilteredNames.isNotEmpty())
                        ExposedDropdownMenu(
                            expanded = uis.isFromExpanded,
                            onDismissRequest = { vm.onToExpandedChange(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uis.toFilteredNames.forEach {
                                DropdownMenuItem(onClick = {
                                    vm.onToChange(it)
                                    vm.onToExpandedChange(false)
                                }) { Text(text = it) }
                            }
                        }
                }
            }
        },
        appBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.app_name), style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold, fontSize = 24.sp
                        )
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = null
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {

    }
}