package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = AgencyEditionRecord.TABLE_NAME)
data class AgencyEditionRecord(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = EDITOR)  val editor: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = EDITION_DETAILS) val editionDetails: String?,
) {
    companion object {
        const val TABLE_NAME = "Agency_Edition_Records"
        const val ID = "id"
        const val TIMESTAMP = "timestamp"
        const val EDITION_DETAILS = "EDITOR_DETAILS"
        const val EDITOR = "EDITOR"
    }
}

@Entity(tableName = TripRecord.TABLE_NAME)
data class TripRecord(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = TRIP_SCHEDULE) val tripSchedule: String?,
    @ColumnInfo(name = DRIVER) val driver: String?,
    @ColumnInfo(name = BUS) val bus: String?,
    @ColumnInfo(name = BOOKS_COUNT) val booksCount: Int?,
    @ColumnInfo(name = SCANNED_BOOKS_COUNT) val scannedBooksCount: Int?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
    @ColumnInfo(name = COMPLETION_TIME) val completionTime: Long?,
    @ColumnInfo(name = TOTAL_PROFIT) val totalProfit: Double,
) {
    companion object {
        const val TABLE_NAME = "Trip_Records"
        const val ID = "id"
        const val BUS = "bus"
        const val BOOKS_COUNT = "book_count"
        const val SCANNED_BOOKS_COUNT = "scanned_book_count"
        const val DRIVER = "scanner"
        const val COMPLETION_TIME = "completion_time"
        const val TRIP_SCHEDULE = "trip_schedule"
        const val TOTAL_PROFIT = "total_profit"
        const val TIMESTAMP = "timestamp"
    }
}

@Entity(tableName = BookScanRecord.TABLE_NAME)
data class BookScanRecord(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = BOOK) val booker: String?,
    @ColumnInfo(name = SCANNER) val scanner: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) {
    companion object {
        const val TABLE_NAME = "Scan_Records"
        const val ID = "id"
        const val BOOK = "book"
        const val SCANNER = "scanner"
        const val TIMESTAMP = "timestamp"
    }
}

