package tech.xken.tripbook.ui.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tech.xken.tripbook.ui.navigation.UniverseDestinationArgs.UNIV_SEARCH_RESULT
import tech.xken.tripbook.ui.screens.booking.BookerSignIn
import tech.xken.tripbook.ui.screens.booking.BookerSignUpOrDetails
import tech.xken.tripbook.ui.screens.booking.BookerSignUpVM
import tech.xken.tripbook.ui.screens.universe_editor.UniverseEditorScreen
import tech.xken.tripbook.ui.screens.universe_editor.UniverseSearchScreen
import tech.xken.tripbook.ui.screens.universe_editor.UniverseVisualizer
import tech.xken.tripbook.ui.screens.universe_editor.UniverseVisualizerVM

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = BookingScreens.BOOKER_SIGN_IN,
) {
    val bookingNavActions = BookingNavActions(navController)
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
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
                navController.navigate(route = UnivScreens.UNIVERSE_SEARCH)
            }
        }
        composable(UnivScreens.UNIVERSE_EDITOR) {
            UniverseEditorScreen()
        }
        composable(UnivScreens.UNIVERSE_SEARCH) {
            UniverseSearchScreen { results ->
                navController.getBackStackEntry(UnivScreens.UNIVERSE_VISUALIZER).savedStateHandle[UNIV_SEARCH_RESULT] =
                    results
                Log.d(
                    "UNIV SEARCH CLEAR",
                    navController.getBackStackEntry(UnivScreens.UNIVERSE_VISUALIZER)
                        .savedStateHandle.contains(UNIV_SEARCH_RESULT).toString()
                )
                navController.popBackStack()
            }
        }

    }
}
