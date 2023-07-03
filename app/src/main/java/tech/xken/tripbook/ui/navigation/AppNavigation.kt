package tech.xken.tripbook.ui.navigation

import androidx.navigation.NavHostController
import tech.xken.tripbook.data.models.Station
import tech.xken.tripbook.domain.DEFAULT_UNIV_QUERY_FIELDS
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_ID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_ID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_IS_EDIT_MODE
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_JOB_ID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_LAT
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_LON
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_NAME
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_DASHBOARD
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_JOBS
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_JOB_DETAILS
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_LOCATION
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_PERSONNEL
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_PROFILE
import tech.xken.tripbook.ui.navigation.UnivScreens.UNIVERSE_SEARCH
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIVERSE_SEARCH_RETURN_TOWNS_ONLY
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_CALLER_SCREEN
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_FIELDS
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_HAS_PRESELECTED_FIELDS
import java.util.UUID

object UnivScreens {
    const val BASE_UNIV = "agency"
    const val UNIVERSE_SEARCH = "universe_search"
    const val UNIVERSE_EDITOR = "univ_editor"
    const val UNIVERSE_VISUALIZER = "univ_visual"
}

object AgencyScreens {
    const val BASE_AGENCY = "agency"
    const val AGENCY_STATION_DASHBOARD = "station_dashboard"
    const val AGENCY_STATION_PROFILE = "station_profile"
    const val AGENCY_STATION_LOCATION = "station_location"
    const val AGENCY_STATION_PERSONNEL = "station_personnel"
    const val AGENCY_STATION_JOBS = "station_jobs"
    const val AGENCY_STATION_JOB_DETAILS = "station_jobs_details"
}

object BookingScreens {
    const val BASE_BOOKER = "booker"
    const val BOOKER_SIGN_IN = "booker_sign_in"
    const val BOOKER_PROFILE = "booker_profile"
}

object AgencyArgs {
    const val AGENCY_ID = "agency_id"
    const val AGENCY_STATION_IS_EDIT_MODE = "station_is_new"
    const val AGENCY_STATION_ID = "stationID"
    const val AGENCY_STATION_NAME = "station_name"
    const val AGENCY_STATION_LON = "station_lon"
    const val AGENCY_STATION_LAT = "station_lan"
    const val AGENCY_STATION_JOB_ID = "station_job_id"
}

object AgencyDestinations {
    const val AGENCY_STATION_DASHBOARD_ROUTE =
        "$AGENCY_STATION_DASHBOARD/{$AGENCY_STATION_IS_EDIT_MODE}/{$AGENCY_STATION_ID}"
    const val AGENCY_STATION_PROFILE_ROUTE =
        "$AGENCY_STATION_PROFILE/{$AGENCY_STATION_ID}/{$AGENCY_STATION_IS_EDIT_MODE}"
    const val AGENCY_STATION_LOCATION_ROUTE =
        "$AGENCY_STATION_LOCATION/{$AGENCY_STATION_ID}/{$AGENCY_STATION_NAME}/{$AGENCY_STATION_LAT}/{$AGENCY_STATION_LON}"
    const val AGENCY_STATION_PERSONNEL_ROUTE =
        "$AGENCY_STATION_PERSONNEL/{$AGENCY_STATION_ID}/{$AGENCY_ID}"
    const val AGENCY_STATION_JOBS_ROUTE = "$AGENCY_STATION_JOBS/{$AGENCY_STATION_ID}"
    const val AGENCY_STATION_JOB_DETAILS_ROUTE = "$AGENCY_STATION_JOB_DETAILS/{$AGENCY_STATION_JOB_ID}"
}

object UnivDestinations {
    const val UNIV_SEARCH_ROUTE =
        "$UNIVERSE_SEARCH/{$UNIV_SEARCH_HAS_PRESELECTED_FIELDS}/{$UNIV_SEARCH_CALLER_SCREEN}/{$UNIVERSE_SEARCH_RETURN_TOWNS_ONLY}/{$UNIV_SEARCH_FIELDS}"
}

object UniverseArgs {
    //States whether we are going into the search view with already selected results stored in the cache
    const val UNIV_SEARCH_HAS_PRESELECTED_FIELDS = "univ_search_has_preselected_fields"
    const val UNIV_SEARCH_RESULT = "univ_search_results"
    const val UNIV_SEARCH_CALLER_SCREEN = "univ_search_caller_screen"
    const val UNIV_SEARCH_FIELDS = "univ_search_fields"
    const val TOWNS_TO_EXCLUDE_BY_IDS = "towns_for_exclusion"
    const val UNIVERSE_SEARCH_RETURN_TOWNS_ONLY = "univ_search_return_towns_only"
}

object BookingNavArgs {
    const val IS_EDIT_MODE = "is_edit_mode"
}

class AgencyNavActions(private val navController: NavHostController) {

    fun navigateToStationJobDetails(
        jobId: String
    ){
        navController.navigate(route = "$AGENCY_STATION_JOB_DETAILS/$jobId"){
            launchSingleTop = true
        }
    }
    fun navigateToStationJobs(
        station: String
    ){
        navController.navigate(route = "$AGENCY_STATION_JOBS/$station"){
            launchSingleTop = true
        }
    }

    fun navigateToStationPersonnel(
        station: String,
        agency: String
    ){
        navController.navigate(route = "$AGENCY_STATION_PERSONNEL/$station/$agency"){
            launchSingleTop = true
        }
    }
    fun navigateToStationLocation(
        station: Station,
    ) {
        navController.navigate(route = "$AGENCY_STATION_LOCATION/${station.id}/${station.name}/${station.lat ?: 0f}/${station.lon ?: 0f}") {
            launchSingleTop = true
        }
    }

    fun navigateToStationDashboard(
        isEditMode: Boolean = false,
        station: String = UUID.randomUUID().toString(),
    ) {
        navController.navigate(route = "$AGENCY_STATION_DASHBOARD/$isEditMode/$station") {
            launchSingleTop = false
        }
    }

    fun navigateToStationProfile(isEditMode: Boolean, station: String) {
        navController.navigate(route = "$AGENCY_STATION_PROFILE/$station/$isEditMode") {
            launchSingleTop = true
        }
    }
}

class UnivNavActions(private val navController: NavHostController) {
    fun navigateToUnivSearch(
        hasPreselectedFields: Boolean,
        callerScreen: String,
        returnTownsOnly: Boolean = false,
        fields: Array<Int> = DEFAULT_UNIV_QUERY_FIELDS,
    ) {
        navController.navigate(
            route = "$UNIVERSE_SEARCH/${hasPreselectedFields}/$callerScreen/${returnTownsOnly}/${
                fields.fold(
                    ""
                ) { acc, i -> "$acc $i" }
            }"
        ) {
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

    fun navigateToProfile() {
        navController.navigate(BookingScreens.BOOKER_PROFILE) {
            launchSingleTop = true
        }
    }

}