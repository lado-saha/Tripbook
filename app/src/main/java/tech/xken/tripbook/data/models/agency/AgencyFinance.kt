package tech.xken.tripbook.data.models.agency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgencyMoMoPaymentMethod(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("phone_number_momo") val phoneNumberMoMo: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class AgencyOMPaymentMethod(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("phone_number_om") val phoneNumberOM: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class AgencyCardPaymentMethod(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("credit_card_num") val creditCardNumber: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("active") val active: Boolean,
)

