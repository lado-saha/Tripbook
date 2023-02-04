package tech.xken.tripbook.ui.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.navigation.NavHostController

object Routes {
    const val BOOKING = "booking"
}

object UnivScreens {
    const val UNIVERSE_SEARCH = "universe search"
    const val UNIVERSE_EDITOR = "univ_editor"
    const val UNIVERSE_VISUALIZER = "univ_visual"
}

object UniverseDestinationArgs {
    const val UNIV_SEARCH_RESULT = "town_search_results"
    const val TOWNS_TO_EXCLUDE_BY_IDS = "towns_for_exclusion"
}

object BookingScreens {
    const val BOOKER_SIGN_IN = "booker_sign_in"
    const val BOOKER_SIGN_UP = "booker_sign_up"
    const val BOOKER_DETAILS = "booker_details"
}

object BookingNavArgs {
    const val IS_EDIT_MODE = "is_edit_mode"
}

class BookingNavActions(private val navController: NavHostController) {
    fun navigateToSignIn() {
        navController.navigate(BookingScreens.BOOKER_SIGN_IN) {
            launchSingleTop = true
        }
    }

    fun navigateToSignUp() {
        navController.navigate(BookingScreens.BOOKER_SIGN_UP) {
            launchSingleTop = true
        }
    }

    fun navigateToBookerDetails() {
        navController.navigate(BookingScreens.BOOKER_DETAILS) {
            launchSingleTop = true
        }
    }
}