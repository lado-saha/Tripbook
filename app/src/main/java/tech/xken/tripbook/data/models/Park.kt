package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = Park.TABLE_NAME)
data class Park(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = AGENCY) val agencyID: String?,
    @ColumnInfo(name = TOWN) val town: String?,
    @ColumnInfo(name = LAT) val lat: Double?,
    @ColumnInfo(name = LON) val lon: Double?,
    @ColumnInfo(name = SUPPORT_PHONE_1) val supportPhone1: String?,
    @ColumnInfo(name = SUPPORT_PHONE_2) val supportPhone2: String?,
    @ColumnInfo(name = SUPPORT_EMAIL_1) val supportEmail1: String?,
    @ColumnInfo(name = SUPPORT_EMAIL_2) val supportEmail2: String?,
) {
    companion object {
        const val TABLE_NAME = "Parks"
        const val ID = "id"
        const val LAT = "lat"
        const val LON = "lon"
        const val AGENCY = "agency"
        const val TOWN = "town"
        const val SUPPORT_EMAIL_1 = "support_email_1"
        const val SUPPORT_EMAIL_2 = "support_email_2"
        const val SUPPORT_PHONE_1 = "support_phone_1"
        const val SUPPORT_PHONE_2 = "support_phone_2"
    }
}
