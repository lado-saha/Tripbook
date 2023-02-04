package tech.xken.tripbook.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.OutTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookerSignIn(
    vm: BookerSignInVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateBack: (() -> Unit)? = null,
    navigateToSignUp: () -> Unit,
    onSignInComplete: () -> Unit,
) {
    val uis by vm.uiState.collectAsState()
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)

    if(uis.isComplete){
        onSignInComplete()
        vm.onCompleteChange(false)
    }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = navigateBack?.let {
                    { IconButton(onClick = { it() }) {
                            Icon(Icons.Outlined.ArrowBack,
                                contentDescription = null,
                                tint = LocalContentColor.current)
                        }
                    }
                },
                title = {
                    Text(text = stringResource(R.string.lb_sign_in).titleCase,
                        style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold,
                            fontSize = 24.sp))
                },
                actions = {
                    IconButton(onClick = { vm.signInBooker() }) {
                        Icon(imageVector = Icons.Outlined.Login,
                            contentDescription = null, tint = LocalContentColor.current)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(stringResource(R.string.lb_signup_instead).titleCase) },
                onClick = { navigateToSignUp() },
                icon = { Icon(imageVector = Icons.Outlined.PersonAdd, null) },
                shape = RoundedCornerShape(30f))
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .disableComposable(uis.isLoading)
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            AnimatedVisibility(
                visible = uis.isLoading,
                modifier = Modifier.padding(4.dp)) {
                CircularProgressIndicator()
            }

            Text(stringResource(id = R.string.msg_welcome_back).caps,
                style = MaterialTheme.typography.h2.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                ),
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 64.dp, start = 2.dp, end = 2.dp)
                    .fillMaxWidth())

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
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "")
                        }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "")
                },
                label = { Text(stringResource(R.string.lb_name).caps) },
                keyboardActions = KeyboardActions (
                    onNext = {focusManager.moveFocus(FocusDirection.Next)}
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            //Password
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .padding(bottom = 64.dp)
                    .padding(bottom = 32.dp)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,
                    autoCorrect = false, imeAction = ImeAction.Go),
                label = { Text(stringResource(R.string.lb_password).caps) },
                keyboardActions = KeyboardActions(
                    onGo= {
                        vm.signInBooker()
                    }
                )
            )
        }
    }

    // Check for user messages to display on the screen
    if (uis.message != null) {
        val message = stringResource(id = uis.message!!).caps
        val actionLabel = stringResource(R.string.lb_signup_instead).titleCase
        LaunchedEffect(scaffoldState, vm, uis.message) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message, actionLabel = actionLabel
            ).also {
                vm.onMessageChange(null)
            }
        }
    }
}



