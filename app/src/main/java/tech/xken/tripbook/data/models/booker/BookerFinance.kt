package tech.xken.tripbook.data.models.booker

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "booker_momo_account", primaryKeys = ["booker_id", "phone_number"])
data class BookerMoMoAccount(
    @ColumnInfo("booker_id") @SerialName("booker_id") val bookerId: String = "",
    @ColumnInfo("phone_number") @SerialName("phone_number") val phoneNumber: String = "",
    @ColumnInfo("name") @SerialName("name") val name: String? = null,
    @ColumnInfo("is_active") @SerialName("is_active") val isActive: Boolean = true,
    @ColumnInfo("added_on") @SerialName("added_on") val addedOn: Instant = Clock.System.now()
)

@Serializable
@Entity(tableName = "booker_om_account", primaryKeys = ["booker_id", "phone_number"])
data class BookerOMAccount(
    @ColumnInfo("booker_id") @SerialName("booker_id") val bookerId: String = "",
    @ColumnInfo("phone_number") @SerialName("phone_number") val phoneNumber: String = "",
    @ColumnInfo("name") @SerialName("name") val name: String? = null,
    @ColumnInfo("is_active") @SerialName("is_active") val isActive: Boolean = true,
    @ColumnInfo("added_on") @SerialName("added_on") val addedOn: Instant = Clock.System.now()
)



