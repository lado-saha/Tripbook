package tech.xken.tripbook.data.models.agency

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import tech.xken.tripbook.data.models.DbAction

typealias CI = ColumnInfo

/**
 * This enum class represents the reasons for a trip cancellation.
 */
enum class TripCancellationReason {
    /**
     * The booking was cancelled due to a medical reason on the part of the booker.
     */
    BOOKER_MEDICAL_REASON,

    /**
     * The booking was cancelled due to a change of plan on the part of the booker.
     */
    BOOKER_CHANGE_OF_PLAN,

    /**
     * The booking was cancelled due to weather conditions.
     */
    AGENCY_WEATHER,

    /**
     * The booking was cancelled due to technical difficulties on the part of the agency.
     */
    AGENCY_TECHNICAL_DIFFICULTIES,

    /**
     * Other
     */
    OTHER
}

/**
 * This data class stores the legal settings for a travel agency.
 *
 * @property agencyId The ID of the travel agency.
 * @property businessEntity The business entity of the travel agency.
 * @property licence The licence of the travel agency.
 * @property privacyPolicy The privacy policy of the travel agency.
 */
@Serializable
@Entity(AgencyLegalDocs.NAME, primaryKeys = ["agency_id"])
data class AgencyLegalDocs(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("business_entity_url") @SN("business_entity_url") val businessEntityUrl: String? = null,
    @CI("licence_url") @SN("licence_url") val licenceUrl: String? = null,
    @CI("privacy_policy_url") @SN("privacy_policy_url") val privacyPolicyUrl: String? = null,
    @CI("scope_of_authority_url") @SN("scope_of_authority_url") val scopeOfAuthorityUrl: String? = null,
    @CI("duties_of_agency_url") @SN("duties_of_agency_url") val dutiesOfAgencyUrl: String? = null,
    @CI("rights_of_client_url") @SN("rights_of_client_url") val rightsOfClientUrl: String? = null,
    @CI("liability_of_agency_url") @SN("liability_of_agency_url") val liabilityOfAgencyUrl: String? = null,
    @CI("dispute_resolution_process_url") @SN("dispute_resolution_process_url") val disputeResolutionProcessUrl: String? = null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyLegalDocs? = null,
        @SN("agency_id") val agencyId: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String?=null
    ) {
        companion object {
            const val NAME = "agency_legal_docs_log"
        }

    }

    companion object {
        const val NAME = "agency_legal_docs"
    }
}

/**
 * This class stores the refund policy for a travel agency.
 *
 * @property agencyId The ID of the travel agency.
 * @property maxCancellationDaysBeforeDeparture The maximum number of days before departure that a booking can be cancelled.
 * @property reason The reason for the cancellation.
 * @property refundPercentage The percentage of the booking price that will be refunded if the booking is cancelled.
 * @property description A description of the refund policy.
 */
@Serializable
@Entity(AgencyRefundPolicy.NAME, primaryKeys = ["agency_id", "reason"])
class AgencyRefundPolicy(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("reason") @SN("reason") val reason: TripCancellationReason = TripCancellationReason.OTHER,
    @CI("max_cancellation_days_before_departure") @SN("max_cancellation_days_before_departure") val maxCancellationDaysBeforeDeparture: Int = 0,
    @CI("refund_percentage") @SN("refund_percentage") val refundPercentage: Double = 0.0,
    @CI("description") @SN("description") val description: String = "",
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyRefundPolicy? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("reason") val reason: TripCancellationReason,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String?=null
    ) {
        companion object {
            const val NAME = "agency_refund_policy_log"
        }

    }

    companion object {
        const val NAME = "agency_refund_policy"
    }
}



