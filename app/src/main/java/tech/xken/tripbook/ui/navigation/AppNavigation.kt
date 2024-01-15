package tech.xken.tripbook.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavHostController
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.ImageUiState
import tech.xken.tripbook.data.models.agency.Station
import tech.xken.tripbook.domain.DEFAULT_UNIV_QUERY_FIELDS
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_CALLER_UUID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_ID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_IMAGE_UI_STATE
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_ID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_IS_EDIT_MODE
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_JOB_ID
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_LAT
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_LON
import tech.xken.tripbook.ui.navigation.AgencyArgs.AGENCY_STATION_NAME
import tech.xken.tripbook.ui.navigation.AgencyDestinations.AGENCY_FINANCE_ROUTE
import tech.xken.tripbook.ui.navigation.AgencyDestinations.AGENCY_PROFILE_ROUTE
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_FINANCE
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_HELP_CENTER
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_IMAGE_PICKER
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_PROFILE
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_DASHBOARD
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_JOBS
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_JOB_DETAILS
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_LOCATION
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_PERSONNEL
import tech.xken.tripbook.ui.navigation.AgencyScreens.AGENCY_STATION_PROFILE
import tech.xken.tripbook.ui.navigation.BookingDestinations.AGENCY_PORTAL_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_CREDIT_CARDS_ACCOUNTS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_MOMO_ACCOUNTS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_MOMO_ACCOUNT_DETAILS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_OM_ACCOUNTS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingDestinations.BOOKER_OM_ACCOUNT_DETAILS_ROUTE
import tech.xken.tripbook.ui.navigation.BookingNavArgs.CARD_NUMBER
import tech.xken.tripbook.ui.navigation.BookingNavArgs.SHOULD_SIGN_OUT
import tech.xken.tripbook.ui.navigation.BookingScreens.AGENCY_PORTAL
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_ACCOUNT
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_AGENCY_PICKING
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_AGENCY_SETTINGS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_AUTHENTICATION
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_BOOKS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_CREDIT_CARDS_ACCOUNTS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_CREDIT_CARD_ACCOUNT_DETAILS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_IMAGE_PICKER
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_MOMO_ACCOUNTS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_MOMO_ACCOUNT_DETAILS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_OM_ACCOUNTS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_OM_ACCOUNT_DETAILS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_PROFILE
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_TRIP_DETAILS
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_TRIP_PAYMENT
import tech.xken.tripbook.ui.navigation.BookingScreens.BOOKER_TRIP_SEARCH
import tech.xken.tripbook.ui.navigation.UnivScreens.UNIVERSE_SEARCH
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIVERSE_SEARCH_RETURN_TOWNS_ONLY
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_CALLER_SCREEN
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_FIELDS
import tech.xken.tripbook.ui.navigation.UniverseArgs.UNIV_SEARCH_HAS_PRESELECTED_FIELDS
import tech.xken.tripbook.ui.screens.agency.MainAgencyActivity
import java.util.UUID

/** Screens */
object UnivScreens {
    const val BASE_UNIV = "agency"
    const val UNIVERSE_SEARCH = "universe_search"
    const val UNIVERSE_EDITOR = "univ_editor"
    const val UNIVERSE_VISUALIZER = "univ_visual"
}

object AgencyScreens {
    const val BASE_AGENCY = "agency"

    //    -------------------------------------------------
    const val AGENCY_PROFILE = "agency_profile"
    const val AGENCY_SUPPORT = "agency_support"
    const val AGENCY_HELP_CENTER = "agency_help_center"
    const val AGENCY_FINANCE = "agency_finance"
//    -------------------------------------------------

    const val AGENCY_STATION_DASHBOARD = "station_dashboard"
    const val AGENCY_STATION_PROFILE = "station_profile"
    const val AGENCY_STATION_LOCATION = "station_location"
    const val AGENCY_STATION_PERSONNEL = "station_personnel"
    const val AGENCY_STATION_JOBS = "station_jobs"
    const val AGENCY_STATION_JOB_DETAILS = "station_jobs_details"

    const val AGENCY_IMAGE_PICKER = "ag_image_picker"
}

object BookingScreens {
    const val BASE_BOOKER = "booker"
    const val BOOKER_AUTHENTICATION = "booker_authentication"
    const val BOOKER_BOOKS = "booker_books"
    const val BOOKER_PROFILE = "booker_profile"
    const val BOOKER_ACCOUNT = "booker_account"
    const val BOOKER_MOMO_ACCOUNTS = "booker_accounts_momo"
    const val BOOKER_OM_ACCOUNTS = "booker_accounts_om"
    const val BOOKER_CREDIT_CARDS_ACCOUNTS = "booker_accounts_credit_card"
    const val BOOKER_MOMO_ACCOUNT_DETAILS = "booker_account_momo_details"
    const val BOOKER_OM_ACCOUNT_DETAILS = "booker_account_om_details"
    const val BOOKER_CREDIT_CARD_ACCOUNT_DETAILS = "booker_accounts_credit_card_details"
    const val BOOKER_AGENCY_SETTINGS = "booker_agency_settings"
    const val BOOKER_TRIP_SEARCH = "booker_trip_search"
    const val BOOKER_AGENCY_PICKING = "booker_agency_picking"
    const val BOOKER_TRIP_DETAILS = "booker_trip_details"
    const val BOOKER_TRIP_PAYMENT = "booker_trip_payment"
    const val AGENCY_PORTAL = "agency_portal"

    const val BOOKER_IMAGE_PICKER = "bo_image_picker"
}

/** Destinations */
object BookingDestinations {
    const val BOOKER_AUTHENTICATION_ROUTE = "$BOOKER_AUTHENTICATION/{$SHOULD_SIGN_OUT}"
    const val BOOKER_PROFILE_ROUTE = BOOKER_PROFILE
    const val BOOKER_BOOKS_ROUTE = BOOKER_BOOKS
    const val BOOKER_TRIP_SEARCH_ROUTE = BOOKER_TRIP_SEARCH
    const val BOOKER_AGENCY_SEARCH_ROUTE = BOOKER_AGENCY_PICKING
    const val BOOKER_TRIP_DETAILS_ROUTE = BOOKER_TRIP_DETAILS
    const val BOOKER_TRIP_PAYMENT_ROUTE = BOOKER_TRIP_PAYMENT

    const val BOOKER_ACCOUNT_ROUTE = "$BOOKER_PROFILE/$BOOKER_ACCOUNT"
    const val BOOKER_MOMO_ACCOUNTS_ROUTE = "$BOOKER_PROFILE/$BOOKER_MOMO_ACCOUNTS"
    const val BOOKER_OM_ACCOUNTS_ROUTE = "$BOOKER_PROFILE/$BOOKER_OM_ACCOUNTS"
    const val BOOKER_CREDIT_CARDS_ACCOUNTS_ROUTE = "$BOOKER_PROFILE/$BOOKER_CREDIT_CARDS_ACCOUNTS"
    const val BOOKER_AGENCY_SETTINGS_ROUTE = "$BOOKER_PROFILE/$BOOKER_AGENCY_SETTINGS"

    const val BOOKER_MOMO_ACCOUNT_DETAILS_ROUTE =
        "$BOOKER_MOMO_ACCOUNTS_ROUTE/$BOOKER_MOMO_ACCOUNT_DETAILS"
    const val BOOKER_OM_ACCOUNT_DETAILS_ROUTE =
        "$BOOKER_OM_ACCOUNTS_ROUTE/$BOOKER_OM_ACCOUNT_DETAILS"
    const val BOOKER_CREDIT_CARD_ACCOUNT_DETAILS_ROUTE =
        "$BOOKER_PROFILE/$BOOKER_CREDIT_CARD_ACCOUNT_DETAILS/{$CARD_NUMBER}"

    const val AGENCY_PORTAL_ROUTE = AGENCY_PORTAL
    const val BOOKER_IMAGE_PICKER_ROUTE = BOOKER_IMAGE_PICKER
}

object UnivDestinations {
    const val UNIV_SEARCH_ROUTE =
        "$UNIVERSE_SEARCH/{$UNIV_SEARCH_HAS_PRESELECTED_FIELDS}/{$UNIV_SEARCH_CALLER_SCREEN}/{$UNIVERSE_SEARCH_RETURN_TOWNS_ONLY}/{$UNIV_SEARCH_FIELDS}"
}

object AgencyDestinations {
    const val AGENCY_PROFILE_ROUTE = AGENCY_PROFILE
    const val AGENCY_FINANCE_ROUTE = AGENCY_FINANCE
//    const val AGENCY_SUPPORT_ROUTE = AGENCY_SUPPORT

    const val AGENCY_STATION_DASHBOARD_ROUTE =
        "$AGENCY_STATION_DASHBOARD/{$AGENCY_STATION_IS_EDIT_MODE}/{$AGENCY_STATION_ID}"
    const val AGENCY_HELP_CENTER_ROUTE = AGENCY_HELP_CENTER
    const val AGENCY_STATION_PROFILE_ROUTE =
        "$AGENCY_STATION_PROFILE/{$AGENCY_STATION_ID}/{$AGENCY_STATION_IS_EDIT_MODE}"
    const val AGENCY_STATION_LOCATION_ROUTE =
        "$AGENCY_STATION_LOCATION/{$AGENCY_STATION_ID}/{$AGENCY_STATION_NAME}/{$AGENCY_STATION_LAT}/{$AGENCY_STATION_LON}"
    const val AGENCY_STATION_PERSONNEL_ROUTE =
        "$AGENCY_STATION_PERSONNEL/{$AGENCY_STATION_ID}/{$AGENCY_ID}"
    const val AGENCY_STATION_JOBS_ROUTE = "$AGENCY_STATION_JOBS/{$AGENCY_STATION_ID}"
    const val AGENCY_STATION_JOB_DETAILS_ROUTE =
        "$AGENCY_STATION_JOB_DETAILS/{$AGENCY_STATION_JOB_ID}"
    const val AGENCY_IMAGE_PICKER_ROUTE =
        "$AGENCY_IMAGE_PICKER/{$AGENCY_IMAGE_UI_STATE}/{$AGENCY_CALLER_UUID}"
}

/** Nav Args */
object AgencyArgs {
    const val AGENCY_ID = "agency_id"
    const val AGENCY_STATION_IS_EDIT_MODE = "station_is_new"
    const val AGENCY_STATION_ID = "stationID"
    const val AGENCY_STATION_NAME = "station_name"
    const val AGENCY_STATION_LON = "station_lon"
    const val AGENCY_STATION_LAT = "station_lan"
    const val AGENCY_STATION_JOB_ID = "station_job_id"
    const val AGENCY_IMAGE_UI_STATE = "agency_image_ui_state"
    const val AGENCY_CALLER_UUID = "img_picker_caller"
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
    const val SHOULD_SIGN_OUT = "should_sign_out"
    const val PHONE_NUMBER = "phone_number"
    const val CARD_NUMBER = "card_number"
    const val MOMO_ACCOUNT = "momo_account"

    const val BOOKER_IMAGE_UI_STATE = "img_ui_state_json"
}

/** Nav Actions */
class BookingNavActions(
    private val navController: NavHostController,
    private val authRepo: AuthRepo,
    private val activityLauncher: ActivityResultLauncher<Intent>,
    private val context: Context
) {

//    private val parser: Json = Json { encodeDefaults = true }

    fun navigateToImagePicker() {
//        val strUiState =
//            parser.encodeToString(imageUiState)
        navController.navigate(route = BOOKER_IMAGE_PICKER) {
            launchSingleTop = true
        }
    }

    fun navigateToSignIn(shouldSignOut: Boolean = false) {
        navController.navigate(route = "$BOOKER_AUTHENTICATION/$shouldSignOut") {
            launchSingleTop = true
        }
    }

    fun navigateToProfile() {
        navController.navigate(BOOKER_PROFILE) {
            launchSingleTop = true
        }
    }

    fun navigateToAccount() {
        navController.navigate("$BOOKER_PROFILE/$BOOKER_ACCOUNT") {
            launchSingleTop = true
        }
    }

    fun navigateToMoMoAccounts() {
        navController.navigate(BOOKER_MOMO_ACCOUNTS_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToMoMoAccountDetails() {
        navController.navigate(BOOKER_MOMO_ACCOUNT_DETAILS_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToCreditCardAccounts(cardNumber: String) {
        navController.navigate("$BOOKER_PROFILE/$BOOKER_CREDIT_CARDS_ACCOUNTS/$BOOKER_CREDIT_CARD_ACCOUNT_DETAILS/{$cardNumber}") {
            launchSingleTop = true
        }
    }

    fun navigateToOMAccountDetails() {
        navController.navigate(BOOKER_OM_ACCOUNT_DETAILS_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToOMAccounts() {
        navController.navigate(BOOKER_OM_ACCOUNTS_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToCreditCardAccounts() {
        navController.navigate(BOOKER_CREDIT_CARDS_ACCOUNTS_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToAgencySettings() {
        navController.navigate(BOOKER_AGENCY_SETTINGS) {
            launchSingleTop = true
        }
    }

    fun navigateToTripSearch() {
        navController.navigate(BOOKER_TRIP_SEARCH) {
            launchSingleTop = true

        }
    }

    fun navigateToAgencyPicking() {
        navController.navigate(BOOKER_AGENCY_PICKING) {
            launchSingleTop = true
        }
    }

    fun navigateToTripDetails() {
        navController.navigate(BOOKER_TRIP_DETAILS) {
            launchSingleTop = true
        }
    }

    fun navigateToTripPayment() {
        navController.navigate(BOOKER_TRIP_PAYMENT) {
            launchSingleTop = true
        }
    }

    fun navigateToAgencyPortal() {
        navController.navigate(AGENCY_PORTAL_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToAgency() {
        activityLauncher.launch(Intent(context, MainAgencyActivity::class.java))
    }
}

class AgencyNavActions(
    private val navController: NavHostController,
    private val activity: MainAgencyActivity
) {
    fun navigateToImagePicker(imageUiState: ImageUiState, callerUUID: String) {
        navController.navigate(route = "$AGENCY_IMAGE_PICKER/${imageUiState}/$callerUUID") {
            launchSingleTop = true
        }
    }

    fun leaveAgency() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    fun navigateToAgencyProfile() {
        navController.navigate(route = AGENCY_PROFILE_ROUTE) {
            launchSingleTop = true
        }
    }


    fun navigateToAgencyFinance() {
        navController.navigate(route = AGENCY_FINANCE_ROUTE) {
            launchSingleTop = true
        }
    }


    /**
     * Navigate to the main support page
     */
//    fun navigateToAgencySupport() {
//        navController.navigate(route = AGENCY_SUPPORT_ROUTE) {
//            launchSingleTop = true
//        }
//    }

    fun navigateHelpCenter() {
        navController.navigate(route = AGENCY_HELP_CENTER) {
            launchSingleTop = true
        }
    }

    fun navigateToStationJobDetails(
        jobId: String
    ) {
        navController.navigate(route = "$AGENCY_STATION_JOB_DETAILS/$jobId") {
            launchSingleTop = true
        }
    }

    fun navigateToStationJobs(
        station: String
    ) {
        navController.navigate(route = "$AGENCY_STATION_JOBS/$station") {
            launchSingleTop = true
        }
    }

    fun navigateToStationPersonnel(
        station: String,
        agency: String
    ) {
        navController.navigate(route = "$AGENCY_STATION_PERSONNEL/$station/$agency") {
            launchSingleTop = true
        }
    }

    fun navigateToStationLocation(
        station: Station,
    ) {
        navController.navigate(route = "$AGENCY_STATION_LOCATION/${4/*station.id*/}/${station.name}/${station.lat ?: 0f}/${station.lon ?: 0f}") {
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

class UnivNavActions(
    private val navController: NavHostController
) {
    fun navigateToUnivSearch(
        hasPreselectedFields: Boolean,
        callerScreen: String,
        returnTownsOnly: Boolean = false,
        fields: Array<Int> = DEFAULT_UNIV_QUERY_FIELDS,
    ) {
        navController.navigate(
            route = "$UNIVERSE_SEARCH/$hasPreselectedFields/$callerScreen/$returnTownsOnly/" + fields.fold(
                ""
            ) { acc, i -> "$acc $i" }
        ) {
            launchSingleTop = true
        }
    }
}

