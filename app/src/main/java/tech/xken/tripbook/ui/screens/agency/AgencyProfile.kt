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
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
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
            AgencyProfileDialogState.ABOUT_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.Person,
                title = "My Booking Profile",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            AgencyProfileDialogState.ABOUT_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Badge,
                title = "Me",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
//            ABOUT_AGENCY_SETTINGS to InfoDialogUiState(
//                mainIcon = Icons.Outlined.Business,
//                title = "My Agency Settings",
//                text = buildAnnotatedString {
//                    append("Text here")//TODO: Add profile page is job seeker help text
//                },
//                positiveText = "I understand"
//            ),
            AgencyProfileDialogState.ABOUT_OM to InfoDialogUiState(
                mainIcon = Icons.Filled.Payments,
                title = "Orange Money Accounts",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            AgencyProfileDialogState.ABOUT_MOMO to InfoDialogUiState(
                mainIcon = Icons.Filled.Payments,
                title = "MTN MoMo Accounts",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
//            ABOUT_CREDIT_CARD to InfoDialogUiState(
//                mainIcon = Icons.Outlined.CreditCard,
//                title = "Credit Cards",
//                text = buildAnnotatedString {
//                    append("Text here")
//                },
//                positiveText = "I understand",
//            ),
            AgencyProfileDialogState.FAILED_GET_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogState.FAILED_GET_OM to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get OM account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogState.FAILED_GET_MOMO to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get MoMo account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            AgencyProfileDialogState.FAILED_GET_AGENCY_SETTINGS to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not Agency Settings",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),
            AgencyProfileDialogState.DELETE_ACCOUNT_1 to InfoDialogUiState(
                mainIcon = Icons.Outlined.Dangerous,
                title = "Danger zone! Want to delete account?",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "No",
                isNegative = true,
                otherText = "I want"
            ),
            AgencyProfileDialogState.DELETE_ACCOUNT_2 to InfoDialogUiState(
                mainIcon = Icons.Outlined.DeleteForever,
                title = "Warning! Want to delete account?",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "No",
                isNegative = true,
                otherText = "I really want"
            ),
            AgencyProfileDialogState.DELETE_ACCOUNT_3 to InfoDialogUiState(
                mainIcon = Icons.Outlined.DeleteForever,
                title = "Final Warning! Do you want to delete your Account?",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Nooooo!",
                isNegative = true,
                otherText = "I really really want!"
            )
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
                    vm.onSheetStateChange(AgencyProfileSheetState.NONE)
                }

                ModalBottomSheetValue.Expanded -> {}
                ModalBottomSheetValue.HalfExpanded -> {}
            }
            true
        }
    )
    LaunchedEffect(uis.sheetStatus) {
        if (uis.sheetStatus != AgencyProfileSheetState.NONE) {
            sheetState.show()
        } else if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.currentValue != ModalBottomSheetValue.Hidden) {
            sheetState.hide()
        }
    }

    when (val status = uis.dialogStatus) {
        AgencyProfileDialogState.ABOUT_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }
        )

        AgencyProfileDialogState.ABOUT_MOMO -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }
        )

        AgencyProfileDialogState.ABOUT_OM -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }, onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }
        )

//        ABOUT_CREDIT_CARD -> InfoDialog(
//            uis = statusMap[status]!!,
//            onCloseClick = {
//                vm.onDialogStateChange(NONE)
//            }, onPositiveClick = {
//                vm.onDialogStateChange(NONE)
//            }
//        )
//
//        ABOUT_AGENCY_SETTINGS -> InfoDialog(
//            uis = statusMap[status]!!,
//            onCloseClick = {
//                vm.onDialogStateChange(NONE)
//
//            }, onPositiveClick = {
//                vm.onDialogStateChange(NONE)
//            }
//        )

        AgencyProfileDialogState.ABOUT_MAIN_PAGE ->
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStateChange(AgencyProfileDialogState.NONE)
                }, onPositiveClick = {
                    vm.onDialogStateChange(AgencyProfileDialogState.NONE)
                }
            )

        AgencyProfileDialogState.NONE -> {}
        AgencyProfileDialogState.FAILED_GET_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },

            onPositiveClick = {
                vm.onAccountCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
        )

        AgencyProfileDialogState.FAILED_GET_OM -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onPositiveClick = {
                vm.onOMCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },

            )

        AgencyProfileDialogState.FAILED_GET_MOMO -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onPositiveClick = {
                vm.onMoMoCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },

            )

        AgencyProfileDialogState.FAILED_GET_AGENCY_SETTINGS -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange( AgencyProfileDialogState.NONE)
                navigateUp()
            },
            onPositiveClick = {
                vm.onAgencySettingsCompleteChange(null)
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
        )

        AgencyProfileDialogState.DELETE_ACCOUNT_1 -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onOtherClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.DELETE_ACCOUNT_2)
            },
            mainIconTint = MaterialTheme.colors.error
        )

        AgencyProfileDialogState.DELETE_ACCOUNT_2 -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onOtherClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.DELETE_ACCOUNT_3)
            },
            mainIconTint = MaterialTheme.colors.error
        )

        AgencyProfileDialogState.DELETE_ACCOUNT_3 -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onPositiveClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            },
            onOtherClick = {
                vm.onDialogStateChange(AgencyProfileDialogState.NONE)
            }, mainIconTint = MaterialTheme.colors.error
        )

        else -> {}
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
                AgencyProfileSheetState.ACTIONS -> {
                    ActionSheet(
                        modifier = Modifier.fillMaxWidth()
                    ) {


                        ActionItem(
                            action = MainAction(R.string.lb_about_page, Icons.Outlined.Info),
                            onClick = {
                                vm.onDialogStateChange(AgencyProfileDialogState.ABOUT_MAIN_PAGE)
                                vm.onSheetStateChange(AgencyProfileSheetState.NONE)
                            },
                        )

                    }
                }

                AgencyProfileSheetState.NONE -> {}
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
                            text = stringResource(id = R.string.lb_my_profile).titleCase,
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
                            vm.onSheetStateChange(AgencyProfileSheetState.ACTIONS)
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
                        mainIcon = Icons.Outlined.Badge,
                        title = "My Account Details",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = uis.isAccountComplete == null,
                        isFailure = uis.isAccountComplete == false,
                        isDeletable = uis.hasAccount
                    ),
                    onClick = { onNavigateToAccount() },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogState.ABOUT_ACCOUNT)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onRetryClick = {
                        vm.onAccountCompleteChange(null)
                    },
                    onDeleteClick = {
                        vm.onDialogStateChange(AgencyProfileDialogState.DELETE_ACCOUNT_1)
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
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = !uis.hasAccountPhoto,
                            positiveText = "Account photo found",
                            errorText = "No account photo found. Please add"
                        )
                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Payments,
                        title = "MTN MoMo Accounts",
                        isClickable = true,
                        isHelpable = true,

                        isLoading = uis.isMoMoAccountComplete == null,
                        isFailure = uis.isMoMoAccountComplete == false,
                    ),
                    onRetryClick = {
                        vm.onMoMoCompleteChange(null)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onClick = {
                        onNavigateToMoMoAccount()
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogState.ABOUT_MOMO)
                    })
                {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = uis.emailSupportCount == 0L,
                            positiveText = "MoMo Accounts found: ${uis.emailSupportCount}",
                            errorText = "No MoMo account found"
                        )
                    }
                }

                DashboardItem(
                    modifier = Modifier
                        .padding(fieldPadding)
                        .fillMaxWidth(),
                    uis = DashboardItemUiState(
                        mainIcon = Icons.Outlined.Payments,
                        title = "Orange Money Accounts",
                        isClickable = true,
                        isHelpable = true,
                        isLoading = uis.isOMAccountComplete == null,
                        isFailure = uis.isOMAccountComplete == false,
                    ),
                    onClick = {
                        onNavigateToOMAccount()
                    },
                    onHelpClick = {
                        vm.onDialogStateChange(AgencyProfileDialogState.ABOUT_OM)
                    },
                    mainIconColor = MaterialTheme.colors.onSurface,
                    onRetryClick = {
                        vm.onOMCompleteChange(null)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        DashboardSubItem(
                            modifier = Modifier.padding(2.dp),
                            isError = uis.phoneSupportCount == 0L,
                            positiveText = "OM Accounts found: ${uis.phoneSupportCount}",
                            errorText = "No OM account found"
                        )
                    }
                }


//                DashboardItem(
//                    modifier = Modifier
//                        .padding(fieldPadding)
//                        .fillMaxWidth(),
//                    uis = DashboardItemUiState(
//                        mainIcon = Icons.Outlined.Business,
//                        title = "My Agency Settings",
//                        isClickable = true,
//                        isHelpable = true,
//                        isLoading = uis.isAgencySettingsComplete == null,
//                        isFailure = uis.isAgencySettingsComplete == false,
//                    ),
//                    onClick = {
//
//                    },
//                    onHelpClick = {
//                        vm.onDialogStateChange(ABOUT_AGENCY_SETTINGS)
//                    },
//                    mainIconColor = MaterialTheme.colors.onSurface,
//                    onRetryClick = {
//                        vm.onAgencySettingsCompleteChange(null)
//                    }
//
//                ) {
//
//                }

            }
        }
    }

}