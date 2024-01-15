package tech.xken.tripbook.ui.screens.booking

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.ActionSheet
import tech.xken.tripbook.data.models.ImageUiState
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.data.models.booker.Sex
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.format
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.FilePicker
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.navigation.BookingNavArgs
import tech.xken.tripbook.ui.screens.booking.BookerAccountDialogState.*
import tech.xken.tripbook.ui.screens.booking.BookerAccountDialogState.NONE
import tech.xken.tripbook.ui.screens.booking.BookerAccountSheetState.*
import java.util.*


/**
 *Responsible for collecting the uri of the new image selected from th image picker
 */
fun collectImageUisStateFromNav(
    vm: BookerAccountVM,
    navController: NavController
) {
    navController.currentBackStackEntry!!.savedStateHandle.get<String?>(BookingNavArgs.BOOKER_IMAGE_UI_STATE)
        ?.let {
            val new = vm.parser.decodeFromString<ImageUiState>(it)
            vm.onPhotoUriChange(new.localUri)
        }
}

/**
 * The main page
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookerAccount(
    vm: BookerAccountVM = hiltViewModel(),
    navigateToImageViewer: (String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: () -> NavController,
    onComplete: () -> Unit,
    navigateUp: () -> Unit
) {
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current

    // Manages the currently highlighted textField
    val focusManager = LocalFocusManager.current

    collectImageUisStateFromNav(vm, navController())

    //We navigate away when uis.isComplete is true
    if (uis.isComplete) {
        onComplete()
        vm.onCompleteChange(false)
    }
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
    val scrollState = rememberScrollState()
    val datePicker = remember {
        DatePickerDialog(/* context = */ context,/* listener = */
            { _, y, m, d ->
                vm.onBirthdayChange(LocalDate(y, m, d))
            },/* year = */
            uis.booker.birthday.year,/* month = */
            uis.booker.birthday.monthNumber,/* dayOfMonth = */
            uis.booker.birthday.dayOfMonth
        )
    }

    // Hides or shows the bottom sheet
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    vm.onSheetStatusChange(BookerAccountSheetState.NONE)
                }

                ModalBottomSheetValue.Expanded -> {}
                ModalBottomSheetValue.HalfExpanded -> {}
            }
            true
        }
    )
// Hides or shows the bottom sheet
    LaunchedEffect(uis.sheetStatus) {
        if (uis.sheetStatus != BookerAccountSheetState.NONE) {
            sheetState.show()
        } else if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.currentValue != ModalBottomSheetValue.Hidden) {
            sheetState.hide()
        }
    }

    // On back button or navigate Up, we do some logic checking and show appropriate dialog.
    // Like to warn if the booker has not yet created an account
    fun handleBackNav() = if (vm.hasAnyFieldChanged) vm.onDialogStateChange(
        LEAVING_WITHOUT_SAVING
    ) else if (!uis.isEditMode) vm.onDialogStateChange(LEAVING_WITH_EMPTY_ACCOUNT) else vm.onCompleteChange(
        true
    )

    BackHandler(true) {
        vm.onHideErrorsChange(false)
        handleBackNav()
    }


    // Manages showing the Info dialogs
    InfoDialogs(uis, vm) { navigateUp() }

    ModalBottomSheetLayout(
        sheetContent = {
            BottomSheetContent(uis, vm)
        },
        sheetShape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(10), topStart = CornerSize(10),
        ),
        sheetState = sheetState,
        sheetElevation = 1.dp,
        scrimColor = ModalBottomSheetDefaults.scrimColor.copy(alpha = 0.1f)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(elevation = 0.dp, title = {
                    Text(
                        text = stringResource(id = R.string.lb_me).titleCase,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        handleBackNav()
                    }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                }, modifier = Modifier.fillMaxWidth(), actions = {
                    IconButton(onClick = {
                        vm.onSheetStatusChange(ACTIONS)
                    }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = stringResource(id = R.string.desc_more_options),
                            tint = LocalContentColor.current

                        )
                    }
                })
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .disableComposable(uis.isLoading || !uis.isInitComplete)
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = uis.isLoading, modifier = Modifier.padding(4.dp)
                ) {
                    CircularProgressIndicator()
                    if (uis.isLoading && uis.isInitComplete) LaunchedEffect(Unit) {
                        scrollState.animateScrollTo(0)
                    }
                }

                // Show the screen only when the request to get the Booker info were successful
                if (uis.isInitComplete) {
                    // In this case use to choose profile photo
                    FilePicker(
                        uis = uis.photoUiState,
                        onUiStateChange = {
                            vm.onPhotoUiStateChange(it as ImageUiState)
                        },
                        onOpenClick = {
//                            vm.setStateForNavigation()
                            navigateToImageViewer(vm.encodedImageUis)
                        },
                        onEditClick = {
//                            vm.setStateForNavigation()
                            navigateToImageViewer(vm.encodedImageUis)
                        },
                        onInfoClick = {
                            vm.onDialogStateChange(ABOUT_ACCOUNT_PHOTO)
                        },
                        onHideClick = {
                            vm.onPhotoUiStateChange(uis.photoUiState.copy(isContentHidden = !uis.photoUiState.isContentHidden))
                        },
                        onDeleteClick = {
                            vm.onPhotoUriChange(Uri.EMPTY)
                        },
                        onUndoClick = {
                            vm.onPhotoUriChange(null)
                        },
                        modifier = Modifier
                            .padding(fieldPadding),
                        border = BorderStroke(
                            1.dp,
                            if (uis.photoUiState.isFailure && !uis.hideErrors) MaterialTheme.colors.error else Color.Unspecified
                        ),
                    )

                    //Name
                    OutTextField(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        value = uis.booker.name,
                        errorText = { vm.nameErrorText(it) },
                        onValueChange = { vm.onNameChange(it) },
                        trailingIcon = {
                            if (uis.booker.name.isNotBlank()) IconButton(onClick = {
                                vm.onNameChange("")
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear, contentDescription = ""
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person, contentDescription = ""
                            )
                        },
                        label = { Text(stringResource(R.string.lb_name).caps) },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Next)
                        }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    //nation id card number
                    OutTextField(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        value = uis.booker.idCardNumber,
                        errorText = { vm.idCardNumberErrorText(it) },
                        onValueChange = { vm.onIdCardNumberChange(it) },
                        trailingIcon = {
                            if (uis.booker.idCardNumber.isNotBlank()) IconButton(onClick = {
                                vm.onIdCardNumberChange(
                                    ""
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear, contentDescription = ""
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Badge, contentDescription = "")
                        },
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Next)
                        }),
                        label = { Text(stringResource(R.string.lb_id_card_number).titleCase) },
                        singleLine = true
                    )

                    // Birthday
                    OutTextField(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        value = uis.booker.birthday.format(),
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(
                                    imageVector = Icons.Outlined.EditCalendar,
                                    contentDescription = ""
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Cake, contentDescription = "")
                        },
                        label = { Text(stringResource(R.string.lb_birthday).caps) },
                        onValueChange = { },
                        readOnly = true,
                        singleLine = true,
                        errorText = { vm.birthdayErrorText() },
                        keyboardActions = KeyboardActions.Default,
                    )

                    //Gender
                    ExposedDropdownMenuBox(
                        expanded = uis.isSexExpanded,
                        onExpandedChange = { vm.onGenderExpansionChange(it) },
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth()
                    ) {
                        OutTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = stringResource(id = uis.booker.bookerSex.resId).caps,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = uis.isSexExpanded
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Transgender, contentDescription = ""
                                )
                            },
                            label = { Text(stringResource(R.string.lb_sex).caps) },
                            onValueChange = {},
                            readOnly = true,
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            keyboardActions = KeyboardActions.Default,
                            singleLine = true,
                        )
                        ExposedDropdownMenu(
                            expanded = uis.isSexExpanded,
                            onDismissRequest = { vm.onGenderExpansionChange(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Sex.values().forEach { sex ->
                                DropdownMenuItem(onClick = {
                                    vm.onSelectedSexChange(sex)
                                    vm.onGenderExpansionChange(false)
                                }) {
                                    Text(text = stringResource(sex.resId).caps)
                                }
                            }
                        }
                    }

                    //Nationality
                    OutTextField(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        value = uis.booker.nationality ?: "",
                        onValueChange = { vm.onNationalityChange(it) },
                        trailingIcon = {
                            if (!uis.booker.nationality.isNullOrBlank()) IconButton(onClick = {
                                vm.onNationalityChange(
                                    ""
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear, contentDescription = ""
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = "")
                        },
                        label = { Text(stringResource(R.string.lb_nationality).caps) },
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Next)
                        }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true
                    )
                    //Occupation
                    OutTextField(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        value = uis.booker.occupation ?: "",
                        onValueChange = { vm.onOccupationChange(it) },
                        trailingIcon = {
                            if (!uis.booker.occupation.isNullOrBlank()) IconButton(onClick = {
                                vm.onOccupationChange(
                                    ""
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear, contentDescription = ""
                                )
                            }

                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Work, contentDescription = "")
                        },
                        label = { Text(stringResource(R.string.lb_occupation).caps) },
                        keyboardActions = KeyboardActions(onGo = { vm.saveOrUpdateAccount() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            vm.saveOrUpdateAccount()
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = if (uis.isEditMode) Icons.Outlined.Check else Icons.Outlined.PersonAdd,
                            contentDescription = null
                        )
                        Text(
                            stringResource(if (uis.isEditMode) R.string.lb_save else R.string.lb_create).titleCase,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                }

            }
        }
    }

// Check for user messages to display on the screen
    SnackbarManager(uis, scaffoldState, vm)
}


/**
 * The bottom sheet shows the actions from [BookerAccountSheetState]
 */
@Composable
private fun BottomSheetContent(
    uis: BookerAccountUiState,
    vm: BookerAccountVM,
) {
    when (uis.sheetStatus) {
        BookerAccountSheetState.NONE -> {}
        ACTIONS -> {
            ActionSheet(
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionItem(
                    action = MainAction(R.string.lb_about_page, Icons.Outlined.Info),
                    onClick = {
                        vm.onDialogStateChange(ABOUT_MAIN_PAGE)
                        vm.onSheetStatusChange(BookerAccountSheetState.NONE)
                    },
                )

            }
        }
    }
}

/**
 * Shows the snackbar messages
 */
@Composable
private fun SnackbarManager(
    uis: BookerAccountUiState,
    scaffoldState: ScaffoldState,
    vm: BookerAccountVM
) {
    if (uis.message != null) {
        val message = stringResource(id = uis.message).caps
        LaunchedEffect(scaffoldState, vm, uis.message) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message, /*actionLabel = retryActionLabel*/
            ).also {
                if (it == SnackbarResult.ActionPerformed) when (uis.message) {

                }
            }
            vm.onMessageChange(null)
        }
    }
}

/**
 * Shows dialogs from [BookerAccountDialogState]
 */
@Composable
private fun InfoDialogs(
    uis: BookerAccountUiState,
    vm: BookerAccountVM,
    navigateUp: () -> Unit,
) {
    val statusMap = remember {
        mapOf(
            ABOUT_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.Badge,
                title = "Tell more us about you",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            ABOUT_ACCOUNT_PHOTO to InfoDialogUiState(
                mainIcon = Icons.Outlined.PersonPin,
                title = "Account Photo",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ), LEAVING_WITHOUT_SAVING to InfoDialogUiState(
                mainIcon = Icons.Outlined.Save,
                title = "Some changes may be lost",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Save changes",
                otherText = "Discard",
                isNegative = true
            ), WELCOME_NEW_BOOKER to InfoDialogUiState(
                mainIcon = Icons.Outlined.Person,
                title = "Welcome",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ), COULD_NOT_GET_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Filled.PersonOff,
                title = "Could not load account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true
            ), COULD_NOT_GET_PHOTO to InfoDialogUiState(
                mainIcon = Icons.Filled.PersonOff,
                title = "Could not load photo",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true
            ), LEAVING_WITH_EMPTY_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Filled.ErrorOutline,
                title = "Leaving with empty account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Stay",
                otherText = "Leave",
                isNegative = true
            )
        )
    }
    when (val status = uis.dialogStatus) {
        NONE -> {}
        ABOUT_MAIN_PAGE -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
            })
        }

        ABOUT_ACCOUNT_PHOTO -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
            })
        }

        LEAVING_WITHOUT_SAVING -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
                vm.saveOrUpdateAccount()

            }, onOtherClick = {
                vm.onDialogStateChange(NONE)
                vm.onCompleteChange(true)
            })
        }

        WELCOME_NEW_BOOKER -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
            })
        }

        COULD_NOT_GET_ACCOUNT -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
                navigateUp()
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
                vm.onInitCompleted(false)
            }, onOtherClick = {
                vm.onDialogStateChange(NONE)
                vm.onCompleteChange(true)
                navigateUp()
            })
        }

        LEAVING_WITH_EMPTY_ACCOUNT -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
            }, onOtherClick = {
                vm.onDialogStateChange(NONE)
                vm.onCompleteChange(true)
            })
        }

        COULD_NOT_GET_PHOTO -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStateChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(NONE)
            }, onOtherClick = {
                vm.onDialogStateChange(NONE)
            })
        }

    }
}

