//package tech.xken.tripbook.ui.components
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Business
//import androidx.compose.material.icons.outlined.Menu
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import tech.xken.tripbook.R
//import tech.xken.tripbook.domain.caps
//import tech.xken.tripbook.domain.titleCase
//import tech.xken.tripbook.ui.navigation.AgencyDestinations
//
//@Composable
//fun AppModalDrawer(
//    drawerState: DrawerState,
//    currentRoute: String,
//    navigationActions: AppNavigationActions,
//    coroutineScope: CoroutineScope = rememberCoroutineScope(),
//    content: @Composable () -> Unit
//) {
//    ModalDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            AgencyDrawer(
//                currentRoute = currentRoute,
//                navigateToTasks = { navigationActions.navigateToTasks() },
//                navigateToStatistics = { navigationActions.navigateToStatistics() },
//                closeDrawer = { coroutineScope.launch { drawerState.close() } }
//            )
//        }
//    ) {
//        content()
//    }
//}
//
//@Composable
//private fun AgencyDrawer(
//    currentRoute: String,
//    closeDrawer: () -> Unit,
//    modifier: Modifier = Modifier,
//    navigateToJobDetails: () -> Unit,
//    navigateToStationJobs: () -> Unit,
//    navigateToStationPersonnel: () -> Unit,
//    navigateToStationLocation: () -> Unit,
//    navigateToStationDashboard: () -> Unit,
//    navigateToStationProfile: () -> Unit,
//
//) {
//    Column(modifier = modifier.fillMaxSize()) {
//        DrawerHeader()
//        DrawerButton(
//           icon = Icons.Outlined.Menu,
//            label = stringResource(R.string.lb_stations).caps,
//            isSelected = currentRoute == AgencyDestinations.AGENCY,
//            action = {
//                navigateToTasks()
//                closeDrawer()
//            }
//        )
//        DrawerButton(
//            painter = painterResource(id = R.drawable.ic_statistics),
//            label = stringResource(id = R.string.statistics_title),
//            isSelected = currentRoute == TodoDestinations.STATISTICS_ROUTE,
//            action = {
//                navigateToStatistics()
//                closeDrawer()
//            }
//        )
//    }
//}
//
//@Composable
//private fun DrawerHeader(
//    modifier: Modifier = Modifier
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = modifier
//            .fillMaxWidth()
//            .background(primaryDarkColor)
//            .height(192.dp)
//            .padding(16.dp)
//    ) {
//        Icon(
//            imageVector = Icons.Default.Business,
//            contentDescription = null,
//            modifier = Modifier.width(100.dp)
//        )
//        Text(
//            text = stringResource(id = R.string.lb_my_agency).titleCase,
//            color = MaterialTheme.colors.surface
//        )
//    }
//}
//
//@Composable
//private fun DrawerButton(
//    icon: ImageVector,
//    label: String,
//    isSelected: Boolean,
//    action: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val tintColor = if (isSelected) {
//        MaterialTheme.colors.secondary
//    } else {
//        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
//    }
//
//    TextButton(
//        onClick = action,
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.Start,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Icon(
//                imageVector = icon,
//                contentDescription = null, // decorative
//                tint = tintColor
//            )
//            Spacer(Modifier.width(16.dp))
//            Text(
//                text = label,
//                style = MaterialTheme.typography.body2,
//                color = tintColor
//            )
//        }
//    }
//}
