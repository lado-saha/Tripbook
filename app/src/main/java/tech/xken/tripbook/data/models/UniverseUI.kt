package tech.xken.tripbook.data.models

import androidx.compose.ui.geometry.Offset
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


/**
 * A [Town] class mainly used for UI requirements like in the Universe editor
 */
data class TownNode(
    val town: Town,
    val offset: Offset = Offset.Zero,
    val timestamp: Long = Date().time,
    val pinPoint: Offset = Offset(0f, 0f),
)

@Serializable
@Entity(tableName = "country")
data class Country(
    @SerialName("country_id") @ColumnInfo(name = "country_id") @PrimaryKey val countryId: String,
    @SerialName("name") @ColumnInfo(name = "name") val name: String?,
    @SerialName("added_on") @ColumnInfo(name = "added_on") val addedOn: Long?
)

@Serializable
@Entity(tableName = "region")
data class Region(
    @SerialName("region_id") @ColumnInfo(name = "region_id") @PrimaryKey val regionId: String,
    @SerialName("name") @ColumnInfo(name = "name") val name: String?,
    @SerialName("country_id") @ColumnInfo(name = "country_id") val countryId: String?,
    @SerialName("added_on") @ColumnInfo(name = "added_on") val addedOn: Long?,
) {
    @Ignore
    lateinit var country: Country

}

@Serializable
@Entity(tableName = "division")
data class Division(
    @SerialName("division_id") @ColumnInfo(name = "division_id") @PrimaryKey val divisionId: String,
    @SerialName("name") @ColumnInfo(name = "name") val name: String?,
    @SerialName("region_id") @ColumnInfo(name = "region_id") val regionId: String?,
    @SerialName("added_on") @ColumnInfo(name = "added_on") val addedOn: Long?
) {
    @Ignore
    lateinit var region: Region

}

@Serializable
@Entity(tableName = "subdivision")
data class Subdivision(
    @SerialName("subdivision_id") @ColumnInfo(name = "subdivision_id") @PrimaryKey val subdivisionId: String,
    @SerialName("name") @ColumnInfo(name = "name") val name: String?,
    @SerialName("division_id") @ColumnInfo(name = "division_id") val divisionId: String?,
    @SerialName("added_on") @ColumnInfo(name = "added_on") val addedOn: Long?,
) {
    @Ignore
    lateinit var division: Division

}

@Entity(tableName = "town")
@Serializable
data class Town(
    @SerialName("town_id") @ColumnInfo(name = "town_id") @PrimaryKey val townId: String,
    @SerialName("name") @ColumnInfo(name = "name") val name: String?,
    @SerialName("subdivision_id") @ColumnInfo(name = "subdivision_id") val subdivisionId: String?,
    @Serializable(GeoPointAsStringSerializer::class) @SerialName("geo_point") @ColumnInfo(name = "geo_point") val geoPoint: GeoPoint?,
    @SerialName("added_on") @ColumnInfo(name = "added_on") val addedOn: Instant? = null,
) {
    @Ignore
    var subdivision: Subdivision? = null
}