package tech.xken.tripbook.data.models.agency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This enum class represents the reasons for a trip cancellation.
 */
enum class TripCancellationReasons {
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
data class AgencyLegalSettings(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("business_entity") val businessEntity: String,
    @SerialName("licence") val licence: String,
    @SerialName("privacy_policy") val privacyPolicy: String,
)

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
class AgencyRefundPolicy(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("max_cancellation_days_before_departure") val maxCancellationDaysBeforeDeparture: Int,
    @SerialName("reason") val reason: TripCancellationReasons,
    @SerialName("refund_percentage") val refundPercentage: Double,
    @SerialName("description") val description: String,
)



