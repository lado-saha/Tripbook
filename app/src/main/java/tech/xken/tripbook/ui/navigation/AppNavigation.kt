package tech.xken.tripbook.ui.navigation

import android.os.Bundle
import androidx.navigation.NavHostController
import tech.xken.tripbook.ui.navigation.UnivScreens.UNIVERSE_SEARCH
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_CALLER_ROUTE
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_FIELDS
import java.util.ArrayList

object UnivScreens {
    const val UNIVERSE_SEARCH = "universe_search"
    const val UNIVERSE_EDITOR = "univ_editor"
    const val UNIVERSE_VISUALIZER = "univ_visual"
}

object AgencyScreens {
    const val AGENCY_PARK_DETAILS = "agency_park_details"
}

object UnivDestinations {
    const val UNIV_SEARCH_ROUTE =
        "$UNIVERSE_SEARCH/{$UNIV_SEARCH_CALLER_ROUTE}/{$UNIV_SEARCH_FIELDS}"
}

object UniverseArgs {
    const val UNIV_SEARCH_RESULT = "univ_search_results"
    const val UNIV_SEARCH_CALLER_ROUTE = "univ_search_caller_id"
    const val UNIV_SEARCH_FIELDS = "univ_search_fields"
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

class UnivNavActions(private val navController: NavHostController) {
    fun navigateToUnivSearch(caller: String, fields: Array<Int>) {
        navController.navigate(route = "$UNIVERSE_SEARCH/$caller/${fields.fold(""){ acc, i -> "$acc $i" }}") {
            launchSingleTop = true
        }
    }
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