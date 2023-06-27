@file:OptIn(ExperimentalMaterialApi::class)

package tech.xken.tripbook.ui.navigation


import android.util.Log
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import tech.xken.tripbook.ui.navigation.AgencyScreens.BASE_AGENCY
import tech.xken.tripbook.ui.navigation.BookingScreens.BASE_BOOKING
import tech.xken.tripbook.ui.navigation.UnivScreens.BASE_UNIV
import tech.xken.tripbook.ui.navigation.UnivScreens.UNIVERSE_SEARCH
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIVERSE_SEARCH_RETURN_TOWNS_ONLY
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_CALLER_SCREEN
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_FIELDS
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_HAS_PRESELECTED_FIELDS
import tech.xken.tripbook.ui.screens.agency.station.*
import tech.xken.tripbook.ui.screens.booking.BookerSignIn
import tech.xken.tripbook.ui.screens.booking.BookerSignUpOrDetails
import tech.xken.tripbook.ui.screens.universe.*

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AgencyDestinations.AGENCY_STATION_DASHBOARD_ROUTE,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    agencyNavActions: AgencyNavActions = remember(navController) {
        AgencyNavActions(navController)
    },
    bookingNavActions: BookingNavActions = remember(navController){
        BookingNavActions(navController)
    },
    univNavActions: UnivNavActions = remember(navController){
        UnivNavActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
//        Agency navigations
        navigation(
            startDestination = AgencyDestinations.AGENCY_STATION_DASHBOARD_ROUTE,
            route = BASE_AGENCY
        ) {
            composable(
                AgencyDestinations.AGENCY_STATION_JOBS_ROUTE,
                arguments = listOf(navArgument(AgencyArgs.AGENCY_STATION_ID) {
                    type = NavType.StringType
                })
            ) {
                StationJobs(onNavigateBack = { navController.popBackStack() }, onStationJobClick = {
//                agencyNavActions.navigateToStationJobDetails(it)
                })
            }
            composable(
                AgencyDestinations.AGENCY_STATION_JOB_DETAILS_ROUTE,
                arguments = listOf(navArgument(AgencyArgs.AGENCY_STATION_JOB_ID) {
                    type = NavType.StringType
                })
            ) {
                StationJobDetailsBottomSheet(onNavigateBack = { navController.popBackStack() })
            }
            composable(route = AgencyDestinations.AGENCY_STATION_DASHBOARD_ROUTE,
                arguments = listOf(
                    navArgument(AgencyArgs.AGENCY_STATION_IS_EDIT_MODE) { type = NavType.BoolType },
                    navArgument(AgencyArgs.AGENCY_STATION_ID) { type = NavType.StringType }
                )) {
                StationDashboard(
                    vm = hiltViewModel(),
                    navigateBack = { navController.popBackStack() },
                    onProfileClick = { id, isEditMode ->
//                        navActions.navigateToStationProfile(isEditMode, id)
                    },
                    onLocationClick = {
//                        navActions.navigateToStationLocation(it)
                    },
                    onStationJobsClick = {
//                        navActions.navigateToStationJobs(it)
                    })
            }
            composable(route = AgencyDestinations.AGENCY_STATION_PERSONNEL_ROUTE,
                arguments = listOf(
                    navArgument(AgencyArgs.AGENCY_ID) { type = NavType.StringType },
                    navArgument(AgencyArgs.AGENCY_STATION_ID) { type = NavType.StringType }
                )) { entry ->
                EditStationPersonnel(
                    navigateBack = { navController.popBackStack() },
                    onComplete = { navController.popBackStack() }
                )
            }
            composable(route = AgencyDestinations.AGENCY_STATION_PROFILE_ROUTE,
                arguments = listOf(
                    navArgument(AgencyArgs.AGENCY_STATION_IS_EDIT_MODE) { type = NavType.BoolType },
                    navArgument(AgencyArgs.AGENCY_STATION_ID) { type = NavType.StringType }
                )) { entry ->
                EditStationProfile(
                    vm = hiltViewModel(),
                    navigateBack = { navController.popBackStack() },
                    onComplete = { navController.popBackStack() }
                )
            }
            composable(
                route = AgencyDestinations.AGENCY_STATION_LOCATION_ROUTE,
                arguments = listOf(
                    navArgument(AgencyArgs.AGENCY_STATION_ID) { type = NavType.StringType },
                    navArgument(AgencyArgs.AGENCY_STATION_NAME) { type = NavType.StringType },
                    navArgument(AgencyArgs.AGENCY_STATION_LON) { type = NavType.FloatType },
                    navArgument(AgencyArgs.AGENCY_STATION_LAT) { type = NavType.FloatType },
                )
            ) { entry: NavBackStackEntry ->
                EditStationLocation(
                    onBackClick = { navController.popBackStack() },
                    onComplete = { navController.popBackStack() },
                    onAddEditLocationClick = {
                        univNavActions.navigateToUnivSearch(
                            it,
                            AgencyScreens.AGENCY_STATION_LOCATION,
                            true
                        )
                    }
                )
            }

        }

        // Booking
        navigation(startDestination = "Booking", route = BASE_BOOKING) {
            composable(BookingScreens.BOOKER_SIGN_IN) {
                BookerSignIn(
                    navigateBack = null,
                    /*if (navController.backQueue.size == 0) null else {
                    { navController.popBackStack() }
                }*/
                    navigateToSignUp = { bookingNavActions.navigateToSignUp() },
                    onSignInComplete = { bookingNavActions.navigateToBookerDetails() }
                )
            }
            composable(BookingScreens.BOOKER_SIGN_UP) {
                BookerSignUpOrDetails(
                    navigateBack = { navController.popBackStack() },
                    navigateToSignIn = { bookingNavActions.navigateToSignIn() },
                    onSignUpComplete = { bookingNavActions.navigateToSignIn() },
                    onBookerDetailsEditComplete = null,
                )
            }
            composable(BookingScreens.BOOKER_DETAILS) {
                BookerSignUpOrDetails(
                    navigateBack = { navController.popBackStack() },
                    navigateToSignIn = null,
                    onSignUpComplete = null,
                    onBookerDetailsEditComplete = { navController.popBackStack() },
                )
            }
        }

        //Universe
        navigation("", route = BASE_UNIV) {
            composable(UnivScreens.UNIVERSE_VISUALIZER) {
                UniverseVisualizer(viewModel = hiltViewModel<UniverseVisualizerVM>().apply {
                    savedStateHandle = it.savedStateHandle
                }) {
                    navController.navigate(route = UNIVERSE_SEARCH)
                }
            }
            composable(UnivScreens.UNIVERSE_EDITOR) {
                UniverseEditor()
            }
            composable(
                route = UnivDestinations.UNIV_SEARCH_ROUTE, arguments = listOf(
                    navArgument(UNIV_SEARCH_HAS_PRESELECTED_FIELDS) { type = NavType.BoolType },
                    navArgument(UNIVERSE_SEARCH_RETURN_TOWNS_ONLY) { type = NavType.BoolType },
                    navArgument(UNIV_SEARCH_FIELDS) { type = NavType.StringType },
                    navArgument(UNIV_SEARCH_CALLER_SCREEN) { type = NavType.StringType },
                )
            ) {
                UniverseSearch() { results ->
                    navController.popBackStack()
                    Log.d("Univ was Selected: ", results.toString())
                }
            }
        }
    }
}
