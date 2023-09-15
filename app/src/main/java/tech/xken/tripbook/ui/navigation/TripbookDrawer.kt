package tech.xken.tripbook.ui.navigation

import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.EmojiTransportation
import androidx.compose.material.icons.outlined.HelpCenter
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.ui.navigation.BookingDestinations.AGENCY_PORTAL_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_AUTHENTICATION_ROUTE

@Composable
fun BookingModalDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    bookerNavActions: BookingNavActions,
//    univNavActions: UnivNavActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isSignedIn: () -> Boolean,
    content: @Composable () -> Unit
) {
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            BookingDrawerContent(
                currentRoute = currentRoute,
                navigateToSignIn = {
                    coroutineScope.launch { bookerNavActions.navigateToSignIn(false) }
                },
                navigateToProfile = {
                    coroutineScope.launch {
                        bookerNavActions.navigateToProfile()
                    }
                },
                navigateToTripSearch = {
                    coroutineScope.launch {
                        bookerNavActions.navigateToTripSearch()
                    }
                },
                navigateToAgencyPortal = {
                    coroutineScope.launch {
                        bookerNavActions.navigateToAgencyPortal()
                    }
                },
                closeDrawer = { coroutineScope.launch { drawerState.close() } },
                isSignedIn = isSignedIn,
                signOut = { bookerNavActions.navigateToSignIn(true) }
            )
        }, drawerElevation = 4.dp
    ) {
        content()
    }
}

@Composable
private fun BookingDrawerContent(
    currentRoute: String,
    navigateToProfile: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToTripSearch: () -> Unit,
    navigateToAgencyPortal: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    isSignedIn: () -> Boolean,
    signOut: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) Color.Unspecified else MaterialTheme.colors.primary)
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(id = R.string.app_name).caps,
                style = MaterialTheme.typography.h2.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                    color = Color.Unspecified
                ),
                modifier = Modifier.padding(4.dp),
            )
            Icon(
                imageVector = Icons.Outlined.Luggage,
                contentDescription = null,
            )
        }

        Divider(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
        )

        ActionItem(
            action = MainAction(
                R.string.lb_home, Icons.Outlined.Search
            ),
            isSelected = currentRoute == BookingScreens.BOOKER_TRIP_SEARCH,
            onClick = {
                closeDrawer()
                navigateToTripSearch()
            },
            isBold = true
        )

        if (!isSignedIn())
            ActionItem(
                action = MainAction(
                    R.string.lb_sign_in_or_up, Icons.Outlined.Person,
                ),
                isSelected = currentRoute == BOOKER_AUTHENTICATION_ROUTE,
                onClick = {
                    closeDrawer()
                    navigateToSignIn()
                },
                isBold = true
            )

        //Booker profile
        if (isSignedIn()) {
            ActionItem(
                action = MainAction(
                    R.string.lb_my_books, Icons.Outlined.Book,
                ),
                isSelected = false,
                onClick = {
                    closeDrawer()
                },
                isBold = true
            )

            ActionItem(
                action = MainAction(
                    R.string.lb_my_profile, Icons.Outlined.AccountCircle
                ),
                isSelected = currentRoute == BookingScreens.BOOKER_PROFILE,
                onClick = {
                    navigateToProfile()
                    closeDrawer()
                },
                isBold = true
            )
            ActionItem(
                action = MainAction(R.string.lb_settings, Icons.Outlined.Settings),
                isSelected = false,
                onClick = {
//                    navigateToProfile()
                    closeDrawer()
                },
                isBold = true
            )
            ActionItem(
                action = MainAction(R.string.lb_help_center, Icons.Outlined.HelpCenter),
                isSelected = false,
                onClick = {
//                    navigateToProfile()
                    closeDrawer()
                },
                isBold = true
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            )

            ActionItem(
                action = MainAction(R.string.lb_sign_out, Icons.Outlined.Logout),
                isSelected = false,
                onClick = {
                    signOut()
                    closeDrawer()
                },
                isBold = true
            )

            Divider(
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .fillMaxWidth()
            )

            ActionItem(
                action = MainAction(R.string.lb_agency_portal, Icons.Outlined.EmojiTransportation),
                isSelected = currentRoute == AGENCY_PORTAL_ROUTE,
                onClick = {
                    navigateToAgencyPortal()
                    closeDrawer()
                },
                isBold = true
            )
        }

    }
}

@Composable
fun AgencyModalDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    agencyNavActions: AgencyNavActions,
//    univNavActions: UnivNavActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isSignedIn: () -> Boolean,
    content: @Composable () -> Unit
) {
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            AgencyDrawerContent(
                currentRoute = currentRoute,
                navigateBackToAgencyPortal = {

                },
                navigateToAgencyProfile = {
                    coroutineScope.launch {
                        agencyNavActions.navigateToAgencyProfile()
                    }
                },
                closeDrawer = { coroutineScope.launch { drawerState.close() } },
                isSignedIn = isSignedIn,
                navigateToAgencyHelpCenter = {
                    coroutineScope.launch {
                        agencyNavActions.navigateHelpCenter()
                    }
                }
            )
        }, drawerElevation = 4.dp
    ) {
        content()
    }
}

@Composable
private fun AgencyDrawerContent(
    currentRoute: String,
    navigateToAgencyProfile: () -> Unit,
    navigateBackToAgencyPortal: () -> Unit,
    navigateToAgencyHelpCenter: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    isSignedIn: () -> Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) Color.Unspecified else MaterialTheme.colors.primary)
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(id = R.string.app_name).caps,
                style = MaterialTheme.typography.h2.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                    color = Color.Unspecified
                ),
                modifier = Modifier.padding(4.dp),
            )
            Icon(
                imageVector = Icons.Outlined.EmojiTransportation,
                contentDescription = null,
            )
        }

        Divider(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
        )

        ActionItem(
            action = MainAction(
                R.string.lb_agency_profile, Icons.Outlined.AppRegistration
            ),
            isSelected = currentRoute == AgencyDestinations.AGENCY_PROFILE_ROUTE,
            onClick = {
                closeDrawer()
                navigateToAgencyProfile()
            },
            isBold = true
        )

        ActionItem(
            action = MainAction(
                R.string.lb_help_center, Icons.Outlined.HelpCenter,
            ),
            isSelected = currentRoute == AgencyDestinations.AGENCY_HELP_CENTER_ROUTE,
            onClick = {
                closeDrawer()
                navigateToAgencyHelpCenter()
            },
            isBold = true
        )

        Divider(
            modifier = Modifier
                .padding(bottom = 18.dp)
                .fillMaxWidth()
        )

        ActionItem(
            action = MainAction(
                R.string.lb_agency_portal, Icons.Outlined.Logout,
            ),
            isSelected = currentRoute == BookingDestinations.AGENCY_PORTAL_ROUTE,
            onClick = {
                closeDrawer()
                navigateBackToAgencyPortal()
            },
            isBold = true
        )


    }
}

@Composable
private fun DrawerHeader(
    phoneNumber: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
//            .height(dimensionResource(id = R.dimen.header_height))
            .height(20.dp)
            .padding(dimensionResource(id = R.dimen.header_padding))
    ) {
//        Icon(
//            imageVector = Icons.Outlined.AccountCircle,
//            contentDescription = stringResource(id = R.string.desc_nav_account),
//            modifier = Modifier
//                .size(100.dp)
//                .padding(2.dp) /*.width(dimensionResource(id = R.dimen.header_image_width))*/,
//            tint = MaterialTheme.colors.onPrimary
//        )
//        Text(
//            text = phoneNumber,
//            color = MaterialTheme.colors.onPrimary,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(2.dp),
//            textAlign = TextAlign.Center
//        )
    }
}

@Composable
private fun DrawerButton(
    imageVector: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colors.secondary
    } else {
        MaterialTheme.colors.onSurface
    }

    TextButton(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin)),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null, // decorative
                tint = tintColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(18.dp))
            Text(
                text = label.caps,
                style = MaterialTheme.typography.button.run {
                    if (isSelected) this.copy(fontWeight = FontWeight.Bold)
                    else this
                },
                color = tintColor
            )
        }
    }
}
