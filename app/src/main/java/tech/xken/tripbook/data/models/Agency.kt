package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = AgencyProfile.TABLE_NAME)
data class AgencyProfile(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = NAME) val name: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = CREATED_ON) val createdOn: Long?,
    @ColumnInfo(name = CREATED_BY) val createdBy: String?, //No need since the owner field in Scanner knows
    @ColumnInfo(name = LOGO_URL) val logoUrl: String?,
    @ColumnInfo(name = MOTTO) val motto: String?,
) {
    companion object {
        const val TABLE_NAME = "AgencyProfiles"
        const val ID = "id"
        const val NAME = "name"
        const val TIMESTAMP = "timestamp"
        const val CREATED_ON = "created_on"
        const val CREATED_BY = "created_by"
        const val LOGO_URL = "logo_url"
        const val MOTTO = "motto"
    }
}

@Entity(tableName = AgencyMetadata.TABLE_NAME)
data class AgencyMetadata(
    @ColumnInfo(name = AGENCY) @PrimaryKey val agency: String,
    @ColumnInfo(name = IS_SUSPENDED) val isSuspended: Boolean?,
    @ColumnInfo(name = REPUTATION) val reputation: Float?,
    @ColumnInfo(name = ACCIDENT_COUNT) val accidentCount: Int?,
) {
    companion object {
        const val TABLE_NAME = "Agency_Metadata"
        const val AGENCY = "agency"
        const val IS_SUSPENDED = "is_suspended"
        const val REPUTATION = "reputation"
        const val ACCIDENT_COUNT = "accident_count"
    }
}

@Entity()
data class AgencyFinance(
    @ColumnInfo(name = ID) val id: String,
) {
    companion object {
        const val ID = "id"
        const val AGENCY = "agency"
        const val MOMO_NUMBER_1 = "momo_number_1"
        const val OM_NUMBER_1 = "om_number_1"
        const val MOMO_NUMBER_2 = "momo_number_2"
        const val OM_NUMBER_2 = "om_number_2"
        const val BANK_NUMBER_1 = "bank_number_1"
        const val BANK_CARRIER_1 = "bank_carrier_1"
        const val BANK_CARRIER_2 = "bank_carrier_2"
        const val BANK_NUMBER_2 = "bank_number_3"
    }
}

@Entity(tableName = AgencySettings.TABLE_NAME)
data class AgencySettings(
    @ColumnInfo(name = AGENCY) @PrimaryKey val agency: String?,
    @ColumnInfo(name = COST_PER_KM) val costPerKm: Double?,
    @ColumnInfo(name = VIP_COST_PER_KM) val vipCostPerKm: Double?,
) {
    companion object {
        const val TABLE_NAME = "table_name"
        const val AGENCY = "agency"
        const val COST_PER_KM = "cost_per_km"
        const val VIP_COST_PER_KM = "vip_cost_per_km"
    }
}

@Entity(tableName = AgencyEvent.TABLE_NAME)
data class AgencyEvent(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = START_TIME) val startTime: Long?,
    @ColumnInfo(name = END_TIME) val endTime: Long?,
    @ColumnInfo(name = EXPECTED_DURATION) val expectedDuration: Long?,
    @ColumnInfo(name = PURPOSE) val purpose: String?,
) {
    companion object {
        const val TABLE_NAME = "Agency_Event"
        const val ID = "id"
        const val TIMESTAMP = "timestamp"
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val EXPECTED_DURATION = "expected_duration"
        const val PURPOSE = "purpose"
    }
}

//A vi
@Entity(tableName = "AgencyProfileStats")
data class AgencyProfileStats(
     val agencyId: String,
     val personnelCount: Int,
     val busCount: Int,
     val busCountCount: Int,
     val stationCount: Int,
     val supportEmailCount: Int,
     val supportPhoneCount: Int,
)

@Entity(tableName = "StationStatistics")
data class StationProfileStats(
    val stationId: String,
    val affiliatedTownCount: Int,
    val personnelCount: Int,
    val busCount: Int,
    val stationCount: Int,
)

/**
 * Profile
 * Vehicle
 * Support Contacts
 * Stations
 * Towns
 * Jobs & Salaries
 * Locations
 * Trips
 * Personnel
 * Other
 * */