package tech.xken.tripbook.ui.screens.booking

import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PhonelinkErase
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.ActionSheet
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.components.OutTextField

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun BookerAuthentication(
    vm: BookerAuthenticationVM = hiltViewModel(LocalViewModelStoreOwner.current as ViewModelStoreOwner),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateBack: (() -> Unit)?,
    navigateToProfile: () -> Unit,
    onMenuClick: () -> Unit,
    onSignInComplete: () -> Unit = {},
    doOnSignOutComplete: () -> Unit,
    doOnSignOutCancelled: () -> Unit
) {
    val uis by vm.uiState.collectAsState()
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    val dialogStatusMap = mapOf(
        BookerAuthenticationDialogState.ABOUT_SIGN_IN to InfoDialogUiState(
            mainIcon = Icons.Outlined.Login,
            title = "SignIn ",
            positiveText = "I understand",
            text = buildAnnotatedString {
                append("text")
            }
        ),
        BookerAuthenticationDialogState.ABOUT_SIGN_UP to InfoDialogUiState(
            mainIcon = Icons.Outlined.PersonAdd,
            title = "SignUp",
            positiveText = "I understand",
            text = buildAnnotatedString {
                append("text")
            }
        ),
        BookerAuthenticationDialogState.CONFIRM_SIGN_OUT to InfoDialogUiState(
            mainIcon = Icons.Outlined.Logout,
            title = "Do want to logout?",
            positiveText = "Confirm",
            text = buildAnnotatedString {
                append("text")
            }
        ),
        BookerAuthenticationDialogState.ABOUT_MAIN_PAGE to InfoDialogUiState(
            mainIcon = Icons.Outlined.Person,
            title = "SignIn / SignUp",
            positiveText = "I understand",
            text = buildAnnotatedString {
                append("text")
            }
        ),
        BookerAuthenticationDialogState.SIGN_UP_INSTEAD to InfoDialogUiState(
            mainIcon = Icons.Outlined.ErrorOutline,
            title = "${uis.bookerCredentials.formatedPhoneNumber} Not Found!",
            positiveText = "SignUp Instead",
            otherText = "Cancel",
            text = buildAnnotatedString {
                append("text")
            }
        ),
    )
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {

            true
        }
    )
    when (val status = uis.dialogState) {
        BookerAuthenticationDialogState.ABOUT_MAIN_PAGE -> {
            InfoDialog(
                uis = dialogStatusMap[status]!!,
                onCloseClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) },
                onPositiveClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) }
            )
        }

        BookerAuthenticationDialogState.CONFIRM_SIGN_OUT -> {
            InfoDialog(
                uis = dialogStatusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStateChange(BookerAuthenticationDialogState.NONE)
                    doOnSignOutCancelled()
                },
                onPositiveClick = {
                    vm.signOut {
                        doOnSignOutComplete()
                    }
                }
            )
        }

        BookerAuthenticationDialogState.NONE -> {}
        BookerAuthenticationDialogState.ABOUT_SIGN_IN -> InfoDialog(
            uis = dialogStatusMap[status]!!,
            onCloseClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) },
            onPositiveClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) }
        )

        BookerAuthenticationDialogState.ABOUT_SIGN_UP -> InfoDialog(
            uis = dialogStatusMap[status]!!,
            onCloseClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) },
            onPositiveClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) }
        )

        BookerAuthenticationDialogState.SIGN_UP_INSTEAD -> InfoDialog(
            uis = dialogStatusMap[status]!!,
            onCloseClick = { vm.onDialogStateChange(BookerAuthenticationDialogState.NONE) },
            onPositiveClick = {
                vm.authBooker({}, {}, isCreation = true)
                vm.onDialogStateChange(BookerAuthenticationDialogState.NONE)
            },
            onOtherClick = {
                vm.onDialogStateChange(BookerAuthenticationDialogState.NONE)
            }
        )
    }
    if (uis.isComplete) {
        navigateToProfile()
        vm.onCompleteChange(false)
    }
    LaunchedEffect(uis.sheetState) {
        if (uis.sheetState != BookerAuthenticationSheetStatus.NONE) {
            sheetState.show()
        }
        else if (sheetState.targetValue != ModalBottomSheetValue.Hidden) {
            Log.d("Called Me", "${uis.sheetState.name} to hide")
            sheetState.hide()
        }
    }
    val focusManager: FocusManager = LocalFocusManager.current
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    ModalBottomSheetLayout(
        sheetContent = {
            when (uis.sheetState) {
                BookerAuthenticationSheetStatus.PHONE_CONFIRMATION -> PhoneVerification(
                    uis,
                    vm,
                    fieldPadding,
                    focusManager,
                    keyboard,
                )

                BookerAuthenticationSheetStatus.ACTIONS -> {
                    ActionSheet(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        ActionItem(
                            action = MainAction(
                                R.string.lb_lost_my_phone,
                                Icons.Outlined.PhonelinkErase
                            ),
                            onClick = {
                                vm.onDialogStateChange(BookerAuthenticationDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(BookerAuthenticationSheetStatus.NONE)
                            },
                        )

                        ActionItem(
                            action = MainAction(R.string.lb_forgot_pswd, Icons.Outlined.Password),
                            onClick = {
                                vm.onDialogStateChange(BookerAuthenticationDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(BookerAuthenticationSheetStatus.NONE)
                            },
                        )
                        ActionItem(
                            action = MainAction(R.string.lb_help_sign_in, Icons.Outlined.Login),
                            onClick = {
                                vm.onDialogStateChange(BookerAuthenticationDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(BookerAuthenticationSheetStatus.NONE)
                            },
                        )
                        ActionItem(
                            action = MainAction(R.string.lb_help_sign_up, Icons.Outlined.PersonAdd),
                            onClick = {
                                vm.onDialogStateChange(BookerAuthenticationDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(BookerAuthenticationSheetStatus.NONE)
                            },
                        )
                        ActionItem(
                            action = MainAction(R.string.lb_about_page, Icons.Outlined.Info),
                            onClick = {
                                vm.onDialogStateChange(BookerAuthenticationDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(BookerAuthenticationSheetStatus.NONE)
                            },
                        )

                    }
                }

                BookerAuthenticationSheetStatus.NONE -> {}
            }
        }, sheetShape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(10), topStart = CornerSize(10),
        ), sheetState = sheetState,
        sheetElevation = 1.dp,
        scrimColor = ModalBottomSheetDefaults.scrimColor.copy(alpha = 0.1f)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onMenuClick() }) {
                            Icon(
                                Icons.Outlined.Menu,
                                contentDescription = null,
                                tint = LocalContentColor.current
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { vm.onSheetStateChange(BookerAuthenticationSheetStatus.ACTIONS) }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null,
                                tint = LocalContentColor.current
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(if (uis.shouldSignOut) R.string.lb_sign_out else R.string.lb_sign_in_or_up).caps,
                            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .disableComposable(uis.isLoading)
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = uis.isLoading,
                    modifier = Modifier.padding(4.dp)
                ) {
                    CircularProgressIndicator()
                }
                if (!uis.shouldSignOut) {
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
                    Row(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                    ) {
                        // Country Code
                        OutTextField(
                            value = uis.bookerCredentials.phoneCode ?: "",
                            modifier = Modifier.fillMaxWidth(0.35f),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = "")
                            },
                            label = { Text(stringResource(R.string.lb_phone_code).caps) },
                            onValueChange = { vm.onPhoneCodeChange(it) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
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
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Next) }
                            )
                        )

                        // Phone
                        OutTextField(
                            value = uis.bookerCredentials.phoneNumber,
                            singleLine = true,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .fillMaxWidth(1f),
                            label = { Text(stringResource(R.string.lb_phone).caps) },
                            onValueChange = { vm.onPhoneChange(it) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            visualTransformation = {
                                if (vm.phoneErrorText(it.text) == null && vm.phoneCodeErrorText(uis.bookerCredentials.phoneCode) == null && uis.bookerCredentials.phoneNumber.isNotBlank() && uis.bookerCredentials.phoneCode.isNotBlank()) {
                                    val offsetMapping = object : OffsetMapping {
                                        override fun originalToTransformed(offset: Int) =
                                            PhoneNumberUtils.formatNumber(
                                                it.text,
                                                vm.countryFromCode
                                            ).length

                                        override fun transformedToOriginal(offset: Int) =
                                            uis.bookerCredentials.phoneNumber.length
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
                                if (!uis.bookerCredentials.phoneNumber.isNullOrBlank())
                                    IconButton(onClick = { vm.onPhoneChange("") }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Clear,
                                            contentDescription = ""
                                        )
                                    }
                            },
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Next) }
                            )
                        )
                    }

                    //Password
                    OutTextField(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .fillMaxWidth(),
                        value = uis.bookerCredentials.password ?: "",
                        errorText = { vm.passwordErrorText(it) },
                        onValueChange = { vm.onPasswordChange(it) },
                        visualTransformation = if (uis.isPeekingPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            if (uis.bookerCredentials.password.isNotBlank())
                                IconButton(onClick = { vm.invertPasswordPeeking() }) {
                                    Icon(
                                        imageVector = if (uis.isPeekingPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.run { if (vm.passwordErrorText() == null) primary else error }
                                    )
                                }
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Password, contentDescription = "")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            autoCorrect = false, imeAction = ImeAction.Go
                        ),
                        label = { Text(stringResource(R.string.lb_password).caps) },
                        keyboardActions = KeyboardActions(
                            onGo = {
                                keyboard?.hide()
                            }
                        )
                    )

                    Row(
                        modifier = Modifier
                            .padding(fieldPadding)
                            .padding(top = 32.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                vm.authBooker(
                                    doOnStart = {
                                        focusManager.clearFocus(true)
                                        keyboard?.hide()
                                    },
                                    doOnFinish = {

                                    },
                                    isCreation = true
                                )
                            }, modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .weight(0.5f),
                            enabled = uis.resendAfterInMillis == 0L
                        ) {
                            Icon(imageVector = Icons.Outlined.PersonAdd, contentDescription = null)

                            if (uis.resendAfterInMillis == 0L)
                                Text(
                                    stringResource(R.string.lb_sign_up).titleCase,
                                    modifier = Modifier.padding(8.dp)
                                )
                            else
                                Text(
                                    "${stringResource(R.string.lb_wait).titleCase}: ${uis.resendAfterInMillis / 1000}s",
                                    modifier = Modifier.padding(8.dp)
                                )
                        }

                        Button(
                            onClick = {
                                vm.authBooker(
                                    doOnStart = {
                                        focusManager.clearFocus(true)
                                        keyboard?.hide()
                                    },
                                    isCreation = false,
                                    doOnFinish = {
//                                        vm.onSheetStateChange(BookerAuthenticationSheetState.PHONE_CONFIRMATION)
                                    }
                                )
                            }, modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .weight(0.5f),
                            enabled = uis.resendAfterInMillis == 0L
                        ) {
                            Icon(imageVector = Icons.Outlined.Login, contentDescription = null)

                            if (uis.resendAfterInMillis == 0L)
                                Text(
                                    stringResource(R.string.lb_sign_in).titleCase,
                                    modifier = Modifier.padding(8.dp)
                                )
                            else
                                Text(
                                    "${stringResource(R.string.lb_wait).titleCase}: ${uis.resendAfterInMillis / 1000}s",
                                    modifier = Modifier.padding(8.dp)
                                )

                        }
                    }
                    Spacer(modifier = Modifier.height(145.dp))
                }
            }

            if (uis.message != null) {
                val message = stringResource(id = uis.message!!).caps
                val closeLabel = stringResource(id = R.string.lb_close)

                LaunchedEffect(uis.message) {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short,
                        actionLabel = closeLabel
                    ).also {
                        when (it) {
                            SnackbarResult.Dismissed -> vm.onMessageChange(null)
                            SnackbarResult.ActionPerformed -> vm.onMessageChange(null)
                        }
                    }
                }
            }
        }
        // Check for user messages to display on the screen

    }

}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
private fun PhoneVerification(
    uis: BookerAuthenticationUiState,
    vm: BookerAuthenticationVM,
    fieldPadding: PaddingValues,
    focusManager: FocusManager,
    keyboard: SoftwareKeyboardController?,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .disableComposable(uis.isLoading),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(id = R.string.lb_phone_verification).titleCase}: ${
                    uis.bookerCredentials.formatedPhoneNumber
                }",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.9f, true),
            )

            IconButton(
                onClick = { vm.cancelVerification() }, modifier = Modifier
                    .padding(2.dp)
                    .weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "",
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.msg_token_has_been_sent), modifier = Modifier
                .padding(fieldPadding)
                .fillMaxWidth()
        )

        OutTextField(
            modifier = Modifier
                .padding(fieldPadding)
                .fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Sms, contentDescription = "")
            },
            label = { Text(text = stringResource(R.string.lb_10_digit_code).titleCase) },
            value = uis.bookerCredentials.token ?: "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                vm.onTokenChange(it)
            },
            errorText = { vm.tokenErrorText(it) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                vm.verifyBookerPhone(
                    doOnStart = {
                        focusManager.clearFocus(true)
                        keyboard?.hide()
                    }
                ) {
                    // If the verification fails, we retry
                    if (uis.sheetState == BookerAuthenticationSheetStatus.NONE) {
                        focusManager.moveFocus(FocusDirection.Up)
                        keyboard?.show()
                    }
                }
            },
            modifier = Modifier
                .padding(fieldPadding)
                .fillMaxWidth(),
        ) {
            Icon(imageVector = Icons.Outlined.Done, contentDescription = null)
            Text(
                text = stringResource(id = R.string.lb_confirm_token).caps,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        TextButton(
            onClick = {
                vm.resendConfirmationToken(doOnStart = {
                    focusManager.clearFocus(true)
                    keyboard?.hide()
                }
                ) {
                    focusManager.moveFocus(FocusDirection.Up)
                    keyboard?.show()
//                    vm.onSheetStateChange(BookerAuthenticationSheetState.PHONE_CONFIRMATION)
                }
            },
            modifier = Modifier.padding(4.dp),
            enabled = uis.resendAfterInMillis == 0L
        ) {
            Icon(imageVector = Icons.Outlined.RestartAlt, contentDescription = null)
            if (uis.resendAfterInMillis == 0L)
                Text(
                    stringResource(id = R.string.lb_resend_token).titleCase,
                    modifier = Modifier.padding(start = 4.dp)
                )
            else
                Text(
                    "${stringResource(R.string.lb_wait).titleCase}: ${uis.resendAfterInMillis / 1000}s".caps,
                    modifier = Modifier.padding(start = 4.dp)
                )

        }
    }
}






