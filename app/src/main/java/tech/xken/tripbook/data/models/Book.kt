package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Book.TABLE_NAME)
data class Book(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = QR_TOKEN) val qrToken: String?,
    @ColumnInfo(name = BOOKER) val booker: String?,
    @ColumnInfo(name = TRIP_SCHEDULE) val tripSchedule: String?,
    @ColumnInfo(name = IS_VIP) val isVip: Boolean?,
    @ColumnInfo(name = BUS) val bus: String?,
    @ColumnInfo(name = SEAT_NUMBER) val seatNumber: String?,
    @ColumnInfo(name = IS_EXPIRED) val isExpired: Boolean?,
    @ColumnInfo(name = SCANNED_BY) val scannedBy: String?,
    @ColumnInfo(name = SCANNED_ON) val scannedOn: Long?,
) {
    companion object {
        const val TABLE_NAME = "Books"
        const val ID = "id"
        const val QR_TOKEN = "qr_token"
        const val BOOKER = "booker"
        const val SEAT_NUMBER = "seat_number"
        const val TRIP_SCHEDULE = "trip_schedule"
        const val IS_VIP = "is_vip"
        const val BUS = "bus"
        const val IS_EXPIRED = "is_expired"
        const val SCANNED_BY = "scanned_by"
        const val SCANNED_ON = "scanned_on"
    }
}
