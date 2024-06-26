package tech.xken.tripbook.data.models.agency


import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
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
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null
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
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null
) {
    @Serializable
    data class Log(
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("phone_number") val phoneNumber: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null,
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

@Serializable
@Entity(AgencyPayPalAccount.NAME, primaryKeys = ["agency_id", "email"])
data class AgencyPayPalAccount(
    @CI("agency_id") @SN("agency_id") val agencyId: String = "",
    @CI("email") @SN("email") val email: String = "",
    @CI("description") @SN("description") val description: String = "",
    @CI("is_enabled") @SN("is_enabled") val isEnabled: Boolean = true,
    @CI("added_on") @SN("added_on") val addedOn: Instant? = null,
    @CI("modified_on") @SN("modified_on") val modifiedOn: Instant? = null
) {
    @Serializable
    data class Log(
        @SN("log_id") val logId: Long,
        @SN("agency_id") val agencyId: String,
        @SN("email") val email: String,
        @SN("added_on") val addedOn: Instant,
        @SN("db_action") val dbAction: DbAction,
        @SN("scanner_id") val scannerId: String? = null,
        @SN("data_json") val data: AgencyPayPalAccount? = null,
    ) {
        companion object {
            const val NAME = "agency_paypal_account_log"
        }
    }

    companion object {
        const val NAME = "agency_paypal_account"
    }
}


//@Serializable

