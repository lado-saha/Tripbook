@file:OptIn(ExperimentalMaterialApi::class)

package tech.xken.tripbook.ui.screens.booking

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.DomainAdd
import androidx.compose.material.icons.outlined.EmojiTransportation
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import tech.xken.tripbook.R
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.ActionSheet
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.domain.NetworkState
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.di.NetworkStateFlowAnnot
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.DashboardItem
import tech.xken.tripbook.ui.components.DashboardItemUiState
import tech.xken.tripbook.ui.components.DashboardSubItem
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.screens.agency.MainAgencyActivity
import javax.inject.Inject

data class AgencyPortalUiState(
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val dialogStatus: AgencyPortalDialogState = AgencyPortalDialogState.NONE,
    val has: Boolean = false,
    val hasAgencyConfigs: Boolean = false,
    val sheetStatus: AgencyPortalSheetState = AgencyPortalSheetState.NONE,
)

enum class AgencyPortalSheetState {
    ACTIONS, NONE
}

enum class AgencyPortalDialogState {
    ABOUT_CREATE_AGENCY, ABOUT_LOGIN_TO_AGENCY, ABOUT_MAIN_PAGE, NONE, FAILED_GET_YOUR_STATUS
}

@HiltViewModel
class AgencyPortalVM @Inject constructor(
    private val repo: BookerRepository,
    private val authRepo: AuthRepo,
    val savedStateHandle: SavedStateHandle,
    @NetworkStateFlowAnnot val networkState: NetworkState
) : ViewModel() {
    private val _message = MutableStateFlow<Int?>(null)
    private val _isInitComplete = MutableStateFlow(!authRepo.hasAccount)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogStatus = MutableStateFlow(AgencyPortalDialogState.NONE)
    private val _hasAccount = MutableStateFlow(authRepo.hasAccount)
    private val _sheetState = MutableStateFlow(AgencyPortalSheetState.NONE)
    private val _hasAgencyConfigs = MutableStateFlow(false)

    val uiState = combine(
        // To know if we are signing in up or checking the booker profile
        _message,
        _isInitComplete,
        _isComplete,
        _dialogStatus,
        _hasAccount,
        _hasAgencyConfigs,
        _sheetState,
    ) {
        AgencyPortalUiState(
            message = it[0] as Int?,
            isInitComplete = it[1] as Boolean,
            isComplete = it[2] as Boolean,
            dialogStatus = it[3] as AgencyPortalDialogState,
            has = it[4] as Boolean,
            hasAgencyConfigs = it[5] as Boolean,
            sheetStatus = it[6] as AgencyPortalSheetState
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = AgencyPortalUiState()
    )

    fun onSheetStateChange(new: AgencyPortalSheetState) {
        _sheetState.value = new
    }

    fun onDialogStateChange(new: AgencyPortalDialogState) {
        _dialogStatus.value = new
    }

    fun onMessageChange(resId: Int?) {
        _message.value = resId
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    companion object {
        const val TAG = "AP _VM"
    }

}


@Composable
fun AgencyPortal(
    onNavigateToAgency: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: AgencyPortalVM = hiltViewModel(),
    onNavigateToAccount: () -> Unit,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
) {
    val statusMap = remember {
        mapOf(
            AgencyPortalDialogState.ABOUT_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.EmojiTransportation,
                title = "The Agency Portal",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            AgencyPortalDialogState.ABOUT_CREATE_AGENCY to InfoDialogUiState(
                mainIcon = Icons.Outlined.DomainAdd,
                title = "Become a Partner",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),

            AgencyPortalDialogState.ABOUT_LOGIN_TO_AGENCY to InfoDialogUiState(
                mainIcon = Icons.Filled.Login,
                title = "Join your Team",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            AgencyPortalDialogState.FAILED_GET_YOUR_STATUS to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get critical info",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            )
    }

    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current
    if (uis.isComplete) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
                .apply {
                    val intent = Intent(context, MainAgencyActivity::class.java)
                    createIntent(context, intent)
                }
        ) {it ->
            vm.onCompleteChange(false)
            Log.d("RESULT CODE", it.resultCode.toString())
        }
    }

    val fieldPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    vm.onSheetStateChange(AgencyPortalSheetState.NONE)
                }

                ModalBottomSheetValue.Expanded -> {}
                ModalBottomSheetValue.HalfExpanded -> {}
            }
            true
        }
    )
    LaunchedEffect(uis.sheetStatus) {
        if (uis.sheetStatus != AgencyPortalSheetState.NONE) {
            sheetState.show()
        } else if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.currentValue != ModalBottomSheetValue.Hidden) {
            sheetState.hide()
        }
    }

    when (val status = uis.dialogStatus) {
        AgencyPortalDialogState.NONE -> {}
        AgencyPortalDialogState.ABOUT_CREATE_AGENCY -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyPortalDialogState.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyPortalDialogState.NONE)
            }
        )

        AgencyPortalDialogState.ABOUT_LOGIN_TO_AGENCY -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyPortalDialogState.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyPortalDialogState.NONE)
            }
        )

        AgencyPortalDialogState.ABOUT_MAIN_PAGE ->
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStateChange(AgencyPortalDialogState.NONE)
                }, onPositiveClick = {
                    vm.onDialogStateChange(AgencyPortalDialogState.NONE)
                }
            )

        AgencyPortalDialogState.FAILED_GET_YOUR_STATUS -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyPortalDialogState.NONE)
            },

            onPositiveClick = {
                vm.onDialogStateChange(AgencyPortalDialogState.NONE)
            },
        )

    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (uis.sheetStatus) {
                AgencyPortalSheetState.ACTIONS -> {
                    ActionSheet(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        ActionItem(
                            action = MainAction(R.string.lb_about_page, Icons.Outlined.Info),
                            onClick = {
                                vm.onDialogStateChange(AgencyPortalDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(AgencyPortalSheetState.NONE)
                            },
                        )

                    }
                }

                AgencyPortalSheetState.NONE -> {}
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
                    title = {
                        Text(
                            text = stringResource(id = R.string.lb_agency_portal).titleCase,
                            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { openDrawer() }) {
                            Icon(
                                Icons.Outlined.Menu,
                                contentDescription = null,
                                tint = LocalContentColor.current
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        IconButton(onClick = {
                            vm.onSheetStateChange(AgencyPortalSheetState.ACTIONS)
                        }) {
                            Icon(
                                Icons.Outlined.MoreVert,
                                contentDescription = stringResource(id = R.string.desc_more_options),
                                tint = LocalContentColor.current

                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Login,
                        title = "Join your Team",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = false
//                        isFailure = false
                    ),
                    onClick = { onNavigateToAccount() },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyPortalDialogState.ABOUT_LOGIN_TO_AGENCY)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onRetryClick = {
//                        vm.onAccountCompleteChange(null)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = true,
                            positiveText = "Owner of 3 Agencies",
                            errorText = "Owner of no Agency"
                        )
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = false,
                            positiveText = "No job request",
                            errorText = "Pending job request"
                        )
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = false,
                            positiveText = "Currently employed",
                            errorText = "Currently not employed"
                        )

                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.DomainAdd,
                        title = "Create your Agency",
                        isClickable = true,
                        isHelpable = true
                    ),
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onClick = {
                        vm.onCompleteChange(true)
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyPortalDialogState.ABOUT_CREATE_AGENCY)
                    })
                {
//                    Column(
//                        modifier = Modifier.padding(start = 4.dp)
//                    ) {
//                        DashboardSubItem(
//                            modifier = Modifier.padding(2.dp),
//                            isError = uis.bookerMoMoPhoneCount == 0L,
//                            positiveText = "MoMo Accounts found: ${uis.bookerMoMoPhoneCount}",
//                            errorText = "No MoMo account found"
//                        )
//                    }
                }


            }
        }
    }


}