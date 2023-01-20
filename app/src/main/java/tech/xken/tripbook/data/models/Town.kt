package tech.xken.tripbook.data.models

import androidx.compose.ui.geometry.Offset
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.xken.tripbook.data.models.Town.Companion.TABLE_NAME
import java.util.*

/**
 * A town is a unit place
 */
@Entity(tableName = TABLE_NAME)
data class Town(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = NAME) val name: String?,
    @ColumnInfo(name = LAT) val lat: Double?,
    @ColumnInfo(name = LON) val lon: Double?,
    @ColumnInfo(name = SUBDIVISION) val subdivision: String?,
    @ColumnInfo(name = DIVISION) val division: String?,
    @ColumnInfo(name = REGION) val region: String?,
    @ColumnInfo(name = COUNTRY) val country: String?,
    @ColumnInfo(name = XM) val xm: Double?,
    @ColumnInfo(name = YM) val ym: Double?,
    @ColumnInfo(name = IS_COMPLETE) val isComplete: Boolean?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) {

    companion object {
        const val TABLE_NAME = "Towns"
        const val ID = "id"
        const val IS_COMPLETE = "is_complete"
        const val NAME = "name"
        const val LAT = "lat"
        const val LON = "lon"
        const val SUBDIVISION = "subdivision"
        const val DIVISION = "division"
        const val REGION = "region"
        const val COUNTRY = "country"
        const val XM = "xm"
        const val YM = "ym"
        const val TIMESTAMP = "timestamp"
        const val all = "*"
    }
}

/**
 * A [Town] class mainly used for UI requirements like in the Universe editor
 */
data class TownNode(
    val town: Town,
    val offset: Offset = Offset.Zero,
    val timestamp: Long = Date().time,
    val pinPoint: Offset = Offset(0f, 0f))
