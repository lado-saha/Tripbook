package tech.xken.tripbook.data.models.agency

import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import tech.xken.tripbook.data.models.DbAction

/**
 * This data class represents phone support.
 *
 * @property agencyId The ID of the travel agency.
 * @property phoneCode The country code for the phone number.
 * @property phoneNumber The phone number of the travel agency.
 * @property isWhatsapp A Boolean value that indicates whether this phone number supports WhatsApp.
 * @property isTelegram A Boolean value that indicates whether this phone number supports Telegram.
 */
@Serializable
@Entity(AgencyPhoneSupport.NAME, primaryKeys = ["agency_id", "phone_code", "phone_number"])
data class AgencyPhoneSupport(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("phone_code") @SN("phone_code") val phoneCode: String = "",
    @CI("phone_number") @SN("phone_number") val phoneNumber: String = "",
    @CI("is_whatsapp") @SN("is_whatsapp") val isWhatsapp: Boolean = false,
    @CI("is_telegram") @SN("is_telegram") val isTelegram: Boolean = false,
    @CI("is_enabled") @SN("is_enabled") val isEnabled: Boolean = false,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("description") @SN("description") val description: String? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyPhoneSupport? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("phone_number") val phoneNumber: String,
        @SN("phone_code") val phoneCode: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String?=null
    ) {
        companion object {
            const val NAME = "agency_phone_support_log"
        }

    }
    companion object {
        const val NAME = "agency_phone_support"
    }
}

/**
 * This data class represents email support.
 *
 * @property agencyId The ID of the travel agency.
 * @property email The email address of the travel agency.
 */
@Serializable
@Entity(AgencyEmailSupport.NAME, primaryKeys = ["agency_id", "email"])
data class AgencyEmailSupport(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("email") @SN("email") val email: String = "",
    @CI("is_enabled") @SN("is_enabled") val isEnabled: Boolean = false,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
    @CI("description")@SN("description") val description: String?=null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyEmailSupport? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("email") val email: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String?=null,

    ) {
        companion object {
            const val NAME = "agency_email_support_log"
        }
    }

    companion object {
        const val NAME = "agency_email_support"
    }
}

/**
 * This data class represents social media support.
 *
 * @property agencyId The ID of the travel agency.
 * @property websiteUrl The website URL of the travel agency.
 * @property facebookUrl The Facebook URL of the travel agency.
 * @property youtubeUrl The YouTube URL of the travel agency.
 * @property xUrl The URL of the X social media platform.
 * @property redditUrl The Reddit URL of the travel agency.
 * @property snapchatUrl The Snapchat URL of the travel agency.
 * @property instagramUrl The Instagram URL of the travel agency.
 */
@Serializable
@Entity(AgencySocialSupport.NAME, primaryKeys = ["agency_id"])
data class AgencySocialSupport(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("website_url") @SN("website_url") val websiteUrl: String? = null,
    @CI("facebook_url") @SN("facebook_url") val facebookUrl: String? = null,
    @CI("youtube_url") @SN("youtube_url") val youtubeUrl: String? = null,
    @CI("x_url") @SN("x_url") val xUrl: String? = null,
    @CI("reddit_url") @SN("reddit_url") val redditUrl: String? = null,
    @CI("snapchat_url") @SN("snapchat_url") val snapchatUrl: String? = null,
    @CI("instagram_url") @SN("instagram_url") val instagramUrl: String? = null,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencySocialSupport? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String?=null
    ) {
        companion object {
            const val NAME = "agency_social_support_log"
        }
    }

    companion object {
        const val NAME = "agency_social_support"
    }
}


