package tech.xken.tripbook.ui.screens.booking

import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Gender
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.components.PhotoPicker
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookerProfile(
    vm: BookerProfileVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateBack: () -> Unit,
    onProfileComplete: (() -> Unit),
) {
    val focusManager = LocalFocusManager.current
    //Initialisation Processes
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current
    vm.loadPhotoBitmap(context)
    //We navigate away
    if (uis.isComplete) {
        onProfileComplete()
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

    val dialog = remember {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                vm.onBirthdayChange(Calendar.getInstance().apply { set(y, m, d) }.timeInMillis)
            },
            uis.calendar[Calendar.YEAR],
            uis.calendar[Calendar.MONTH],
            uis.calendar[Calendar.DAY_OF_MONTH]
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id =  R.string.lb_my_profile
                        ).titleCase,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                //We disable content when we are loading or we are still determining the mode
                .disableComposable(uis.isLoading || !uis.isInitComplete)
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = uis.isLoading,
                modifier = Modifier.padding(4.dp)
            ) {
                CircularProgressIndicator()
            }

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
                }
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
                    if (!uis.booker.name.isNullOrBlank())
                        IconButton(onClick = { vm.onNameChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = ""
                            )
                        }
                },
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "") },
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
                    if (!uis.booker.idCardNumber.isNullOrBlank())
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
                value = vm.formattedBirthday(),
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
                errorText = { vm.birthdayErrorText() }, keyboardActions = KeyboardActions.Default,
            )

            //Gender
            ExposedDropdownMenuBox(
                expanded = uis.isGenderExpanded,
                onExpandedChange = { vm.onGenderExpansionChange(it) },
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth()
            ) {
                OutTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = stringResource(id = uis.selectedGender).caps,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = uis.isGenderExpanded
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Transgender, contentDescription = "")
                    },
                    label = { Text(stringResource(R.string.lb_gender).caps) },
                    onValueChange = {},
                    readOnly = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions.Default,
                    singleLine = true,
                )
                ExposedDropdownMenu(
                    expanded = uis.isGenderExpanded,
                    onDismissRequest = { vm.onGenderExpansionChange(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Gender.gendersStrRes.forEach { gender ->
                        DropdownMenuItem(onClick = {
                            vm.onSelectedGenderChange(gender)
                            vm.onGenderExpansionChange(false)
                        }) {
                            Text(text = stringResource(gender).caps)
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
                    onGo = { vm.save() }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                singleLine = true
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), thickness = 1.dp
            )

            //Is a job seeker
            Card(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.lb_job_seeker).caps,
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(0.8f)
                        )
                        Switch(
                            checked = uis.booker.isJobSeeker == true,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .weight(0.2f),
                            onCheckedChange = { vm.onJobSeekerChange(it) })
                    }
                    Text(
                        text = stringResource(if (uis.booker.isJobSeeker == true) R.string.msg_is_job_seeker else R.string.msg_is_not_job_seeker),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = {
                    vm.save()
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Check for user messages to display on the screen
    if (uis.message != null) {
        val message = stringResource(id = uis.message!!).caps
        val retryActionLabel = stringResource(R.string.lb_retry).caps

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