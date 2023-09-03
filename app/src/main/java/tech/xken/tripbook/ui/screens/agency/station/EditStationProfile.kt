//package tech.xken.tripbook.ui.screens.agency.station
//
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.ImageDecoder
//import android.net.Uri
//import android.os.Build
//import android.provider.MediaStore
//import android.telephony.PhoneNumberUtils
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusDirection
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.OffsetMapping
//import androidx.compose.ui.text.input.TransformedText
//import androidx.compose.ui.unit.dp
//import androidx.core.net.toUri
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import tech.xken.tripbook.R
//import tech.xken.tripbook.data.models.*
//import tech.xken.tripbook.data.sources.agency.AgencyRepository
//import tech.xken.tripbook.domain.*
//import tech.xken.tripbook.ui.components.OutTextField
//import tech.xken.tripbook.ui.components.PhotoPicker
//import tech.xken.tripbook.ui.navigation.AgencyArgs
//import java.util.*
//import javax.inject.Inject
//
//data class StationProfileUiState(
//    val station: Station = Station.new(),
//    val isLoading: Boolean = false,
//    val message: Int? = null,
//    val isInitComplete: Boolean = false,
//    val isComplete: Boolean = false,
//     val calendar: Calendar = Calendar.getInstance(),
//    val photoUri: Uri? = null,
//    val photoBitmap: Bitmap? = null,
//    val isEditMode: Boolean = false,
//) {
//    val countryFromCode1 = codeCountryMap[station.supportPhone1Code] ?: ""
//    val countryFromCode2 = codeCountryMap[station.supportPhone2Code] ?: ""
//}
//
//@HiltViewModel
//class EditStationProfileVM @Inject constructor(
//    private val repo: AgencyRepository,
//    savedStateHandle: SavedStateHandle,
//) : ViewModel() {
//    private val _isLoading = MutableStateFlow(false)
//    private val _message = MutableStateFlow<Int?>(null)
//    private val _calendar = MutableStateFlow(Calendar.getInstance())
//    private val _photoUri = MutableStateFlow<Uri?>(null)
//    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
//    private val _isInitComplete = MutableStateFlow(false)
//    private val _isComplete = MutableStateFlow(false)
//    private val _station =
//        MutableStateFlow(Station.new().copy(id = savedStateHandle[AgencyArgs.AGENCY_STATION_ID]!!))
//    private val _isEditMode =
//        MutableStateFlow(savedStateHandle.get<Boolean>(AgencyArgs.AGENCY_STATION_IS_EDIT_MODE)!!)
//
//    val uiState = combine(
//        _isLoading,//0
//        _message,//1
//        _station,//2
//        _calendar,//3
//        _photoUri,//4
//        _photoBitmap,//5
//        _isInitComplete,//6
//        _isComplete,//7
//        _isEditMode
//    ) { args ->
//        StationProfileUiState(
//            isLoading = args[0] as Boolean,
//            message = args[1] as Int?,
//            station = args[2] as Station,
//            calendar = args[3] as Calendar,
//            photoUri = args[4] as Uri?,
//            photoBitmap = args[5] as Bitmap?,
//            isInitComplete = args[6] as Boolean,
//            isComplete = args[7] as Boolean,
//            isEditMode = args[8] as Boolean
//        ).also { loadStationProfile() }
//    }.stateIn(
//        scope = viewModelScope, started = WhileUiSubscribed, initialValue = StationProfileUiState()
//    )
//
//    fun onNameChange(new: String) {
//        _station.value = _station.value.copy(name = new)
//    }
//
//    fun onEmail1Change(new: String) {
//        _station.value = _station.value.copy(supportEmail1 = new)
//    }
//
//    fun onEmail2Change(new: String) {
//        _station.value = _station.value.copy(supportEmail2 = new)
//    }
//
//    fun onPhone1Change(new: String) {
//        _station.value = _station.value.copy(supportPhone1 = new)
//    }
//
//    fun onPhone2Change(new: String) {
//        _station.value = _station.value.copy(supportPhone2 = new)
//    }
//
//    fun onPhone1CodeChange(new: String) {
//        _station.value = _station.value.copy(supportPhone1Code = new)
//    }
//
//    fun onPhone2CodeChange(new: String) {
//        _station.value = _station.value.copy(supportPhone2Code = new)
//    }
//
//    private fun loadStationProfile() {
//        if (!_isInitComplete.value && _isEditMode.value)
//            viewModelScope.launch {
//                onLoading(true)
//                repo.stationsFromIds(listOf(_station.value.id)).also {
//                    when (it) {
//                        is Results.Failure -> {
//                            _isComplete.value = true
//                        }
//                        is Results.Success -> {
//                            val station = it.data.first()
//                            _station.value = station
//                            _photoUri.value = station.photoUrl?.toUri()
//                        }
//                    }
//                }
//                onLoading(false)
//                _isInitComplete.value = true
//            }
//    }
//
//    fun onPhotoUriChange(context: Context, new: Uri?) {
//        _photoUri.value = new
//        _station.value = _station.value.copy(photoUrl = new?.toString())
//        _photoBitmap.value = null
//        if (new == null) {
//            return
//        }
//        //We try to load the image
//        loadPhotoBitmap(context)
//    }
//
//    fun loadPhotoBitmap(context: Context) {
//        if (_photoBitmap.value == null && _photoUri.value != null) try {
//            _photoUri.value?.let {
//                _photoBitmap.value = if (Build.VERSION.SDK_INT < 28) {
//                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
//                } else {
//                    val source = ImageDecoder.createSource(context.contentResolver, it)
//                    ImageDecoder.decodeBitmap(source)
//                }
//            }
//        } catch (e: Exception) {
//            //TODO: Handle Exception
//        }
//    }
//
//    fun onLoading(new: Boolean) {
//        _isLoading.value = new
//    }
//
//
//    fun onChangeMessage(new: Int?) {
//        _message.value = new
//    }
//
//    fun nameErrorText(text: String? = _station.value.name) =
//        if (text.isNullOrBlank()) R.string.msg_required_field else null
//
//    fun email1ErrorText(text: String? = _station.value.supportEmail1) =
//        if (text.isNullOrBlank()) R.string.msg_required_field else null
//
//    fun email2ErrorText(text: String? = _station.value.supportEmail2) = null/*TODO: Email checker*/
//
//    fun phone1CodeErrorText(text: String? = _station.value.supportPhone1Code) = when {
//        text.isNullOrBlank() -> R.string.msg_required_field
//        isCodeInvalid(text) -> R.string.msg_invalid_field
//        else -> null
//    }
//
//    fun phone2CodeErrorText(text: String? = _station.value.supportPhone2Code) = when {
//        text.isNullOrBlank() -> null
//        isCodeInvalid(text) -> R.string.msg_invalid_field
//        else -> null
//    }
//
//    fun phone1ErrorText(text: String? = _station.value.supportPhone1) = when {
//        text.isNullOrBlank() -> R.string.msg_required_field
//        isPhoneInvalid(text, _station.value.supportPhone1Code) -> R.string.msg_invalid_field
//        else -> null
//    }
//
//    fun phone2ErrorText(text: String? = _station.value.supportPhone2) = when {
//        text.isNullOrBlank() -> null
//        isPhoneInvalid(text, _station.value.supportPhone2Code) -> R.string.msg_invalid_field
//        else -> null
//    }
//
//    val isNoError
//        get() = nameErrorText() == null && phone1ErrorText() == null && phone2ErrorText() == null && email1ErrorText() == null && email2ErrorText() == null
//
//    fun onCompleteChange(new: Boolean) {
//        _isComplete.value = new
//    }
//
//    fun save() {
//        if (isNoError) viewModelScope.launch {
//            onLoading(true)
//            repo.saveStations(listOf(_station.value))
//            onChangeMessage(R.string.msg_success_saving_station)
//            onLoading(false)
//            _isComplete.value = true
//        }
//        else onChangeMessage(R.string.msg_fields_contain_errors)
//    }
//
//    fun onMessageChange(new: Int?){
//        _message.value = new
//    }
//
//}
//
//@Composable
//fun EditStationProfile(
//    vm: EditStationProfileVM = hiltViewModel(),
//    scaffoldState: ScaffoldState = rememberScaffoldState(),
//    onComplete: (id: String) -> Unit,
//    navigateBack: () -> Unit,
//) {
//    val focusManager = LocalFocusManager.current
//    //Initialisation Processes
//    val uis by vm.uiState.collectAsState()
//    if (uis.isComplete) {
//        onComplete(uis.station.id)
//        vm.onCompleteChange(false)
//    }
//    val context = LocalContext.current
//    vm.loadPhotoBitmap(context)
//    val fieldPadding = remember { PaddingValues(horizontal = 16.dp, vertical = 2.dp) }
//
//    val galleryLauncher =
//        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
//            uri?.let {
//                val persistentUri = it
//                context.contentResolver.takePersistableUriPermission(
//                    persistentUri,
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
//                vm.onPhotoUriChange(context, persistentUri)
//            }
//        }
//
//    Scaffold(
//        scaffoldState = scaffoldState,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = stringResource(id = R.string.lb_profile).titleCase,
//                        style = LocalTextStyle.current.copy(
//                            fontWeight = FontWeight.Bold,
//                        )
//                    )
//                },
//                actions = {
//                    IconButton(onClick = { vm.save() }) {
//                        Icon(
//                            imageVector = Icons.Outlined.Check,
//                            contentDescription = null,
//                            tint = LocalContentColor.current
//                        )
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navigateBack() }) {
//                        Icon(
//                            Icons.Outlined.ArrowBack,
//                            contentDescription = null,
//                            tint = LocalContentColor.current
//                        )
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            )
//        },
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
////                .disableComposable(uis.isLoading || !uis.isInitComplete)
//                .padding(paddingValues)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            AnimatedVisibility(
//                visible = uis.isLoading,
//                modifier = Modifier.padding(4.dp)
//            ) {
//                CircularProgressIndicator()
//            }
//
//            PhotoPicker(
//                photoBitmap = uis.photoBitmap,
//                placeholder = Icons.Filled.DirectionsBus,
//                onDeletePhotoClick = { vm.onPhotoUriChange(context, null) },
//                modifier = Modifier
//                    .padding(4.dp)
//                    .size(120.dp),
//                onBrowseGalleryClick = {
//                    galleryLauncher.launch(arrayOf("image/*"))
//                }
//            )
//
//            //Name
//            OutTextField(
//                modifier = Modifier
//                    .padding(fieldPadding)
//                    .fillMaxWidth(),
//                value = uis.station.name ?: "",
//                errorText = { vm.nameErrorText(it) },
//                onValueChange = { vm.onNameChange(it) },
//                trailingIcon = {
//                    if (!uis.station.name.isNullOrBlank())
//                        IconButton(onClick = { vm.onNameChange("") }) {
//                            Icon(
//                                imageVector = Icons.Outlined.Clear,
//                                contentDescription = ""
//                            )
//                        }
//                },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Bookmark,
//                        contentDescription = ""
//                    )
//                },
//                label = { Text(stringResource(R.string.lb_name).caps) },
//                singleLine = true,
//                keyboardActions = KeyboardActions(
//                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                ),
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
//            )
//
//            //Email 1
//            OutTextField(
//                modifier = Modifier
//                    .padding(fieldPadding)
//                    .fillMaxWidth(),
//                value = uis.station.supportEmail1 ?: "",
//                errorText = { vm.email1ErrorText(it) },
//                onValueChange = { vm.onEmail1Change(it) },
//                trailingIcon = {
//                    if (!uis.station.supportEmail1.isNullOrBlank())
//                        IconButton(onClick = { vm.onEmail1Change("") }) {
//                            Icon(
//                                imageVector = Icons.Outlined.Clear,
//                                contentDescription = ""
//                            )
//                        }
//                },
//                leadingIcon = {
//                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
//                },
//                keyboardActions = KeyboardActions(
//                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                ),
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email,
//                    imeAction = ImeAction.Next
//                ),
//                label = { Text(stringResource(R.string.lb_support_email_1).caps) },
//                singleLine = true
//            )
//
//            //Email 2
//            OutTextField(
//                modifier = Modifier
//                    .padding(fieldPadding)
//                    .fillMaxWidth(),
//                value = uis.station.supportEmail2 ?: "",
//                errorText = { vm.email2ErrorText(it) },
//                onValueChange = { vm.onEmail2Change(it) },
//                trailingIcon = {
//                    if (!uis.station.supportEmail2.isNullOrBlank())
//                        IconButton(onClick = { vm.onEmail2Change("") }) {
//                            Icon(
//                                imageVector = Icons.Outlined.Clear,
//                                contentDescription = ""
//                            )
//                        }
//                },
//                leadingIcon = {
//                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
//                },
//                keyboardActions = KeyboardActions(
//                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                ),
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email,
//                    imeAction = ImeAction.Next
//                ),
//                label = { Text(stringResource(R.string.lb_support_email_2).caps) },
//                singleLine = true
//            )
//
//            // Support phone 1
//            Row(
//                modifier = Modifier
//                    .padding(fieldPadding)
//                    .fillMaxWidth(),
//            ) {
//                // Country Code
//                OutTextField(
//                    value = uis.station.supportPhone1Code ?: "",
//                    modifier = Modifier.fillMaxWidth(0.35f),
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Phone,
//                            contentDescription = ""
//                        )
//                    },
//                    label = { Text(stringResource(R.string.lb_phone_code).caps) },
//                    onValueChange = { vm.onPhone1CodeChange(it) },
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number,
//                        imeAction = ImeAction.Next
//                    ),
//                    visualTransformation = {
//                        val offsetMapping = object : OffsetMapping {
//                            override fun originalToTransformed(offset: Int) =
//                                offset + 1
//
//                            override fun transformedToOriginal(offset: Int) =
//                                if (offset < 1) offset else offset - 1//TODO: Donot why it works
//                        }
//                        TransformedText(
//                            text = AnnotatedString("+${it.text}"),
//                            offsetMapping = offsetMapping
//                        )
//                    },
//                    errorText = { vm.phone1CodeErrorText(it) },
//                    keyboardActions = KeyboardActions(
//                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                    )
//                )
//
//                // Phone
//                OutTextField(
//                    value = uis.station.supportPhone1 ?: "",
//                    singleLine = true,
//                    modifier = Modifier
//                        .padding(start = 2.dp)
//                        .fillMaxWidth(1f),
//                    label = { Text(stringResource(R.string.lb_support_phone_1).caps) },
//                    onValueChange = { vm.onPhone1Change(it) },
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Phone,
//                        imeAction = ImeAction.Next
//                    ),
//                    visualTransformation = {
//                        if (vm.phone1ErrorText(it.text) == null && vm.phone1CodeErrorText(
//                                uis.station.supportPhone1Code
//                            ) == null && !uis.station.supportPhone1.isNullOrBlank() && !uis.station.supportPhone1Code.isNullOrBlank()
//                        ) {
//                            val offsetMapping = object : OffsetMapping {
//                                override fun originalToTransformed(offset: Int) =
//                                    PhoneNumberUtils.formatNumber(
//                                        it.text,
//                                        uis.countryFromCode1
//                                    ).length
//
//                                override fun transformedToOriginal(offset: Int) =
//                                    uis.station.supportPhone1?.length ?: 0
//                            }
//                            TransformedText(
//                                text = AnnotatedString(
//                                    PhoneNumberUtils.formatNumber(
//                                        it.text,
//                                        uis.countryFromCode1
//                                    )
//                                ),
//                                offsetMapping = offsetMapping
//                            )
//                        } else TransformedText(it, OffsetMapping.Identity)
//                    },
//                    errorText = { vm.phone1ErrorText(it) },
//                    trailingIcon = {
//                        if (!uis.station.supportPhone1.isNullOrBlank())
//                            IconButton(onClick = { vm.onPhone1Change("") }) {
//                                Icon(
//                                    imageVector = Icons.Outlined.Clear,
//                                    contentDescription = ""
//                                )
//                            }
//                    },
//                    keyboardActions = KeyboardActions(
//                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                    )
//                )
//            }
//
//            // Support phone 2
//            Row(
//                modifier = Modifier
//                    .padding(fieldPadding)
//                    .fillMaxWidth(),
//            ) {
//                // Country Code
//                OutTextField(
//                    value = uis.station.supportPhone2Code ?: "",
//                    modifier = Modifier.fillMaxWidth(0.35f),
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Phone,
//                            contentDescription = ""
//                        )
//                    },
//                    label = { Text(stringResource(R.string.lb_phone_code).caps) },
//                    onValueChange = { vm.onPhone2CodeChange(it) },
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number,
//                        imeAction = ImeAction.Next
//                    ),
//                    visualTransformation = {
//                        val offsetMapping = object : OffsetMapping {
//                            override fun originalToTransformed(offset: Int) =
//                                offset + 1
//
//                            override fun transformedToOriginal(offset: Int) =
//                                if (offset < 1) offset else offset - 1//TODO: Donot why it works
//                        }
//                        TransformedText(
//                            text = AnnotatedString("+${it.text}"),
//                            offsetMapping = offsetMapping
//                        )
//                    },
//                    errorText = { vm.phone2CodeErrorText(it) },
//                    keyboardActions = KeyboardActions(
//                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                    )
//                )
//
//                // Phone
//                OutTextField(
//                    value = uis.station.supportPhone2 ?: "",
//                    singleLine = true,
//                    modifier = Modifier
//                        .padding(start = 2.dp)
//                        .fillMaxWidth(1f),
//                    label = { Text(stringResource(R.string.lb_support_phone_2).caps) },
//                    onValueChange = { vm.onPhone2Change(it) },
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Phone,
//                        imeAction = ImeAction.Next
//                    ),
//                    visualTransformation = {
//                        if (vm.phone2ErrorText(it.text) == null && vm.phone2CodeErrorText(
//                                uis.station.supportPhone2Code
//                            ) == null && !uis.station.supportPhone2.isNullOrBlank() && !uis.station.supportPhone2Code.isNullOrBlank()
//                        ) {
//                            val offsetMapping = object : OffsetMapping {
//                                override fun originalToTransformed(offset: Int) =
//                                    PhoneNumberUtils.formatNumber(
//                                        it.text,
//                                        uis.countryFromCode1
//                                    ).length
//
//                                override fun transformedToOriginal(offset: Int) =
//                                    uis.station.supportPhone1?.length ?: 0
//                            }
//                            TransformedText(
//                                text = AnnotatedString(
//                                    PhoneNumberUtils.formatNumber(
//                                        it.text,
//                                        uis.countryFromCode2
//                                    )
//                                ),
//                                offsetMapping = offsetMapping
//                            )
//                        } else TransformedText(it, OffsetMapping.Identity)
//                    },
//                    errorText = { vm.phone2ErrorText(it) },
//                    trailingIcon = {
//                        if (!uis.station.supportPhone2.isNullOrBlank())
//                            IconButton(onClick = { vm.onPhone2Change("") }) {
//                                Icon(
//                                    imageVector = Icons.Outlined.Clear,
//                                    contentDescription = ""
//                                )
//                            }
//                    },
//                    keyboardActions = KeyboardActions(
//                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
//                    )
//                )
//            }
//        }
//    }
//
//
//    if (uis.message != null) {
//        val message = stringResource(id = uis.message!!).caps
//        LaunchedEffect(scaffoldState, vm, uis.message) {
//            scaffoldState.snackbarHostState.showSnackbar(
//                message = message
//            )
//            vm.onMessageChange(null)
//        }
//    }
//}