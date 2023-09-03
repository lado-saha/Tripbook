package tech.xken.tripbook.ui.navigation

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.HelpCenter
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase

@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    bookerNavActions: BookingNavActions,
    agencyNavActions: AgencyNavActions,
//    univNavActions: UnivNavActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isSignedIn: () -> Boolean,
    content: @Composable () -> Unit
) {
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
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
private fun AppDrawer(
    currentRoute: String,
    navigateToProfile: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToTripSearch: () -> Unit,
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
                .background(MaterialTheme.colors.primary)
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(id = R.string.app_name).caps,
                style = MaterialTheme.typography.h2.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,/* color = MaterialTheme.colors.primary*/
                    color = MaterialTheme.colors.onPrimary
                ),
                modifier = Modifier.padding(4.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Luggage,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Divider(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
        )

        DrawerButton(
            imageVector = Icons.Outlined.Search,
            label = stringResource(id = R.string.lb_home),
            isSelected = currentRoute == BookingScreens.BOOKER_TRIP_SEARCH,
            action = {
                closeDrawer()
                navigateToTripSearch()
            }
        )

        if (!isSignedIn())
            DrawerButton(
                imageVector = Icons.Outlined.Login,
                label = stringResource(id = R.string.lb_sign_in),
                isSelected = currentRoute == BookingScreens.BOOKER_SIGN_IN,
                action = {
                    closeDrawer()
                    navigateToSignIn()
                }
            )

        //Booker profile
        if (isSignedIn()) {
            DrawerButton(
                imageVector = Icons.Outlined.Book,
                label = stringResource(id = R.string.lb_my_books).titleCase,
                isSelected = false,
                action = {
//                    navigateToTripSearch()
                    closeDrawer()
                }
            )
            DrawerButton(
                imageVector = Icons.Outlined.AccountCircle,
                label = stringResource(id = R.string.lb_my_profile).titleCase,
                isSelected = currentRoute == BookingScreens.BOOKER_PROFILE,
                action = {
                    navigateToProfile()
                    closeDrawer()
                }
            )
            DrawerButton(
                imageVector = Icons.Outlined.Settings,
                label = stringResource(id = R.string.lb_settings).titleCase,
                isSelected = false,
                action = {
//                    navigateToProfile()
                    closeDrawer()
                }
            )
            DrawerButton(
                imageVector = Icons.Outlined.HelpCenter,
                label = stringResource(id = R.string.lb_help_center).titleCase,
                isSelected = false,
                action = {
//                    navigateToProfile()
                    closeDrawer()
                }
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            )
            DrawerButton(
                imageVector = Icons.Outlined.Business,
                label = stringResource(id = R.string.lb_my_agency).titleCase,
                isSelected = false,
                action = {
//                    navigateToProfile()
                    closeDrawer()
                }
            )
            Divider(
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .fillMaxWidth()
            )

            DrawerButton(
                imageVector = Icons.Outlined.Logout,
                label = stringResource(id = R.string.lb_sign_out).titleCase,
                isSelected = false,
                action = {
                    signOut()
                    closeDrawer()
                }
            )
        }

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
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colors.secondary
    } else {
        MaterialTheme.colors.onSurface
    }

    TextButton(
        onClick = { action() },
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
