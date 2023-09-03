package tech.xken.tripbook.ui.screens.booking

import android.telephony.PhoneNumberUtils
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.components.OutTextField

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun BookerSignIn(
    vm: BookerSignInVM = hiltViewModel(LocalViewModelStoreOwner.current as ViewModelStoreOwner),
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    navigateBack: (() -> Unit)?,
    navigateToProfile: () -> Unit,
    onMenuClick: () -> Unit,
    onSignInComplete: () -> Unit = {},
    doOnSignOutComplete: () -> Unit,
    doOnSignOutCancelled: () -> Unit
) {
    val uis by vm.uiState.collectAsState()
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    val scope = rememberCoroutineScope()

    val statusMap = mapOf(
        BookerSignInDialogState.CONFIRM_SIGN_OUT to InfoDialogUiState(
            mainIcon = Icons.Outlined.Logout,
            title = "Do want to logout?",
            positiveText = "Confirm",
            text = buildAnnotatedString {
                append("text")
            }
        ),
        BookerSignInDialogState.HELP_MAIN_PAGE to InfoDialogUiState(
            mainIcon = Icons.Outlined.Login,
            title = "Sign In / Sign Up",
            positiveText = "I understand",
            text = buildAnnotatedString {
                append("text")
            }
        ),
    )

    when (val status = uis.dialogState) {
        BookerSignInDialogState.HELP_MAIN_PAGE -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = { vm.onDialogStateChange(BookerSignInDialogState.NONE) },
                onPositiveClick = { vm.onDialogStateChange(BookerSignInDialogState.NONE) }
            )
        }

        BookerSignInDialogState.CONFIRM_SIGN_OUT -> {
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStateChange(BookerSignInDialogState.NONE)
                    doOnSignOutCancelled()
                },
                onPositiveClick = {
                    vm.signOut {
                        doOnSignOutComplete()
                    }
                }
            )
        }

        BookerSignInDialogState.NONE -> {}
    }

    if (uis.isComplete) {
        navigateToProfile()
        vm.onCompleteChange(false)
    }

    //Hide bottom sheet when not requesting for the code
    if (!uis.isRequestingToken && scaffoldState.bottomSheetState.isExpanded)
        LaunchedEffect(Unit) {
            scaffoldState.bottomSheetState.collapse()
        }

    val focusManager: FocusManager = LocalFocusManager.current
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current


    BottomSheetScaffold(
        sheetContent = {
            PhoneVerification(
                uis,
                vm,
                fieldPadding,
                focusManager,
                keyboard,
                scaffoldState,
                scope
            )
        },
        drawerScrimColor = DrawerDefaults.scrimColor,
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
                    IconButton(onClick = { vm.onDialogStateChange(BookerSignInDialogState.HELP_MAIN_PAGE) }) {
                        Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = null)
                    }
                },
                title = {
                    Text(
                        text = stringResource(if (uis.shouldSignOut) R.string.lb_sign_out else R.string.lb_sign_in).caps,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 0.4f, topEnd = 0.4f),
        sheetPeekHeight = if (uis.isRequestingToken) BottomSheetScaffoldDefaults.SheetPeekHeight else 0.dp
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .disableComposable(uis.isLoading || uis.isRequestingToken)
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

                Button(
                    onClick = {
                        vm.signInBooker(
                            doOnStart = {
                                focusManager.clearFocus(true)
                                keyboard?.hide()
                            },
                            doOnFinish = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        )
                    }, modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(fieldPadding)
                        .fillMaxWidth(),
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
                            "${stringResource(R.string.lb_time_left).titleCase}: ${uis.resendAfterInMillis / 1000}s",
                            modifier = Modifier.padding(8.dp)
                        )

                }
                Spacer(modifier = Modifier.height(145.dp))
            }
        }
        // Check for user messages to display on the screen
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
private fun PhoneVerification(
    uis: BookerSignInUiState,
    vm: BookerSignInVM,
    fieldPadding: PaddingValues,
    focusManager: FocusManager,
    keyboard: SoftwareKeyboardController?,
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope
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
                    PhoneNumberUtils.formatNumber(
                        uis.bookerCredentials.phoneNumber,
                        vm.countryFromCode
                    )
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
                    if (uis.isRequestingToken) {
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
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
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
                    "${stringResource(R.string.lb_time_left).titleCase}: ${uis.resendAfterInMillis / 1000}s".caps,
                    modifier = Modifier.padding(start = 4.dp)
                )

        }
    }
}






