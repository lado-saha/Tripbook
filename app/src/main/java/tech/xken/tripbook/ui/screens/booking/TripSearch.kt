package tech.xken.tripbook.ui.screens.booking


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Gender
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.OutTextField

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripSearch(
    vm: TripSearchVM = hiltViewModel(),
    scaffoldState: BackdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
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
            TripSearchLayer(uis, vm, fieldPadding, focusManager)
        },
        appBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uis.tripStr,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold, fontSize = 24.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        backLayerBackgroundColor = MaterialTheme.colors.background,
        persistentAppBar = false,
        peekHeight = 47.dp,
    ) {

    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun TripSearchLayer(
    uis: TripSearchUiState,
    vm: TripSearchVM,
    fieldPadding: PaddingValues,
    focusManager: FocusManager,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(id = R.string.app_name).caps,
                style = MaterialTheme.typography.h2.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                ),
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 64.dp, start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
            )
            //Origin
            ExposedDropdownMenuBox(
                expanded = uis.isFromExpanded && uis.fromFilteredNames.isNotEmpty(),
                onExpandedChange = { vm.onFromExpandedChange(it) },
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth()
            ) {
                OutTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uis.from,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = ""
                        )
                    },
                    label = { Text(stringResource(R.string.lb_from).caps) },
                    onValueChange = {
                        vm.filterFromNames(it)
                        vm.onFromChange(it)
                        vm.onFromExpandedChange(true)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    singleLine = true,
                    errorText = { vm.isFromError(it.trim().lowercase()) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                ExposedDropdownMenu(
                    expanded = uis.isFromExpanded && uis.fromFilteredNames.isNotEmpty(),
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
                expanded = uis.isToExpanded && uis.toFilteredNames.isNotEmpty(),
                onExpandedChange = { vm.onToExpandedChange(it) },
                modifier = Modifier
                    .padding(fieldPadding)
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            ) {
                OutTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uis.to,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = ""
                        )
                    },
                    label = { Text(stringResource(R.string.lb_to).caps) },
                    onValueChange = {
                        vm.filterToNames(it)
                        vm.onToChange(it)
                        vm.onToExpandedChange(true)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions(onGo = {
                        // Start Search
                    }),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    errorText = { vm.isToError(it.trim().lowercase()) }
                )
                ExposedDropdownMenu(
                    expanded = uis.isToExpanded && uis.toFilteredNames.isNotEmpty(),
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
        FloatingActionButton(
            onClick = {},
            shape = RoundedCornerShape(bottomEnd = 30f),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.Outlined.Menu, null)
        }

        FloatingActionButton(
            onClick = {},
            shape = RoundedCornerShape(bottomStart = 30f),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(imageVector = Icons.Outlined.ArrowForward, null)
        }
    }
}