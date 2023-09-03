@file:OptIn(ExperimentalMaterialApi::class)

package tech.xken.tripbook.ui.navigation


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.ui.navigation.AgencyScreens.BASE_AGENCY
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_MOMO_ACCOUNTS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_OM_ACCOUNTS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingScreens.BASE_BOOKER
import tech.xken.tripbook.ui.screens.agency.station.*
import tech.xken.tripbook.ui.screens.booking.BookerAccount
import tech.xken.tripbook.ui.screens.booking.BookerMoMoAccountDetails
import tech.xken.tripbook.ui.screens.booking.BookerMoMoAccounts
import tech.xken.tripbook.ui.screens.booking.BookerOMAccountDetails
import tech.xken.tripbook.ui.screens.booking.BookerOMAccounts
import tech.xken.tripbook.ui.screens.booking.BookerProfile
import tech.xken.tripbook.ui.screens.booking.BookerSignIn
import tech.xken.tripbook.ui.screens.booking.TripSearch

//import tech.xken.tripbook.ui.screens.universe.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = BASE_BOOKER,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    agencyNavActions: AgencyNavActions = remember(navController) {
        AgencyNavActions(navController)
    },
    authRepo: AuthRepo,
    bookingNavActions: BookingNavActions = remember(navController) {
        BookingNavActions(navController, authRepo)
    },
    /* univNavActions: UnivNavActions = remember(navController){
         UnivNavActions(navController)
     }*/
) {
    fun defaultNavigateUp() {
        try {
            navController.navigateUp()
        } catch (e: Exception) {
            bookingNavActions.navigateToTripSearch()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {


        navigation(
            startDestination = BookingDestinations.BOOKER_TRIP_SEARCH_ROUTE,
            route = BASE_BOOKER
//            enterTransition = {
//                // Check to see if the previous screen is in the login graph
//                if (initialState.destination.hierarchy.any { it.route == BASE_BOOKER }) {
//                    slideInHorizontally(initialOffsetX = { 1000 })
//                } else null // use the defaults
//            },
//            exitTransition = {
//                // Check to see if the new screen is in the login graph
//                if (targetState.destination.hierarchy.any { it.route == BASE_BOOKER }) {
//                    slideOutHorizontally(targetOffsetX = { -1000 })
//                } else
//                    null // use the defaults
//            },
//            popEnterTransition = {
//                // Check to see if the previous screen is in the login graph
//                if (initialState.destination.hierarchy.any { it.route == BASE_BOOKER }) {
//                    // Note how we animate from the opposite direction on a pop
//                    slideInHorizontally(initialOffsetX = { -1000 })
//                } else
//                    null // use the defaults
//            },
//            popExitTransition = {
//                // Check to see if the new screen is in the login graph
//                if (targetState.destination.hierarchy.any { it.route == BASE_BOOKER }) {
//                    // Note how we animate from the opposite direction on a pop
//                    slideOutHorizontally(targetOffsetX = { 1000 })
//                } else
//                    null // use the defaults
//            }
        ) {
//            this
            composable(
                BookingDestinations.BOOKER_TRIP_SEARCH_ROUTE
            ) {
                AppModalDrawer(
                    drawerState = drawerState,
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: "",
                    bookerNavActions = bookingNavActions,
                    agencyNavActions = agencyNavActions,
                    isSignedIn = { authRepo.isSignedIn },
                ) {
                    TripSearch(
                        onComplete = {

                        },
                        onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
            }

            composable(
                BookingDestinations.BOOKER_SIGN_IN_ROUTE,
                arguments = listOf(navArgument(BookingNavArgs.SHOULD_SIGN_OUT) {
                    type = NavType.BoolType
                    defaultValue = false
                }),
            ) {
                AppModalDrawer(
                    drawerState = drawerState,
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: "",
                    bookerNavActions = bookingNavActions,
                    agencyNavActions = agencyNavActions,
                    isSignedIn = { authRepo.isSignedIn },
                ) {
                    BookerSignIn(
                        navigateBack = null,
                        navigateToProfile = {
                            navController.popBackStack()
                            bookingNavActions.navigateToProfile()
                        },
                        onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        doOnSignOutComplete = {
                            navController.popBackStack()
                            bookingNavActions.navigateToSignIn()
                        },
                        doOnSignOutCancelled = {
                            navController.navigateUp()
                        }
                    )
                }
            }

            composable(
                BookingScreens.BOOKER_PROFILE
            ) {
                AppModalDrawer(
                    drawerState = drawerState,
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: "",
                    bookerNavActions = bookingNavActions,
                    agencyNavActions = agencyNavActions,
                    isSignedIn = { authRepo.isSignedIn },
                ) {
                    BookerProfile(
                        onNavigateToAccount = { bookingNavActions.navigateToAccount() },
                        onNavigateToMoMoAccount = { bookingNavActions.navigateToMoMoAccounts() },
                        onNavigateToOMAccount = { bookingNavActions.navigateToOMAccounts() },
                        onNavigateToBookerAgencySettings = { bookingNavActions.navigateToAgencySettings() },
                        onNavigateToCreditCardAccount = { bookingNavActions.navigateToCreditCardAccounts() },
                        navigateUp = {
                            defaultNavigateUp()
                        }
                    ) {
                        scope.launch { drawerState.open() }
                    }
                }
            }

            composable(
                BookingDestinations.BOOKER_ACCOUNT_ROUTE
            ) {
                BookerAccount(
                    onComplete = { defaultNavigateUp() },
                    navigateUp = { defaultNavigateUp() }
                )
            }

            composable(BookingDestinations.BOOKER_MOMO_ACCOUNTS_ROUTE)
            {
                BookerMoMoAccounts(navigateBack = { defaultNavigateUp() }, navigateToDetails = {
                    bookingNavActions.navigateToMoMoAccountDetails()
                })
            }

            composable(
                BookingDestinations.BOOKER_MOMO_ACCOUNT_DETAILS_ROUTE
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BOOKER_MOMO_ACCOUNTS_ROUTE)
                }
                BookerMoMoAccountDetails(
                    navigateBack = {
                        defaultNavigateUp()
                    }, vm = hiltViewModel(parentEntry)
                )
            }

            composable(BookingDestinations.BOOKER_OM_ACCOUNTS_ROUTE)
            {
                BookerOMAccounts(navigateBack = { defaultNavigateUp() }, navigateToDetails = {
                    bookingNavActions.navigateToOMAccountDetails()
                })
            }

            composable(
                BookingDestinations.BOOKER_OM_ACCOUNT_DETAILS_ROUTE
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BOOKER_OM_ACCOUNTS_ROUTE)
                }
                BookerOMAccountDetails(
                    navigateBack = {
                        defaultNavigateUp()
                    }, vm = hiltViewModel(parentEntry)
                )
            }

        }

        navigation(
            startDestination = AgencyDestinations.AGENCY_CREATION_WELCOME_ROUTE,
            route = BASE_AGENCY
        ) {
            composable(
                route = AgencyDestinations.AGENCY_CREATION_WELCOME_ROUTE
            ) {
                AppModalDrawer(
                    drawerState = drawerState,
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: "",
                    bookerNavActions = bookingNavActions,
                    agencyNavActions = agencyNavActions,
                    isSignedIn = { authRepo.isSignedIn },
                ) {

                }
            }

            composable(
                route = AgencyDestinations.AGENCY_CREATION_WELCOME_PROFILE_ROUTE
            ) {
                AppModalDrawer(
                    drawerState = drawerState,
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: "",
                    bookerNavActions = bookingNavActions,
                    agencyNavActions = agencyNavActions,
                    isSignedIn = { authRepo.isSignedIn },
                ) {

                }
            }
        }

        /*               Agency navigations
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

               Universe
               navigation("Booking", route = BASE_UNIV) {
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
       */
    }
}
