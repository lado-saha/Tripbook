package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = Station.TABLE_NAME)
data class Station(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = NAME) val name: String?,
    @ColumnInfo(name = AGENCY) val agencyID: String?,
    @ColumnInfo(name = LAT) val lat: Double?,
    @ColumnInfo(name = LON) val lon: Double?,
    @ColumnInfo(name = SUPPORT_PHONE_1) val supportPhone1: String?,
    @ColumnInfo(name = SUPPORT_PHONE_2) val supportPhone2: String?,
    @ColumnInfo(name = SUPPORT_PHONE_1_CODE) val supportPhone1Code: String?,
    @ColumnInfo(name = SUPPORT_PHONE_2_CODE) val supportPhone2Code: String?,
    @ColumnInfo(name = SUPPORT_EMAIL_1) val supportEmail1: String?,
    @ColumnInfo(name = SUPPORT_EMAIL_2) val supportEmail2: String?,
    @ColumnInfo(name = PHOTO_URL) val photoUrl: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) {
    companion object {
        const val TABLE_NAME = "Stations"
        const val ID = "id"
        const val NAME = "name"
        const val LAT = "lat"
        const val LON = "lon"
        const val AGENCY = "agency"
        const val SUPPORT_EMAIL_1 = "support_email_1"
        const val SUPPORT_EMAIL_2 = "support_email_2"
        const val SUPPORT_PHONE_1 = "support_phone_1"
        const val SUPPORT_PHONE_2 = "support_phone_2"
        const val SUPPORT_PHONE_1_CODE = "support_phone_1_code"
        const val SUPPORT_PHONE_2_CODE = "support_phone_2_code"
        const val TIMESTAMP = "timestamp"
        const val PHOTO_URL = "photo_url"
        fun new(id: String = NEW_ID) = Station(id, null,null,null, null, null, null, null, null, null, null, null, null)
    }
}

@Entity(tableName = StationTownMap.TABLE_NAME, primaryKeys = [StationTownMap.STATION, StationTownMap.TOWN])
data class StationTownMap(
    @ColumnInfo(name = STATION) val station: String,
    @ColumnInfo(name = TOWN) val town: String,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) {
    companion object {
        const val TABLE_NAME = "StationTownMaps"
        const val STATION = "station"
        const val TOWN = "town"
        const val TIMESTAMP = "timestamp"
    }
}

/**
 * Attributes a scanner to a station
 */
@Entity(tableName = StationScannerMap.TABLE_NAME, primaryKeys = [StationScannerMap.STATION, StationScannerMap.SCANNER, "job"])
data class StationScannerMap(
    @ColumnInfo(name = STATION) val station: String,
    @ColumnInfo(name = SCANNER) val scanner: String,
    val job: String,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) {
    companion object {
        const val TABLE_NAME = "StationScannerMaps"
        const val SCANNER = "scanner"
        const val STATION = "station"
        const val TIMESTAMP = "timestamp"
    }
}