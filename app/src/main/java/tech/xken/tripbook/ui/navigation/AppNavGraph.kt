package tech.xken.tripbook.ui.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import tech.xken.tripbook.R
import tech.xken.tripbook.ui.navigation.UnivScreens.UNIVERSE_SEARCH
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_CALLER_ROUTE
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_FIELDS
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_RESULT
import tech.xken.tripbook.ui.screens.agency.ParkDetails
import tech.xken.tripbook.ui.screens.agency.ParkDetailsVM
import tech.xken.tripbook.ui.screens.booking.BookerSignIn
import tech.xken.tripbook.ui.screens.booking.BookerSignUpOrDetails
import tech.xken.tripbook.ui.screens.universe.*

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AgencyScreens.AGENCY_PARK_DETAILS,
) {
    val bookingNavActions = BookingNavActions(navController)
    val univNavActions = UnivNavActions(navController)
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        //Agency
        composable(AgencyScreens.AGENCY_PARK_DETAILS) {
            ParkDetails(vm = hiltViewModel(), navigateBack = { navController.popBackStack() }, onTownClick = {
                univNavActions.navigateToUnivSearch(
                    AgencyScreens.AGENCY_PARK_DETAILS, arrayOf(
                        R.string.lb_town, R.string.lb_region
                    )
                )
            })
        }
        // sign up - sign in
        // Booking
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

        //Universe
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
            route = UnivDestinations.UNIV_SEARCH_ROUTE,
            arguments = listOf(
                navArgument(UNIV_SEARCH_FIELDS) { type = NavType.StringType },
                navArgument(UNIV_SEARCH_CALLER_ROUTE) { type = NavType.StringType },
            )
        ) {
            UniverseSearch { results ->
                navController.popBackStack()
                Log.d("Univ was Selected: ", results.toString())
            }
        }
    }
}
