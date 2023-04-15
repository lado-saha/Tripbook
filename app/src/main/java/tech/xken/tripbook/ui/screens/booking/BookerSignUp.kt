package tech.xken.tripbook.ui.screens.booking

import android.app.DatePickerDialog
import android.content.Intent
import android.telephony.PhoneNumberUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.AnnotatedString
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
fun BookerSignUpOrDetails(
    vm: BookerSignUpVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateBack: () -> Unit,
    navigateToSignIn: (() -> Unit)? = null,
    onSignUpComplete: (() -> Unit)? = null,
    onBookerDetailsEditComplete: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    //Initialisation Processes
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current
    vm.loadPhotoBitmap(context)
    //We navigate away
    if (uis.isComplete) {
        (if (uis.isEditMode == false) onSignUpComplete else onBookerDetailsEditComplete)!!.invoke()
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
                            id = when (uis.isEditMode) {
                                null -> R.string.empty
                                false -> R.string.lb_sign_up
                                true -> R.string.lb_my_account
                            }
                        ).titleCase,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                },
                actions = {
                    if (uis.isEditMode != null)
                        IconButton(onClick = { vm.save() }) {
                            Icon(
                                imageVector = if (uis.isEditMode!!) Icons.Outlined.Check else Icons.Outlined.PersonAdd,
                                contentDescription = null,
                                tint = LocalContentColor.current
                            )
                        }
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
        floatingActionButton = {
            if (uis.isEditMode == false)
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.lb_signin_instead).titleCase) },
                    onClick = { navigateToSignIn!!.invoke() },
                    icon = { Icon(imageVector = Icons.Outlined.Login, null) },
                    shape = RoundedCornerShape(30f)
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
                keyboardActions = KeyboardActions (
                    onNext = {focusManager.moveFocus(FocusDirection.Next)}
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            //Email
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.booker.email ?: "",
                errorText = { vm.emailErrorText(it) },
                onValueChange = { vm.onEmailChange(it) },
                trailingIcon = {
                    if (!uis.booker.email.isNullOrBlank())
                        IconButton(onClick = { vm.onEmailChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = ""
                            )
                        }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
                },
                keyboardActions = KeyboardActions (
                    onNext = {focusManager.moveFocus(FocusDirection.Next)}
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,imeAction = ImeAction.Next),
                label = { Text(stringResource(R.string.lb_email).caps) },
                singleLine = true
            )

            //Password
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.booker.password ?: "",
                errorText = { vm.passwordErrorText(it) },
                onValueChange = { vm.onPasswordChange(it) },
                visualTransformation = if (uis.isPeekingPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { vm.invertPasswordPeeking() }) {
                        Icon(
                            imageVector = if (uis.isPeekingPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "",
                        )
                    }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = "")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                label = { Text(stringResource(R.string.lb_password).caps) },
                keyboardActions = KeyboardActions (
                    onNext = {focusManager.moveFocus(FocusDirection.Next)}
                ),
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

            Row(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
            ) {
                // Country Code
                OutTextField(
                    value = uis.booker.phoneCode ?: "",
                    modifier = Modifier.fillMaxWidth(0.35f),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "")
                    },
                    label = { Text(stringResource(R.string.lb_phone_code).caps) },
                    onValueChange = { vm.onPhoneCodeChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next),
                    visualTransformation = {
                        val offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int) = offset + 1
                            override fun transformedToOriginal(offset: Int) =
                                if (offset < 1) offset else offset - 1//TODO: Donot why it works
                        }
                        TransformedText(
                            text = AnnotatedString("+${it.text}"),
                            offsetMapping = offsetMapping
                        )
                    },
                    errorText = { vm.phoneCodeErrorText(it) },
                    keyboardActions = KeyboardActions (
                        onNext = {focusManager.moveFocus(FocusDirection.Next)}
                    )
                )

                // Phone
                OutTextField(
                    value = uis.booker.phone ?: "",
                    singleLine = true,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .fillMaxWidth(1f),
                    label = { Text(stringResource(R.string.lb_phone).caps) },
                    onValueChange = { vm.onPhoneChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    visualTransformation = {
                        if (vm.phoneErrorText(it.text) == null && vm.phoneCodeErrorText(uis.booker.phoneCode) == null && !uis.booker.phone.isNullOrBlank() && !uis.booker.phoneCode.isNullOrBlank()) {
                            val offsetMapping = object : OffsetMapping {
                                override fun originalToTransformed(offset: Int) =
                                    PhoneNumberUtils.formatNumber(
                                        it.text,
                                        vm.countryFromCode
                                    ).length

                                override fun transformedToOriginal(offset: Int) =
                                    uis.booker.phone?.length ?: 0
                            }
                            TransformedText(
                                text = AnnotatedString(
                                    PhoneNumberUtils.formatNumber(
                                        it.text,
                                        vm.countryFromCode
                                    )
                                ),
                                offsetMapping = offsetMapping
                            )
                        } else TransformedText(it, OffsetMapping.Identity)
                    },
                    errorText = { vm.phoneErrorText(it) },
                    trailingIcon = {
                        if (!uis.booker.phone.isNullOrBlank())
                            IconButton(onClick = { vm.onPhoneChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = ""
                                )
                            }
                    },
                    keyboardActions = KeyboardActions (
                        onNext = {focusManager.moveFocus(FocusDirection.Next)}
                    )
                )
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
                keyboardActions = KeyboardActions (
                    onNext = {focusManager.moveFocus(FocusDirection.Next)}
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )
            //Occupation
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .padding(bottom = 128.dp)
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
                keyboardActions = KeyboardActions (
                    onGo = { vm.save() }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                singleLine = true
            )
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