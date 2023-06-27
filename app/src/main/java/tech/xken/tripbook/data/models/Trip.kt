package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = TripSchedule.TABLE_NAME)
data class TripSchedule(
    @ColumnInfo(name = TRIP) @PrimaryKey val trip: String,
    @ColumnInfo(name = DEPARTURE_TIME) val departureTime: Long?,
    @ColumnInfo(name = DEPARTURE_DATE) val departureDate: Long?,
    @ColumnInfo(name = DURATION) val duration: Long?,
) {
    companion object {
        const val TABLE_NAME = "Trip_Schedules"
        const val TRIP = "trip"
        const val DEPARTURE_TIME = "departure_time"
        const val DEPARTURE_DATE = "departure_date"
        const val DURATION = "duration"
    }
}

@Entity(tableName = Trip.TABLE_NAME)
data class Trip(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = AGENCY) val agency: String?,
    @ColumnInfo(name = TOWN1) val town1: String?,
    @ColumnInfo(name = TOWN2) val town2: String?,
    @ColumnInfo(name = ROAD) val road: String?,
    @ColumnInfo(name = NORMAL_PRICE) val normalPrice: Double?,
    @ColumnInfo(name = VIP_PRICE) val vipPrice: Double?,
) {
    companion object {
        const val TABLE_NAME = "Trips"
        const val ID = "id"
        const val AGENCY = "agency"
        const val TOWN1 = "town1"
        const val TOWN2 = "town2"
        const val ROAD = "road"
        const val NORMAL_PRICE = "normal_price"
        const val VIP_PRICE = "vip_price"
    }
}
