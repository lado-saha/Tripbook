package tech.xken.tripbook.data.models

import androidx.compose.ui.geometry.Offset
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A chain of towns
 */
@Entity(
    tableName = "road",
    foreignKeys = [
        ForeignKey(
            entity = Town::class,
            parentColumns = ["town_id"],
            childColumns = ["town_1_id"]
        ),
        ForeignKey(
            entity = Town::class,
            parentColumns = ["town_id"],
            childColumns = ["town_2_id"]
        )
    ],
)
@Serializable
data class Road(
    @SerialName("road_id") @ColumnInfo(name = "road_id") @PrimaryKey val roadId: String,
    @SerialName("name") @ColumnInfo(name = "name") val name: String? = null,
    @SerialName("distance") @ColumnInfo(name = "distance") val distance: Double? = null,
    @SerialName("town_1_id") @ColumnInfo(name = "town_1_id") val town1Id: String? = null,
    @SerialName("town_2_id") @ColumnInfo(name = "town_2_id") val town2Id: String? = null,
    @SerialName("added_on") @ColumnInfo(name = "added_on") val addedOn: Instant? = null,
    @SerialName("road_path") @ColumnInfo(name = "road_path") val roadPath: List<String>? = null
) {
    // Contains all towns which make up the road
    @Ignore
    var town1: Town? = null

    @Ignore
    var town2: Town? = null

    @Ignore
    var roadPathTowns: List<Town>? = null

}

/**
 * A [Road] and [TownPair] class mainly used for UI requirements like in the universe editor
 * It contains two [TownNode] id: [start] [stop] in that is the order in which the itinerary is in the [townPairs]
 * map.
 */
data class Itinerary(
    val roadID: String = "",
    val start: String = "",
    val stop: String = "",
    val distance: Double = 0.0,
    val offset: Offset = Offset.Zero,
    val townPairs: HashMap<String, String> = hashMapOf(),
    val isHighlighted: Boolean = false,
) {

    companion object {
        /**
         * This is helper function to create a [Itinerary] from a [List] of [TownPair].
         * @param start the expected departure town
         * @param stop the expected arrival town
         * We use [start] and [stop] to determine the direction of the road. i.e, Our database for redundancy avoidance issues,
         * stores roads in a particular direction usually from the least alphabetical town to the greater
         * alphabetical town. So, if the [start] matches with the first node, it means we need not to reverse the road
         * else we reverse the road
         */
        fun Collection<TownPair>.toItinerary(
            start: String,
            stop: String,
        ): Itinerary {
            // The condition for road reversal
            val isInOrder =
                (find { it.town1 == start } != null) && (find { it.town2 == stop } != null)
            //We return a link object
            return Itinerary(
                roadID = first().roadID,
                start = start,
                stop = stop,
                townPairs = (if (isInOrder) associate { it.town1 to it.town2 } else associate { it.town2 to it.town1 }) as HashMap<String, String>
            )
        }
    }
}