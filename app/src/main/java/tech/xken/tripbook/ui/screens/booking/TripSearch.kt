@file:OptIn(ExperimentalComposeUiApi::class)

package tech.xken.tripbook.ui.screens.booking


import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.components.RoadItem

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TripSearch(
    vm: TripSearchVM = hiltViewModel(LocalViewModelStoreOwner.current as ViewModelStoreOwner),
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    onMenuClick: () -> Unit,
    onComplete: () -> Unit
) {
    val uis by vm.uiState.collectAsState()
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    val focusManager: FocusManager = LocalFocusManager.current
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val dialog = remember {
        DatePickerDialog(
            /* context = */ context,
            /* listener = */ { _, y, m, d ->
                vm.onDepartureDateChange(LocalDate(y, m, d))
            },
            /* year = */ uis.departureDate.year,
            /* month = */ uis.departureDate.monthNumber,
            /* dayOfMonth = */ uis.departureDate.dayOfMonth
        )
    }
    val scope = rememberCoroutineScope()
    if (uis.townNames.isEmpty()) vm.initTownNames(stringArrayResource(id = R.array.town_names))

    if (uis.isComplete) {
        onComplete()
        vm.onCompleteChange(false)
    }

    BottomSheetScaffold(
        sheetContent = {
            if (uis.isShowingRoad && uis.roadUis != null) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = { vm.clearResults() }, modifier = Modifier
                                .padding(2.dp)
                                .weight(0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "",
                            )
                        }
                        Text(
                            text = stringResource(R.string.lb_trip_path).caps,
                            style = MaterialTheme.typography.h6.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(0.2f, true),
                        )

                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = stringResource(id = R.string.desc_choose_agency),
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }

                    Divider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(10.dp))

                    uis.roadUis?.let { roadUis ->
                        RoadItem(
                            uis = roadUis,
                            onPathTownClick = {
                                vm.onRoadPathUisChange(
                                    roadUis.copy(showPath = !roadUis.showPath)
                                )
                            },
                            onClick = {
                                vm.onRoadPathUisChange(
                                    roadUis.copy(showPath = !roadUis.showPath)
                                )
                            },
                            onLongClick = {
                                vm.onRoadPathUisChange(
                                    roadUis.copy(showPath = !roadUis.showPath)
                                )
                            },
                            modifier = Modifier
                                .padding(fieldPadding)
                                .fillMaxWidth(),
                            elevation = 0.dp
                        )
                    }

                    Text(
                        text = stringResource(R.string.msg_trip_path), modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.caption
                    )

                }
            }
        },
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = { onMenuClick() }) {
                        Icon(
                            Icons.Outlined.Menu,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.lb_trip_search).caps,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        modifier = Modifier.fillMaxSize(),
        sheetPeekHeight = if (uis.isShowingRoad) BottomSheetScaffoldDefaults.SheetPeekHeight else 0.dp,
        sheetElevation = 4.dp, sheetShape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(10),
            topEnd = CornerSize(10),
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0)
        ), drawerScrimColor = DrawerDefaults.scrimColor,
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
                .disableComposable(uis.isLoading || uis.isShowingRoad)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = uis.isLoading,
                modifier = Modifier.padding(4.dp)
            ) {
                CircularProgressIndicator()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(fieldPadding),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(id = R.string.app_name).caps,
                    style = MaterialTheme.typography.h2.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                    ),
                    modifier = Modifier.padding(4.dp)
                )
                Icon(imageVector = Icons.Outlined.Luggage, contentDescription = null)
            }

            Divider(
                modifier = Modifier
                    .padding(fieldPadding)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            )

            AnimatedVisibility(
                visible = uis.from.isNotBlank() && uis.to.isNotBlank(),
                modifier = Modifier.padding(0.dp)
            ) {
                IconButton(onClick = { vm.swapTrip() }) {
                    Icon(
                        imageVector = Icons.Outlined.SwapHoriz,
                        contentDescription = stringResource(id = R.string.desc_trip_swap),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
            ) {
                ExposedDropdownMenuBox(
                    expanded = uis.isFromExpanded && uis.fromFilteredNames.isNotEmpty(),
                    onExpandedChange = { vm.onFromExpandedChange(it) },
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .weight(0.5f)
                ) {
//                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    OutTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uis.from.caps,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowCircleUp,
                                contentDescription = stringResource(id = R.string.desc_my_location)
                            )
                        },
                        label = { Text(stringResource(R.string.lb_from).caps) },
                        onValueChange = {
                            vm.onFromChange(it)
                            vm.onFromExpandedChange(true)
                        },
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Next
                            )
                        }),
                        singleLine = true,
                        errorText = {
                            vm.isFromError(
                                it
                                    .trim()
                                    .lowercase()
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
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


                ExposedDropdownMenuBox(
                    expanded = uis.isToExpanded && uis.toFilteredNames.isNotEmpty(),
                    onExpandedChange = { vm.onToExpandedChange(it) },
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(0.5f)
                ) {
                    OutTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uis.to.caps,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowCircleDown,
                                contentDescription = stringResource(id = R.string.desc_my_destination)
                            )
                        },
                        label = { Text(stringResource(R.string.lb_to).caps) },
                        onValueChange = {
                            vm.onToChange(it)
                            vm.onToExpandedChange(true)
                        },
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Next
                            )
                        }),
                        singleLine = true,
                        errorText = {
                            vm.isToError(
                                it
                                    .trim()
                                    .lowercase()
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
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

            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                value = uis.formattedDepartureDate,
                trailingIcon = {
                    IconButton(onClick = { dialog.show() }) {
                        Icon(
                            imageVector = Icons.Outlined.EditCalendar,
                            contentDescription = stringResource(R.string.msg_departure_date),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "")
                },
                label = { Text(stringResource(R.string.lb_on).caps) },
                onValueChange = { },
                readOnly = true,
                singleLine = true,
                errorText = { vm.isDepartureDateError() },
                keyboardActions = KeyboardActions.Default,
            )

            Button(
                onClick = {
                    vm.search(doOnStart = {
                        keyboard?.hide()
                        focusManager.clearFocus(true)
                    })
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(fieldPadding)
                    .fillMaxWidth(),
            ) {

                Text(
                    stringResource(R.string.lb_go).titleCase,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null
                )

            }

        }

        if (uis.message != null) {
            val message = stringResource(id = uis.message!!).caps
            LaunchedEffect(scaffoldState, vm, uis.message) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message
                ).also {
                    vm.onMessageChange(null)
                }
            }
        }
    }
}

