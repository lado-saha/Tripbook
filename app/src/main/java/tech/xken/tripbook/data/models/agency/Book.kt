package tech.xken.tripbook.data.models.agency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


data class Book(
    val id: String,
    val qrToken: String?,
    val booker: String?,
    val tripSchedule: String?,
    val isVip: Boolean?,
    val bus: String?,
    val seatNumber: String?,
    val isExpired: Boolean?,
    val scannedBy: String?,
    val scannedOn: Long?,
)
