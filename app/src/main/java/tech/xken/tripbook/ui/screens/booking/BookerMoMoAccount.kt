package tech.xken.tripbook.ui.screens.booking


import android.telephony.PhoneNumberUtils
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.ToggleOn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.ActionSheet
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.domain.subsetOf
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.DashboardItemNoIcon
import tech.xken.tripbook.ui.components.DashboardItemNoIconUiState
import tech.xken.tripbook.ui.components.DashboardSubItem
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.components.OutTextField
import tech.xken.tripbook.ui.components.SearchBar
import tech.xken.tripbook.ui.components.SortingDialog


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookerMoMoAccounts(
    modifier: Modifier = Modifier,
    vm: BookerMoMoAccountsVM = hiltViewModel(LocalViewModelStoreOwner.current as ViewModelStoreOwner),
    navigateBack: () -> Unit,
    navigateToDetails: () -> Unit,
) {
    val uis by vm.uiState.collectAsState()
    val stateMap = mapOf(
        BookerMoMoAccountsDialogState.ABOUT_MAIN_PAGE to InfoDialogUiState(
            mainIcon = Icons.Filled.Payments,
            title = "My MoMo accounts",
            text = buildAnnotatedString {
                append("text")
            },
            positiveText = stringResource(id = R.string.lb_i_understand)
        ),
        BookerMoMoAccountsDialogState.COULD_NOT_GET_ACCOUNTS to InfoDialogUiState(
            mainIcon = Icons.Filled.ErrorOutline,
            title = "Could not get your accounts",
            text = buildAnnotatedString {
                append("text")
            },
            positiveText = "Retry",
            otherText = "Leave",
        ),
        BookerMoMoAccountsDialogState.LEAVING_WITHOUT_ACCOUNTS to InfoDialogUiState(
            mainIcon = Icons.Filled.Warning,
            title = "Leaving without adding accounts?",
            text = buildAnnotatedString {
                append("text")
            },
            positiveText = "Stay",
            otherText = "Leave",
            isNegative = true
        ),
        BookerMoMoAccountsDialogState.DELETE_ACCOUNTS_WARNING to InfoDialogUiState(
            mainIcon = Icons.Filled.Delete,
            title = "Are you want to delete?",
            text = buildAnnotatedString {
                append("text")
            },
            positiveText = "No",
            otherText = "Delete!",
            isNegative = true
        )
    )
    when (val status = uis.dialogState) {
        BookerMoMoAccountsDialogState.NONE -> {}

        BookerMoMoAccountsDialogState.ABOUT_MAIN_PAGE -> InfoDialog(
            uis = stateMap[status]!!,
            onCloseClick = { vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE) },
            onPositiveClick = { vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE) }
        )

        BookerMoMoAccountsDialogState.COULD_NOT_GET_ACCOUNTS -> InfoDialog(
            uis = stateMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
                navigateBack()
            },
            onOtherClick = {
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
                navigateBack()
            },
            onPositiveClick = {
                vm.onInitComplete(false)
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
            }
        )

        BookerMoMoAccountsDialogState.LEAVING_WITHOUT_ACCOUNTS -> InfoDialog(
            uis = stateMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
            },
            onPositiveClick = {
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
            },
            onOtherClick = {
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
                navigateBack()
            }
        )

        BookerMoMoAccountsDialogState.DELETE_ACCOUNTS_WARNING -> InfoDialog(
            uis = stateMap[status]!!,
            onCloseClick = {
                vm.clearOnToDelete()
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
            },
            onPositiveClick = {
                vm.clearOnToDelete()
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
            },
            onOtherClick = {
                vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE)
                vm.deleteObjects()
            }
        )

        BookerMoMoAccountsDialogState.SORTING -> SortingDialog(
            onDismiss = { vm.onDialogStateChange(BookerMoMoAccountsDialogState.NONE) },
            sortFields = uis.sortFields,
            selectedField = uis.selectedSortField,
            onFieldClick = {
                vm.onSelectedSortFieldChange(it)
//                vm.onDialogStateChange(BookerOMAccountsDialogState.NONE)/**/
            }
        )
    }

//    val context = LocalContext.current
    val fieldPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    val scrollState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    vm.onSheetStateChange(BookerMoMoAccountsSheetState.NONE)
                }

                ModalBottomSheetValue.Expanded -> {}
                ModalBottomSheetValue.HalfExpanded -> {}
            }
            true
        }
    )

    LaunchedEffect(uis.sheetStatus) {
        if (uis.sheetStatus != BookerMoMoAccountsSheetState.NONE) {
            sheetState.show()
        } else if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.currentValue != ModalBottomSheetValue.Hidden) {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (uis.sheetStatus) {
                BookerMoMoAccountsSheetState.ACTIONS -> {
                    ActionSheet(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uis.toDelete.isNotEmpty())
                            ActionItem(
                                action = MainAction(
                                    R.string.lb_delete_sel_accounts,
                                    Icons.Outlined.Delete
                                ),
                                onClick = {
                                    vm.onDialogStateChange(
                                        BookerMoMoAccountsDialogState.DELETE_ACCOUNTS_WARNING
                                    )
                                    vm.onSheetStateChange(BookerMoMoAccountsSheetState.NONE)
                                },
                            )

                        ActionItem(
                            action = MainAction(
                                R.string.lb_sort_by,
                                Icons.Outlined.Sort
                            ),
                            onClick = {
                                vm.onDialogStateChange(BookerMoMoAccountsDialogState.SORTING)
                                vm.onSheetStateChange(BookerMoMoAccountsSheetState.NONE)
                            },
                        )


                        ActionItem(
                            action = MainAction(R.string.lb_about_page, Icons.Outlined.Info),
                            onClick = {
                                vm.onDialogStateChange(BookerMoMoAccountsDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(BookerMoMoAccountsSheetState.NONE)
                            },
                        )

                    }
                }

                BookerMoMoAccountsSheetState.NONE -> {}
            }
        }, sheetShape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(10), topStart = CornerSize(10),
        ), sheetState = sheetState,
        sheetElevation = 1.dp,
        scrimColor = ModalBottomSheetDefaults.scrimColor.copy(alpha = 0.1f)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                AnimatedVisibility(visible = uis.toDelete.isEmpty() && !uis.isLoading) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = stringResource(id = R.string.lb_account)) },
                        onClick = {
                            vm.onNavigateToNew()
                            navigateToDetails()
                        },
                        icon = {
                            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                        }
                    )
                }
            },
            topBar = {
                Crossfade(targetState = uis.isSearching, label = "do") { state ->
                    when (state) {
                        true -> SearchBar(
                            query = uis.query,
                            onQueryChange = { vm.onQueryChanged(it) },
                            fields = uis.searchFields,
                            onFieldClick = { vm.onSearchFieldChange(it) },
                            onBackClick = { vm.onIsSearchingChange(false) },
                            onClearQueryClick = { vm.onQueryChanged("") },
                            onMoreClick = {
                                vm.onSheetStateChange(BookerMoMoAccountsSheetState.ACTIONS)
                            }
                        )

                        false -> TopAppBar(
                            elevation = 0.dp,
                            title = {
                                Text(
                                    text = if (uis.toDelete.isEmpty()) stringResource(id = R.string.lb_my_momo_accounts).titleCase else "${uis.toDelete.size}",
                                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold), overflow = TextOverflow.Ellipsis, softWrap = false
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    if (uis.toDelete.isEmpty())
                                        navigateBack()
                                    else
                                        vm.clearOnToDelete()
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
                                if (uis.toDelete.isEmpty())
                                    IconButton(onClick = {
                                        vm.onIsSearchingChange(true)
                                    }) {
                                        Icon(
                                            Icons.Outlined.Search,
                                            contentDescription = null,
                                            tint = LocalContentColor.current
                                        )
                                    }

                                IconButton(onClick = {
                                    vm.onSheetStateChange(
                                        BookerMoMoAccountsSheetState.ACTIONS
                                    )
                                }) {
                                    Icon(
                                        Icons.Outlined.MoreVert,
                                        contentDescription = null,
                                        tint = LocalContentColor.current
                                    )
                                }
                            }
                        )
                    }
                }

            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .animateContentSize()
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = scrollState,
                contentPadding = fieldPadding
            ) {
                item {
                    AnimatedVisibility(
                        visible = uis.isLoading,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                }
                if (!uis.isLoading)
                    items(
                        uis.objects.run {
                            when (uis.selectedSortField?.ascending) {
                                true -> sortedBy {
                                    it.backingField(uis.selectedSortField!!.nameRes)
                                }

                                false -> sortedBy {
                                    it.backingField(uis.selectedSortField!!.nameRes)
                                }.reversed()

                                else -> this
                            }
                        }.run {
                            when (uis.isSearching) {
                                false -> this
                                else -> {
                                    filter {
                                        uis.query subsetOf it.backingField(uis.searchFields[0].nameRes)
                                    }
                                }
                            }
                        },
                        key = { it.phoneNumber }
                    ) { account ->
                        DashboardItemNoIcon(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            uis = DashboardItemNoIconUiState(
                                title = PhoneNumberUtils.formatNumber(
                                    account.phoneNumber,
                                    codeCountryMap["237"]!!
                                ),
                                isClickable = true,
                                isDeletable = true,
                                isLoading = false,
                                isMarkable = true,
                                isMarked = uis.toDelete.contains(account.phoneNumber)
                            ),
                            onClick = {

                                vm.onNavigateToEdit(account)
                                navigateToDetails()
                            },
                            onDeleteClick = {
                                vm.onToDeleteChange(account.phoneNumber)
                                vm.onDialogStateChange(BookerMoMoAccountsDialogState.DELETE_ACCOUNTS_WARNING)
                            },
                            onLongClick = {
                                vm.onToDeleteChange(account.phoneNumber)
                            }
                        ) {
                            DashboardSubItem(
                                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                                isError = !account.isEnabled,
                                positiveText = "Enabled",
                                errorText = "Disabled"
                            )
                        }
                    }
            }
        }
    }

    // Check for user messages to display on the screen
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookerMoMoAccountDetails(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: BookerMoMoAccountsVM,
    navigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    //Initialisation Processes
    val uis by vm.detailsUiState.collectAsState()
    val context = LocalContext.current
    //We navigate away
    if (uis.isDetailsComplete) {
//        vm.doBeforeNavBackToAccounts()
        navigateBack()
        vm.onAccountCompleteChange(false)
    }
    val fieldPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
    //To pick an image from the gallery
    val scrollState = rememberScrollState()
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    fun handleBackNav() {
        if (vm.hasAnyFieldChanged)
            vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.LEAVING_WITHOUT_SAVING)
        else if (!uis.isEditMode) vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.LEAVING_WITH_EMPTY_ACCOUNT)
        else vm.onAccountCompleteChange(true)
    }

    BackHandler(true) {
        handleBackNav()
    }

    val statusMap = mapOf(
        BookerMoMoAccountDialogState.HELP_IS_ENABLED to InfoDialogUiState(
            Icons.Outlined.ToggleOn,
            title = "Enable or disable account",
            buildAnnotatedString { append("text") },
            positiveText = stringResource(id = R.string.lb_i_understand)
        ),
        BookerMoMoAccountDialogState.ABOUT_MAIN_PAGE to InfoDialogUiState(
            Icons.Outlined.Payments,
            "My MoMo Account",
            buildAnnotatedString { append("text") },
            positiveText = stringResource(id = R.string.lb_i_understand)
        ),
        BookerMoMoAccountDialogState.LEAVING_WITHOUT_SAVING to InfoDialogUiState(
            Icons.Outlined.Save,
            "Leaving without saving changes?",
            buildAnnotatedString { append("text") },
            positiveText = "Stay",
            otherText = "Discard",
            isNegative = true
        ),
        BookerMoMoAccountDialogState.LEAVING_WITH_EMPTY_ACCOUNT to InfoDialogUiState(
            Icons.Outlined.ErrorOutline,
            "Leaving with empty account",
            buildAnnotatedString { append("text") },
            positiveText = "Stay",
            otherText = "Leave",
            isNegative = true
        )
    )

    when (val status = uis.detailsDialogState) {
        BookerMoMoAccountDialogState.NONE -> {}
        BookerMoMoAccountDialogState.ABOUT_MAIN_PAGE -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = { vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE) },
            onPositiveClick = {
                vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE)
            }
        )

        BookerMoMoAccountDialogState.LEAVING_WITHOUT_SAVING -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = { vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE) },
            onPositiveClick = {
                vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE)
                vm.saveOrUpdateAccount(
                    doOnStart = {
                        focusManager.clearFocus(true)
                        keyboard?.hide()
                    },
                )
            },
            onOtherClick = {
                vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE)
                vm.onAccountCompleteChange(true)
            }
        )

        BookerMoMoAccountDialogState.LEAVING_WITH_EMPTY_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = { vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE) },
            onPositiveClick = {
                vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE)
            },
            onOtherClick = {
                vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE)
                vm.onAccountCompleteChange(true)
            }
        )

        BookerMoMoAccountDialogState.HELP_IS_ENABLED -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = { vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE) },
            onPositiveClick = {
                vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.NONE)
            }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                title = {
                    Text(
                        text = if (uis.isEditMode) uis.formattedPhone else stringResource(id = R.string.lb_new_momo_account).titleCase,
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
                    IconButton(onClick = {
                        vm.onAccountDialogStateChange(
                            BookerMoMoAccountDialogState.ABOUT_MAIN_PAGE
                        )
                    }) {
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
                .disableComposable(uis.isLoading)
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = uis.isLoading,
                modifier = Modifier.padding(4.dp)
            ) {
                CircularProgressIndicator()
                if (uis.isLoading)
                    LaunchedEffect(Unit) {
                        scrollState.animateScrollTo(0)
                    }
            }
            Spacer(modifier = Modifier.height(16.dp))

            DashboardItemNoIcon(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemNoIconUiState(
                    title = "Enable or disable account",
                    isClickable = false,
                    isHelpable = true
                ),
                onHelpClick = {
                    vm.onAccountDialogStateChange(BookerMoMoAccountDialogState.HELP_IS_ENABLED)
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (uis.account.isEnabled) "Enabled" else "Disabled",
                        modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                    )
                    Switch(
                        checked = uis.account.isEnabled,
                        onCheckedChange = { vm.onIsActiveChange(it) },
                        modifier = Modifier.padding(start = 4.dp)
//                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }


            Row(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
            ) {
                // Country Code
                OutTextField(
                    value = "237",
                    modifier = Modifier.fillMaxWidth(0.35f),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "")
                    },
                    label = { Text(stringResource(R.string.lb_phone_code).caps) },
                    onValueChange = { },
                    enabled = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone
                OutTextField(
                    value = uis.account.phoneNumber,
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
                        if (vm.phoneErrorText(it.text) == null && uis.account.phoneNumber.isNotBlank()) {
                            val offsetMapping = object : OffsetMapping {
                                override fun originalToTransformed(offset: Int) =
                                    PhoneNumberUtils.formatNumber(
                                        it.text,
                                        codeCountryMap["237"]!!
                                    ).length

                                override fun transformedToOriginal(offset: Int) =
                                    uis.account.phoneNumber.length
                            }
                            TransformedText(
                                text = AnnotatedString(
                                    PhoneNumberUtils.formatNumber(
                                        it.text,
                                        codeCountryMap["237"]!!
                                    )
                                ),
                                offsetMapping = offsetMapping
                            )
                        } else TransformedText(it, OffsetMapping.Identity)
                    },
                    errorText = { vm.phoneErrorText(it) },
                    trailingIcon = {
                        if (uis.account.phoneNumber.isNotBlank())
                            IconButton(onClick = { vm.onPhoneChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = ""
                                )
                            }
                    },
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    enabled = !uis.isEditMode
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = {
                    vm.saveOrUpdateAccount(
                        doOnStart = {
                            focusManager.clearFocus(true)
                            keyboard?.hide()
                        },
                    )
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(fieldPadding)
                    .fillMaxWidth(),
            ) {
                Icon(
                    imageVector = if (uis.isEditMode) Icons.Outlined.Check else Icons.Outlined.Add,
                    contentDescription = null
                )
                Text(
                    stringResource(if (uis.isEditMode) R.string.lb_save else R.string.lb_create).titleCase,
                    modifier = Modifier.padding(8.dp)
                )
            }

        }
    }


// Check for user messages to display on the screen
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

