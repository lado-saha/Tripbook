package tech.xken.tripbook.data.models.agency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class represents phone support.
 *
 * @property agencyId The ID of the travel agency.
 * @property phoneCountryCode The country code for the phone number.
 * @property phoneNumber The phone number of the travel agency.
 * @property isWhatsapp A Boolean value that indicates whether this phone number supports WhatsApp.
 * @property isTelegram A Boolean value that indicates whether this phone number supports Telegram.
 */
@Serializable
data class AgencyPhoneSupport(
    val agencyId: String,
    val phoneCountryCode: String,
    val phoneNumber: String,
    val isWhatsapp: Boolean,
    val isTelegram: Boolean,
)

/**
 * This data class represents email support.
 *
 * @property agencyId The ID of the travel agency.
 * @property email The email address of the travel agency.
 */
@Serializable
data class AgencyEmailSupport(
    val agencyId: String,
    val email: String,
)

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
data class AgencySocialSupport(
    val agencyId: String,
    val websiteUrl: String,
    val facebookUrl: String,
    val youtubeUrl: String,
    val xUrl: String,
    val redditUrl: String,
    val snapchatUrl: String,
    val instagramUrl: String,
)


