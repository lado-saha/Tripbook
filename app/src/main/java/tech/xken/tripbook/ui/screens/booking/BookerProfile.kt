package tech.xken.tripbook.ui.screens.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
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
import tech.xken.tripbook.domain.titleCase
import tech.xken.tripbook.ui.components.DashboardItem
import tech.xken.tripbook.ui.components.DashboardItemUiState
import tech.xken.tripbook.ui.components.DashboardSubItem
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.components.NetworkStateIndicator
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.DELETE_ACCOUNT_1

import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.DELETE_ACCOUNT_2
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.DELETE_ACCOUNT_3
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.FAILED_GET_ACCOUNT
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.FAILED_GET_AGENCY_SETTINGS
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.FAILED_GET_MOMO
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.FAILED_GET_OM
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.HELP_ACCOUNT
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.HELP_AGENCY_SETTINGS
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.HELP_CREDIT_CARD
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.HELP_MAIN_PAGE
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.HELP_MOMO
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.HELP_OM
import tech.xken.tripbook.ui.screens.booking.BookerProfileDialogStatus.NONE


@Composable
fun BookerProfile(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    vm: BookerProfileDashboardVM = hiltViewModel(),
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
            HELP_MAIN_PAGE to InfoDialogUiState(
                mainIcon = Icons.Outlined.Person,
                title = "My Booking Profile",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            HELP_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.Badge,
                title = "Me",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            HELP_AGENCY_SETTINGS to InfoDialogUiState(
                mainIcon = Icons.Outlined.Business,
                title = "My Agency Settings",
                text = buildAnnotatedString {
                    append("Text here")//TODO: Add profile page is job seeker help text
                },
                positiveText = "I understand"
            ),
            HELP_OM to InfoDialogUiState(
                mainIcon = Icons.Filled.Payments,
                title = "Orange Money Accounts",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            HELP_MOMO to InfoDialogUiState(
                mainIcon = Icons.Filled.Payments,
                title = "MTN MoMo Accounts",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            HELP_CREDIT_CARD to InfoDialogUiState(
                mainIcon = Icons.Outlined.CreditCard,
                title = "Credit Cards",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "I understand",
            ),
            FAILED_GET_ACCOUNT to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            FAILED_GET_OM to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get OM account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            FAILED_GET_MOMO to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not get MoMo account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),

            FAILED_GET_AGENCY_SETTINGS to InfoDialogUiState(
                mainIcon = Icons.Outlined.ErrorOutline,
                title = "Could not Agency Settings",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
            ),
            DELETE_ACCOUNT_1 to InfoDialogUiState(
                mainIcon = Icons.Outlined.Dangerous,
                title = "Danger zone! Want to delete account?",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "No",
                isNegative = true,
                otherText = "I want"
            ),
            DELETE_ACCOUNT_2 to InfoDialogUiState(
                mainIcon = Icons.Outlined.DeleteForever,
                title = "Warning! Want to delete account?",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "No",
                isNegative = true,
                otherText = "I really want"
            ),
            DELETE_ACCOUNT_3 to InfoDialogUiState(
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

    when (val status = uis.dialogStatus) {
        HELP_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            }
        )

        HELP_MOMO -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            }
        )

        HELP_OM -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            }
        )

        HELP_CREDIT_CARD -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            }
        )

        HELP_AGENCY_SETTINGS -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)

            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            }
        )

        HELP_MAIN_PAGE ->
            InfoDialog(
                uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                }, onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                }
            )

        NONE -> {}
        FAILED_GET_ACCOUNT -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },

            onPositiveClick = {
                vm.onAccountCompleteChange(null)
                vm.onDialogStatusChange(NONE)
            },
        )

        FAILED_GET_OM -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onOMCompleteChange(null)
                vm.onDialogStatusChange(NONE)
            },

            )

        FAILED_GET_MOMO -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onMoMoCompleteChange(null)
                vm.onDialogStatusChange(NONE)
            },

            )

        FAILED_GET_AGENCY_SETTINGS -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
                navigateUp()
            },
            onPositiveClick = {
                vm.onAgencySettingsCompleteChange(null)
                vm.onDialogStatusChange(NONE)
            },
        )

        DELETE_ACCOUNT_1 -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            },
            onOtherClick = {
                vm.onDialogStatusChange(DELETE_ACCOUNT_2)
            },
            mainIconTint = MaterialTheme.colors.error
        )

        DELETE_ACCOUNT_2 -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            },
            onOtherClick = {
                vm.onDialogStatusChange(DELETE_ACCOUNT_3)
            },
            mainIconTint = MaterialTheme.colors.error
        )

        DELETE_ACCOUNT_3 -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            },
            onOtherClick = {
                vm.onDialogStatusChange(NONE)
            }, mainIconTint = MaterialTheme.colors.error
        )
    }

    BackHandler(true) {
//            if (vm.changeDetected()) {
//                vm.onDialogStatusChange(BookerAccountDialogStatus.LEAVING_WITHOUT_SAVING)
//            } else {
//                vm.onCompleteChange(true)
//            }
    }

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
                        vm.onDialogStatusChange(
                            HELP_MAIN_PAGE
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
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//
            NetworkStateIndicator(
                onClick = { /*TODO*/ },
                isOnline = { uis.isOnline },
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )

            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Default.Badge,
                    title = "My Account Details",
                    isClickable = true,
                    isHelpable = true,
                    isLoading = uis.isAccountComplete == null,
                    isFailure = uis.isAccountComplete == false,
                    isDeletable = true
                ),
                onClick = { onNavigateToAccount() },
                onHelpClick = {
                    vm.onDialogStatusChange(HELP_ACCOUNT)
                },
                mainIconColor = MaterialTheme.colors.onSurface,
                onRetryClick = {
                    vm.onAccountCompleteChange(null)
                },
                onDeleteClick = {
                    vm.onDialogStatusChange(DELETE_ACCOUNT_1)
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
                        negativeText = "No account found. Please add"
                    )
                    DashboardSubItem(
                        modifier = Modifier.padding(2.dp),
                        isError = !uis.hasAccountPhoto,
                        positiveText = "Account photo found",
                        negativeText = "No account photo found. Please add"
                    )
                }
            }

            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Filled.Payments,
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
                    vm.onDialogStatusChange(HELP_MOMO)
                })
            {
                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    DashboardSubItem(
                        modifier = Modifier.padding(2.dp),
                        isError = uis.bookerMoMoPhoneCount == 0L,
                        positiveText = "MoMo Accounts found: ${uis.bookerMoMoPhoneCount}",
                        negativeText = "No MoMo account found"
                    )
                }
            }

            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Filled.Payments,
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
                    vm.onDialogStatusChange(HELP_OM)
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
                        isError = uis.bookerOMPhoneCount == 0L,
                        positiveText = "OM Accounts found: ${uis.bookerOMPhoneCount}",
                        negativeText = "No OM account found"
                    )
                }
            }


            DashboardItem(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                uis = DashboardItemUiState(
                    mainIcon = Icons.Default.Business,
                    title = "My Agency Settings",
                    isClickable = true,
                    isHelpable = true,
                    isLoading = uis.isAgencySettingsComplete == null,
                    isFailure = uis.isAgencySettingsComplete == false,
                ),
                onClick = {

                },
                onHelpClick = {
                    vm.onDialogStatusChange(HELP_AGENCY_SETTINGS)
                },
                mainIconColor = MaterialTheme.colors.onSurface,
                onRetryClick = {
                    vm.onAgencySettingsCompleteChange(null)
                }

            ) {

            }

        }
    }


}