package tech.xken.tripbook.ui.screens.booking

import android.app.DatePickerDialog
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.LocalDate
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.booker.Sex
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.format
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.components.PhotoPicker
import tech.xken.tripbook.ui.screens.booking.BookerAccountDialogStatus.*
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookerAccount(
    vm: BookerAccountVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onComplete: () -> Unit,
    navigateUp: () -> Unit
) {
    val statusMap = remember {
        mapOf(
            HELP_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.Badge,
                title = "Tell more us about you",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            HELP_ON_JOB_SEEKING to InfoDialogUiState(
                mainIcon = Icons.Outlined.Business,
                title = "Do you want to be employed?",
                text = buildAnnotatedString {
                    append("Text here")//TODO: Add profile page is job seeker help text
                },
                positiveText = "I understand"
            ),
            LEAVING_WITHOUT_SAVING to InfoDialogUiState(
                mainIcon = Icons.Outlined.Save,
                title = "Some changes may be lost",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Save changes",
                otherText = "Discard",
                isNegative = true
            ),
            WELCOME to InfoDialogUiState(
                mainIcon = Icons.Outlined.Person,
                title = "Welcome",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            COULD_NOT_GET_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Filled.PersonOff,
                title = "Could not load account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true
            ),
            COULD_NOT_GET_PHOTO to InfoDialogUiState(
                mainIcon = Icons.Filled.PersonOff,
                title = "Could not load photo",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true
            ),
            LEAVING_WITH_EMPTY_PROFILE to InfoDialogUiState(
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

    val focusManager = LocalFocusManager.current
    //Initialisation Processes
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current
    vm.loadPhotoBitmap(context)
    //We navigate away
    if (uis.isComplete) {
        onComplete()
        vm.onCompleteChange(false)
    }
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
    //To pick an image from the gallery
    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val persistentUri = it
                context.contentResolver.takePersistableUriPermission(
                    persistentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                vm.onPhotoUriChange(context, persistentUri)
            }
        }
    val scrollState = rememberScrollState()
    val dialog = remember {
        DatePickerDialog(
            /* context = */ context,
            /* listener = */ { _, y, m, d ->
                vm.onBirthdayChange(LocalDate(y, m, d))
            },
            /* year = */ uis.booker.birthday.year,
            /* month = */ uis.booker.birthday.monthNumber,
            /* dayOfMonth = */ uis.booker.birthday.dayOfMonth
        )
    }


    fun handleBackNav() = if (vm.hasAnyFieldChanged || vm.hasPhotoChanged) vm.onDialogStatusChange(
        LEAVING_WITHOUT_SAVING
    ) else if (!uis.isEditMode) vm.onDialogStatusChange(LEAVING_WITH_EMPTY_PROFILE) else vm.onCompleteChange(
        true
    )

    BackHandler(true) {
        handleBackNav()
    }

    when (val status = uis.dialogStatus) {
        NONE -> {}
        HELP_MAIN_PAGE -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                }
            )
        }

        HELP_ON_JOB_SEEKING -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                }
            )
        }

        LEAVING_WITHOUT_SAVING -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.saveOrUpdateAccount()

                }, onOtherClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.onCompleteChange(true)
                }
            )
        }

        WELCOME -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                }
            )
        }

        COULD_NOT_GET_ACCOUNT -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                    navigateUp()
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.onAccountInitComplete(false)
                },
                onOtherClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.onCompleteChange(true)
                    navigateUp()
                }
            )
        }

        LEAVING_WITH_EMPTY_PROFILE -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                }, onOtherClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.onCompleteChange(true)
                }
            )
        }

        COULD_NOT_GET_PHOTO -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.onPhotoInitComplete(false)
                },
                onOtherClick = {
                    vm.onDialogStatusChange(NONE)
                }
            )
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.lb_me).titleCase,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        handleBackNav()
                    }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(onClick = { vm.onDialogStatusChange(HELP_MAIN_PAGE) }) {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            contentDescription = stringResource(id = R.string.desc_help),
                            tint = LocalContentColor.current
                        )
                    }
                }
            )
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
                visible = uis.isLoading,
                modifier = Modifier.padding(4.dp)
            ) {
                CircularProgressIndicator()
                if (uis.isLoading && uis.isInitComplete)
                    LaunchedEffect(Unit) {
                        scrollState.animateScrollTo(0)
                    }

            }

            if (uis.isInitComplete) {
                PhotoPicker(
                    photoBitmap = uis.photoBitmap,
                    placeholder = Icons.Filled.AccountCircle,
                    onDeletePhotoClick = { vm.onPhotoUriChange(context, null) },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(150.dp),
                    onBrowseGalleryClick = {
                        galleryLauncher.launch(arrayOf("image/*"))
                    },
                    onLaunchCameraClick = {
                        //TODO: Launch camera
                    },
                    isLoading = uis.isLoadingPhoto,
                    onReloadPhotoClick = {
                        vm.onPhotoInitComplete(false)
                    },
                    isFailure = uis.isPhotoFailed
                )

                //Name
                OutTextField(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    value = uis.booker.name ?: "",
                    errorText = { vm.nameErrorText(it) },
                    onValueChange = { vm.onNameChange(it) },
                    trailingIcon = {
                        if (uis.booker.name.isNotBlank())
                            IconButton(onClick = { vm.onNameChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = ""
                                )
                            }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = ""
                        )
                    },
                    label = { Text(stringResource(R.string.lb_name).caps) },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                //nation id card number
                OutTextField(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    value = uis.booker.idCardNumber ?: "",
                    errorText = { vm.idCardNumberErrorText(it) },
                    onValueChange = { vm.onIdCardNumberChange(it) },
                    trailingIcon = {
                        if (uis.booker.idCardNumber.isNotBlank())
                            IconButton(onClick = { vm.onIdCardNumberChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = ""
                                )
                            }
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Badge, contentDescription = "")
                    },
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
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
                        IconButton(onClick = { dialog.show() }) {
                            Icon(imageVector = Icons.Outlined.EditCalendar, contentDescription = "")
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
                            Icon(imageVector = Icons.Default.Transgender, contentDescription = "")
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
                        if (!uis.booker.nationality.isNullOrBlank())
                            IconButton(onClick = { vm.onNationalityChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = ""
                                )
                            }
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = "")
                    },
                    label = { Text(stringResource(R.string.lb_nationality).caps) },
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
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
                        if (!uis.booker.occupation.isNullOrBlank())
                            IconButton(onClick = { vm.onOccupationChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = ""
                                )
                            }

                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Work, contentDescription = "")
                    },
                    label = { Text(stringResource(R.string.lb_occupation).caps) },
                    keyboardActions = KeyboardActions(
                        onGo = { vm.saveOrUpdateAccount() }
                    ),
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


// Check for user messages to display on the screen
    if (uis.message != null) {
        val message = stringResource(id = uis.message!!).caps

        LaunchedEffect(scaffoldState, vm, uis.message) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message, /*actionLabel = retryActionLabel*/
            ).also {
                if (it == SnackbarResult.ActionPerformed)
                    when (uis.message) {

                    }
            }
            vm.onMessageChange(null)
        }
    }
}


