package tech.xken.tripbook.data.models.agency

import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import tech.xken.tripbook.data.models.DbAction

@Serializable
@Entity(AgencyMoMoAccount.NAME, primaryKeys = ["agency_id", "phone_number"])
data class AgencyMoMoAccount(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("phone_number") @SN("phone_number") val phoneNumber: String = "",
    @CI("description") @SN("description") val description: String = "",
    @CI("is_enabled") @SN("is_enabled") val isEnabled: Boolean = true,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("data_json") val data: AgencyMoMoAccount? = null,
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("phone_number") val phoneNumber: String,
        @SN("timestamp") val timestamp: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String
    ) {
        companion object {
            const val NAME = "agency_momo_account_log"
        }

    }

    companion object {
        const val NAME = "agency_momo_account"
    }
}

@Serializable
@Entity(AgencyOMAccount.NAME, primaryKeys = ["agency_id", "phone_number"])
data class AgencyOMAccount(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("phone_number") @SN("phone_number") val phoneNumber: String = "",
    @CI("description") @SN("description") val description: String = "",
    @CI("is_enabled") @SN("is_enabled") val isEnabled: Boolean = true,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null,
) {
    @Serializable
    data class Log(
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("phone_number") val phoneNumber: String,
        @SN("timestamp") val timestamp: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String,
        @SN("data_json") val data: AgencyOMAccount? = null,
    ) {
        companion object {
            const val NAME = "agency_om_account_log"
        }

    }

    companion object {
        const val NAME = "agency_om_account"
    }
}

//@Serializable
//data class AgencyCardPaymentMethod(
//    @SerialName("agency_id") val agencyId: String,
//    @SerialName("credit_card_num") val creditCardNumber: String = "",
//    @SerialName("description") val description: String = "",
//    @SerialName("active") val isEnabled: Boolean
//)

