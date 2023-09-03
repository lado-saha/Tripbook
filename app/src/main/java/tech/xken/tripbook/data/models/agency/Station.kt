package tech.xken.tripbook.data.models.agency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import tech.xken.tripbook.data.models.NEW_ID


data class Station(
    val stationId: String,
    val name: String,
    val agencyId: String,
    val lat: Double,
    val lon: Double,
    val addedOn: Instant,
    val locationDescription: String,
    val aboutUs: String
)

data class StationTownMap(
    val stationId: String,
    val townId: String,
    val addedOn: Instant,
)

/**
 * Attributes a scanner to a station
 */

data class StationScannerMap(
    val stationId: String,
    val scannerId: String,
    val jobId: String,
    val addedOn: Instant,
)