package tech.xken.tripbook.data.models.agency

import androidx.room.Entity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.booker.CI
import tech.xken.tripbook.domain.DATE_NOW

typealias SN = SerialName


@Serializable
@Entity(AgencyAccount.NAME, primaryKeys = ["agency_id"])
data class AgencyAccount(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("name") @SN("name") val name: String = "",
    @CI("physical_created_on") @SN("physical_created_on") val physicalCreatedOn: LocalDate = DATE_NOW,
    @CI("motto") @SN("motto") val motto: String = "",
    @CI("about_us") @SN("about_us") val aboutUs: String? = null,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("timestamp") val timestamp: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String,
        @SN("data_json") val data: AgencyAccount? = null,
    ) {
        companion object {
            const val NAME = "agency_account_log"
        }

    }

    companion object {
        const val NAME = "agency_account"
    }
}

@Serializable
@Entity(AgencyMetadata.NAME, primaryKeys = ["agency_id"])
data class AgencyMetadata(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("is_suspended") @SN("is_suspended") val isSuspended: Boolean = false,
    @CI("reputation") @SN("reputation") val reputation: Float = 5.0f,
    @CI("accident_count") @SN("accident_count") val accidentCount: Int = 0,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    companion object {
        const val NAME = "agency_metadata"
    }
}


@Serializable
@Entity(AgencySettings.NAME, primaryKeys = ["agency_id"])
data class AgencySettings(
    @CI("agency_id") @SN("agency_id") val agencyId: String? = "",
    @CI("cost_per_km") @SN("cost_per_km") val costPerKm: Double?,
    @CI("vip_cost_per_km") @SN("vip_cost_per_km") val vipCostPerKm: Double?,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    companion object {
        const val NAME = "agency_settings"
    }
}

@Serializable
@Entity(AgencyEvent.NAME, primaryKeys = ["agency_id", "event_id"])
data class AgencyEvent(
    @CI("event_id") @SN("event_id") val eventId: String = "",
    @CI("start_time") @SN("start_time") val startTime: Instant = Clock.System.now(),
    @CI("end_time") @SN("end_time") val endTime: Instant = Clock.System.now(),
    @CI("description") @SN("description") val description: String = "",
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    @SerialName(Log.NAME)
    data class Log(
        @SN("data_json") val data: AgencyEvent? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("event_id") val eventId: String,
        @SN("timestamp") val timestamp: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String,
    ) {
        companion object {
            const val NAME = "agency_event_log"
        }

    }

    companion object {
        const val NAME = "agency_event"
    }
}

/**
 * Profile
 * Vehicle
 * Support Contacts
 * Stations
 * Towns
 * Jobs & Salaries
 * Locations
 * Trips
 * Personnel
 * Other
 * */