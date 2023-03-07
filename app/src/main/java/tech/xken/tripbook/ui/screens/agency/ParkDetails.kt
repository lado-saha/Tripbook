package tech.xken.tripbook.ui.screens.agency

import android.content.Intent
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.KeyboardShortcutGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.MainMenuItem
import tech.xken.tripbook.ui.components.MenuItem
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.components.PhotoPicker

@Composable
fun ParkDetails(
    vm: ParkDetailsVM = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateBack: () -> Unit,
    onComplete: (() -> Unit) = {},
    onTownClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onVehiclesClick: () -> Unit = {},
    onPersonnelClick: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    //Initialisation Processes
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current
    vm.loadPhotoBitmap(context)
    val fieldPadding = remember { PaddingValues(horizontal = 16.dp, vertical = 2.dp) }

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

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = when (uis.isEditMode) {
                                false -> R.string.lb_park_creation
                                true -> R.string.lb_park_edition
                            }
                        ).titleCase,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
//                            fontSize = 24.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { vm.save() }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
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
            AnimatedVisibility(uis.isProfileVisible) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.lb_hide_info).titleCase) },
                    onClick = { vm.onChangeProfileVisibility(false) },
                    icon = { Icon(imageVector = Icons.Outlined.VisibilityOff, null) },
                    shape = RoundedCornerShape(30f)
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
//                .disableComposable(uis.isLoading || !uis.isInitComplete)
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

            Crossfade(targetState = uis.isProfileVisible, modifier = Modifier.fillMaxSize()) {
                when (it) {
                    true -> {
                        Column(
                            modifier = Modifier,
//                                .padding(paddingValues)
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PhotoPicker(
                                photoBitmap = uis.photoBitmap,
                                placeholder = Icons.Filled.InsertPhoto,
                                onDeletePhotoClick = { vm.onPhotoUriChange(context, null) },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(120.dp),
                                onBrowseGalleryClick = {
                                    galleryLauncher.launch(arrayOf("image/*"))
                                }
                            )

                            //Name
                            OutTextField(
                                modifier = Modifier
                                    .padding(fieldPadding)
                                    .fillMaxWidth(),
                                value = uis.park.name ?: "",
                                errorText = { vm.nameErrorText(it) },
                                onValueChange = { vm.onNameChange(it) },
                                trailingIcon = {
                                    if (!uis.park.name.isNullOrBlank())
                                        IconButton(onClick = { vm.onNameChange("") }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Clear,
                                                contentDescription = ""
                                            )
                                        }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
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

                            //Email 1
                            OutTextField(
                                modifier = Modifier
                                    .padding(fieldPadding)
                                    .fillMaxWidth(),
                                value = uis.park.supportEmail1 ?: "",
                                errorText = { vm.email1ErrorText(it) },
                                onValueChange = { vm.onEmail1Change(it) },
                                trailingIcon = {
                                    if (!uis.park.supportEmail1.isNullOrBlank())
                                        IconButton(onClick = { vm.onEmail1Change("") }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Clear,
                                                contentDescription = ""
                                            )
                                        }
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
                                },
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                label = { Text(stringResource(R.string.lb_support_email_1).caps) },
                                singleLine = true
                            )

                            //Email 2
                            OutTextField(
                                modifier = Modifier
                                    .padding(fieldPadding)
                                    .fillMaxWidth(),
                                value = uis.park.supportEmail2 ?: "",
                                errorText = { vm.email2ErrorText(it) },
                                onValueChange = { vm.onEmail2Change(it) },
                                trailingIcon = {
                                    if (!uis.park.supportEmail2.isNullOrBlank())
                                        IconButton(onClick = { vm.onEmail2Change("") }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Clear,
                                                contentDescription = ""
                                            )
                                        }
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
                                },
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                label = { Text(stringResource(R.string.lb_support_email_2).caps) },
                                singleLine = true
                            )

                            // Support phone 1
                            Row(
                                modifier = Modifier
                                    .padding(fieldPadding)
                                    .fillMaxWidth(),
                            ) {
                                // Country Code
                                OutTextField(
                                    value = uis.park.supportPhone1Code ?: "",
                                    modifier = Modifier.fillMaxWidth(0.35f),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = ""
                                        )
                                    },
                                    label = { Text(stringResource(R.string.lb_phone_code).caps) },
                                    onValueChange = { vm.onPhone1CodeChange(it) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    visualTransformation = {
                                        val offsetMapping = object : OffsetMapping {
                                            override fun originalToTransformed(offset: Int) =
                                                offset + 1

                                            override fun transformedToOriginal(offset: Int) =
                                                if (offset < 1) offset else offset - 1//TODO: Donot why it works
                                        }
                                        TransformedText(
                                            text = AnnotatedString("+${it.text}"),
                                            offsetMapping = offsetMapping
                                        )
                                    },
                                    errorText = { vm.phone1CodeErrorText(it) },
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                    )
                                )

                                // Phone
                                OutTextField(
                                    value = uis.park.supportPhone1 ?: "",
                                    singleLine = true,
                                    modifier = Modifier
                                        .padding(start = 2.dp)
                                        .fillMaxWidth(1f),
                                    label = { Text(stringResource(R.string.lb_support_phone_1).caps) },
                                    onValueChange = { vm.onPhone1Change(it) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Next
                                    ),
                                    visualTransformation = {
                                        if (vm.phone1ErrorText(it.text) == null && vm.phone1CodeErrorText(
                                                uis.park.supportPhone1Code
                                            ) == null && !uis.park.supportPhone1.isNullOrBlank() && !uis.park.supportPhone1Code.isNullOrBlank()
                                        ) {
                                            val offsetMapping = object : OffsetMapping {
                                                override fun originalToTransformed(offset: Int) =
                                                    PhoneNumberUtils.formatNumber(
                                                        it.text,
                                                        uis.countryFromCode1
                                                    ).length

                                                override fun transformedToOriginal(offset: Int) =
                                                    uis.park.supportPhone1?.length ?: 0
                                            }
                                            TransformedText(
                                                text = AnnotatedString(
                                                    PhoneNumberUtils.formatNumber(
                                                        it.text,
                                                        uis.countryFromCode1
                                                    )
                                                ),
                                                offsetMapping = offsetMapping
                                            )
                                        } else TransformedText(it, OffsetMapping.Identity)
                                    },
                                    errorText = { vm.phone1ErrorText(it) },
                                    trailingIcon = {
                                        if (!uis.park.supportPhone1.isNullOrBlank())
                                            IconButton(onClick = { vm.onPhone1Change("") }) {
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

                            // Support phone 2
                            Row(
                                modifier = Modifier
                                    .padding(fieldPadding)
                                    .fillMaxWidth(),
                            ) {
                                // Country Code
                                OutTextField(
                                    value = uis.park.supportPhone2Code ?: "",
                                    modifier = Modifier.fillMaxWidth(0.35f),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = ""
                                        )
                                    },
                                    label = { Text(stringResource(R.string.lb_phone_code).caps) },
                                    onValueChange = { vm.onPhone2CodeChange(it) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    visualTransformation = {
                                        val offsetMapping = object : OffsetMapping {
                                            override fun originalToTransformed(offset: Int) =
                                                offset + 1

                                            override fun transformedToOriginal(offset: Int) =
                                                if (offset < 1) offset else offset - 1//TODO: Donot why it works
                                        }
                                        TransformedText(
                                            text = AnnotatedString("+${it.text}"),
                                            offsetMapping = offsetMapping
                                        )
                                    },
                                    errorText = { vm.phone2CodeErrorText(it) },
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                    )
                                )

                                // Phone
                                OutTextField(
                                    value = uis.park.supportPhone2 ?: "",
                                    singleLine = true,
                                    modifier = Modifier
                                        .padding(start = 2.dp)
                                        .fillMaxWidth(1f),
                                    label = { Text(stringResource(R.string.lb_support_phone_2).caps) },
                                    onValueChange = { vm.onPhone2Change(it) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Next
                                    ),
                                    visualTransformation = {
                                        if (vm.phone2ErrorText(it.text) == null && vm.phone2CodeErrorText(
                                                uis.park.supportPhone2Code
                                            ) == null && !uis.park.supportPhone2.isNullOrBlank() && !uis.park.supportPhone2Code.isNullOrBlank()
                                        ) {
                                            val offsetMapping = object : OffsetMapping {
                                                override fun originalToTransformed(offset: Int) =
                                                    PhoneNumberUtils.formatNumber(
                                                        it.text,
                                                        uis.countryFromCode1
                                                    ).length

                                                override fun transformedToOriginal(offset: Int) =
                                                    uis.park.supportPhone1?.length ?: 0
                                            }
                                            TransformedText(
                                                text = AnnotatedString(
                                                    PhoneNumberUtils.formatNumber(
                                                        it.text,
                                                        uis.countryFromCode2
                                                    )
                                                ),
                                                offsetMapping = offsetMapping
                                            )
                                        } else TransformedText(it, OffsetMapping.Identity)
                                    },
                                    errorText = { vm.phone2ErrorText(it) },
                                    trailingIcon = {
                                        if (!uis.park.supportPhone2.isNullOrBlank())
                                            IconButton(onClick = { vm.onPhone2Change("") }) {
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
                        }
                    }
                    false -> {
                        MainMenuItem(
                            imageBitmap = uis.photoBitmap,
                            title = uis.park.name ?: "Park profile",
                            subtitle = "Africa /Cameroon /West ",
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            vm.onChangeProfileVisibility(true)
                        }
                    }
                }
            }

            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 1.dp)
                    .fillMaxWidth()
            )
            MenuItem(
                imageVector = Icons.Outlined.Group,
                title = "Personnel",
                subtitle = "You've added 10 different personnel",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {

            }
            MenuItem(
                imageVector = Icons.Outlined.LocationOn,
                title = "Cities of park",
                subtitle = "You've added 2 cities",
                errorText = { null },
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                onTownClick()
            }
            MenuItem(
                imageVector = Icons.Outlined.Schedule,
                title = "Trip schedules",
                subtitle = "You've added 2 schedules",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {

            }

            MenuItem(
                imageVector = Icons.Outlined.DirectionsBus,
                title = "Vehicles",
                errorText = { null },
                subtitle = "You've added 4 different vehicles",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {

            }


        }
    }

}