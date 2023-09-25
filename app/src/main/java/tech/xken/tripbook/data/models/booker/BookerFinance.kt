package tech.xken.tripbook.data.models.booker

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.SortField


@Serializable
@Entity(tableName = BookerMoMoAccount.NAME, primaryKeys = ["booker_id", "phone_number"])
data class BookerMoMoAccount(
    @ColumnInfo("booker_id") @SerialName("booker_id") val bookerId: String = "",
    @ColumnInfo("phone_number") @SerialName("phone_number") val phoneNumber: String = "",
    @ColumnInfo("is_enabled") @SerialName("is_enabled") val isEnabled: Boolean = true,
    @ColumnInfo("added_on") @SerialName("added_on") val addedOn: Instant? = null,
    @ColumnInfo("modified_on") @SerialName("modified_on") val modifiedOn: Instant? = null
) {
    companion object {
        const val NAME = "booker_momo_account"
        val sortFields
            get() = listOf(
                SortField(R.string.lb_phone),
                SortField(R.string.lb_sort_enabled),
                SortField(R.string.lb_sort_modified_on),
                SortField(R.string.lb_sort_added_on)
            )

        val searchFields
            get() = listOf(
                SortField(R.string.lb_phone),
//                SortField(R.string.lb_sort_enabled),
//                SortField(R.string.lb_sort_modified_on),
//                SortField(R.string.lb_sort_added_on)
            )
    }

    fun backingField(nameRes: Int) = when (nameRes) {
        R.string.lb_phone -> phoneNumber
        R.string.lb_sort_enabled -> if(isEnabled) "a" else "b"
        R.string.lb_sort_modified_on -> "${modifiedOn?.epochSeconds}"
        R.string.lb_sort_added_on -> "${addedOn?.epochSeconds}"
        else -> ""
    }
}

@Serializable
@Entity(tableName = BookerOMAccount.NAME, primaryKeys = ["booker_id", "phone_number"])
data class BookerOMAccount(
    @ColumnInfo("booker_id") @SerialName("booker_id") val bookerId: String = "",
    @ColumnInfo("phone_number") @SerialName("phone_number") val phoneNumber: String = "",
    @ColumnInfo("is_enabled") @SerialName("is_enabled") val isEnabled: Boolean = true,
    @ColumnInfo("added_on") @SerialName("added_on") val addedOn: Instant? = null,
    @ColumnInfo("modified_on") @SerialName("modified_on") val modifiedOn: Instant? = null
) {
    companion object {
        const val NAME = "booker_om_account"
        val sortFields
            get() = listOf(
                SortField(R.string.lb_phone),
                SortField(R.string.lb_sort_enabled),
                SortField(R.string.lb_sort_modified_on),
                SortField(R.string.lb_sort_added_on)
            )

        val searchFields
            get() = listOf(
                SortField(R.string.lb_phone),
//                SortField(R.string.lb_sort_enabled),
//                SortField(R.string.lb_sort_modified_on),
//                SortField(R.string.lb_sort_added_on)
            )
    }

    fun backingField(nameRes: Int): String = when (nameRes) {
        R.string.lb_phone -> phoneNumber
        R.string.lb_sort_enabled -> if (isEnabled) "a" else "b"
        R.string.lb_sort_modified_on -> "${modifiedOn?.epochSeconds}"
        R.string.lb_sort_added_on -> "${addedOn?.epochSeconds}"
        else -> ""
    }
}



