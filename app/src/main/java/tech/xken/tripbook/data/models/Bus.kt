package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Bus.TABLE_NAME)
data class Bus(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = CAPACITY) val capacity: Int?,
    @ColumnInfo(name = MODEL) val model: String?,
    @ColumnInfo(name = PHOTO_URL) val photo_url: String?,
    @ColumnInfo(name = BUS_COUNT) val busCount: Long?,
) {
    companion object {
        const val TABLE_NAME = "Buses"
        const val ID = "id"
        const val CAPACITY = "capacity"
        const val MODEL = "model"
        const val PHOTO_URL = "photo_url"
        const val BUS_COUNT = "bus_count"
    }
}

