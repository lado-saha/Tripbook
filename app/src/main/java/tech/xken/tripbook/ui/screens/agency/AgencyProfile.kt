package tech.xken.tripbook.ui.screens.agency

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.EmojiTransportation
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Groups3
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Phone
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
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.ActionSheet
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.DashboardItem
import tech.xken.tripbook.ui.components.DashboardItemUiState
import tech.xken.tripbook.ui.components.DashboardSubItem
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgencyProfile(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: AgencyProfileVM = hiltViewModel(),
    onNavigateToAccount: () -> Unit,
    onNavigateToMoMoAccount: () -> Unit,
    onNavigateToOMAccount: () -> Unit,
    onNavigateToBookerAgencySettings: () -> Unit,
    onNavigateToCreditCardAccount: () -> Unit,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
) {
    val statusMap = remember {
        mapOf(
            AgencyProfileDialogStatus.ABOUT_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.EmojiTransportation,
                title = "Agency Profile",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            AgencyProfileDialogStatus.ABOUT_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Analytics,
                title = "Agency Account",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),

            AgencyProfileDialogStatus.ABOUT_EMAIL_SUPPORT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Email,
                title = "Email Support",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),

            AgencyProfileDialogStatus.ABOUT_PHONE_SUPPORT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Phone,
                title = "Phone Support",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            AgencyProfileDialogStatus.ABOUT_SOCIAL_SUPPORT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Groups3,
                title = "Social Media",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            AgencyProfileDialogStatus.ABOUT_REFUND_POLICY to InfoDialogUiState(
                mainIcon = Icons.Outlined.Payments,
                title = "Refund Policies",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),

            AgencyProfileDialogStatus.FAILED_GET_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogStatus.FAILED_GET_REFUND_POLICY to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get refund policy",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogStatus.FAILED_GET_EMAIL_SUPPORT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get support emails",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogStatus.FAILED_GET_PHONE_SUPPORT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get support phone numbers",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogStatus.FAILED_GET_SOCIAL_SUPPORT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not Social support",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            )
    }
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current
    val fieldPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    vm.onSheetStateChange(AgencyProfileSheetStatus.NONE)
                }

                ModalBottomSheetValue.Expanded -> {}
                ModalBottomSheetValue.HalfExpanded -> {}
            }
            true
        }
    )
    LaunchedEffect(uis.sheetStatus) {
        if (uis.sheetStatus != AgencyProfileSheetStatus.NONE) {
            sheetState.show()
        } else if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.currentValue != ModalBottomSheetValue.Hidden) {
            sheetState.hide()
        }
    }

    when (val status = uis.dialogStatus) {
        AgencyProfileDialogStatus.ABOUT_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            }
        )

        AgencyProfileDialogStatus.ABOUT_PHONE_SUPPORT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            }
        )

        AgencyProfileDialogStatus.ABOUT_EMAIL_SUPPORT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            }
        )

        AgencyProfileDialogStatus.ABOUT_MAIN_PAGE ->
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
                }, onPositiveClick = {
                    vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
                }
            )

        AgencyProfileDialogStatus.NONE -> {}
        AgencyProfileDialogStatus.FAILED_GET_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },

            onPositiveClick = {
//                vm.onAccountCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
        )

        AgencyProfileDialogStatus.FAILED_GET_EMAIL_SUPPORT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
            onPositiveClick = {
//                vm.onOMCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },

            )

        AgencyProfileDialogStatus.FAILED_GET_PHONE_SUPPORT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
            onPositiveClick = {
//                vm.onMoMoCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },

            )

        AgencyProfileDialogStatus.ABOUT_SOCIAL_SUPPORT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
//                navigateUp()
            },
            onPositiveClick = {
//                vm.onAgencySettingsCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
        )

        AgencyProfileDialogStatus.ABOUT_REFUND_POLICY -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
//                navigateUp()
            },
            onPositiveClick = {
//                vm.onAgencySettingsCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
        )

        AgencyProfileDialogStatus.FAILED_GET_SOCIAL_SUPPORT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
//                navigateUp()
            },
            onPositiveClick = {
//                vm.onAgencySettingsCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
        )

        AgencyProfileDialogStatus.FAILED_GET_REFUND_POLICY -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
//                navigateUp()
            },
            onPositiveClick = {
//                vm.onAgencySettingsCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogStatus.NONE)
            },
        )

    }

    BackHandler(true) {
//            if (vm.changeDetected()) {
//                vm.onDialogStatusChange(BookerAccountDialogStatus.LEAVING_WITHOUT_SAVING)
//            } else {
//                vm.onCompleteChange(true)
//            }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            when (uis.sheetStatus) {
                AgencyProfileSheetStatus.ACTIONS -> {
                    ActionSheet(
                        modifier = Modifier.fillMaxWidth()
                    ) {


                        ActionItem(
                            action = MainAction(R.string.lb_about_page, Icons.Outlined.Info),
                            onClick = {
                                vm.onDialogStateChange(AgencyProfileDialogStatus.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(AgencyProfileSheetStatus.NONE)
                            },
                        )

                    }
                }

                AgencyProfileSheetStatus.NONE -> {}
            }
        },
        sheetShape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(10), topStart = CornerSize(10),
        ),
        sheetState = sheetState,
        sheetElevation = 1.dp,
        scrimColor = ModalBottomSheetDefaults.scrimColor.copy(alpha = 0.1f)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.lb_agency_profile).titleCase,
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
                            vm.onSheetStateChange(AgencyProfileSheetStatus.ACTIONS)
                        }) {
                            Icon(
                                Icons.Outlined.MoreVert,
                                contentDescription = stringResource(id = R.string.desc_more_options),
                                tint = LocalContentColor.current

                            )
                        }
                    },
                    elevation = 0.dp
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
                        mainIcon = Icons.Outlined.Analytics,
                        title = "Account Details",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = !uis.isAccountComplete,
//                        isFailure = !uis.isAccountComplete,
                        isDeletable = uis.hasAccount
                    ),
                    onClick = { onNavigateToAccount() },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogStatus.ABOUT_ACCOUNT)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onRetryClick = {
//                        vm.onAccountCompleteChange(null)
                    },
                    onDeleteClick = {
//                        vm.onDialogStateChange(AgencyProfileDialogStatus.DELETE_ACCOUNT_1)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
//                    Divider()
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = !uis.hasAccount,
                            positiveText = "Account found",
                            errorText = "No account found. Please add"
                        )

                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Phone,
                        title = "Phone Support",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = !uis.isPhoneSupportComplete,
//                        isFailure = uis.isMoMoAccountComplete == false,
                    ),
                    onRetryClick = {
//                        vm.onMoMoCompleteChange(null)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onClick = {
////                        onNavigateToMoMoAccount()
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogStatus.ABOUT_PHONE_SUPPORT)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = uis.phoneSupportCount == 0L,
                            positiveText = "Phone numbers found: ${uis.phoneSupportCount}",
                            errorText = "No phone number found"
                        )
                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Email,
                        title = "Email Support",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = !uis.isEmailSupportComplete,
                    ),
                    onRetryClick = {
//                        vm.onMoMoCompleteChange(null)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onClick = {
////                        onNavigateToMoMoAccount()
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogStatus.ABOUT_EMAIL_SUPPORT)
                    }
                )
                {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = uis.emailSupportCount == 0L,
                            positiveText = "Emails found: ${uis.emailSupportCount}",
                            errorText = "No email founds"
                        )
                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Groups3,
                        title = "Social Support",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = !uis.isSocialSupportComplete,
//                        isFailure = uis.isMoMoAccountComplete == false,
                    ),
                    onRetryClick = {
//                        vm.onMoMoCompleteChange(null)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onClick = {
//                        onNavigateToMoMoAccount()
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogStatus.ABOUT_PHONE_SUPPORT)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = !uis.hasSocialSupport,
                            positiveText = "Social accounts found",
                            errorText = "No social account. Please add"
                        )
                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Payments,
                        title = "Refund Policy",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = !uis.isRefundPoliciesComplete,
//                        isFailure = uis.isMoMoAccountComplete == false,
                    ),
                    onRetryClick = {
//                        vm.onMoMoCompleteChange(null)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onClick = {
//                        onNavigateToMoMoAccount()
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogStatus.ABOUT_REFUND_POLICY)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = uis.refundPoliciesCount == 0L,
                            positiveText = "Refund policies found: ${uis.refundPoliciesCount}",
                            errorText = "No refund policy. Please add"
                        )
                    }
                }


            }
        }
    }

}