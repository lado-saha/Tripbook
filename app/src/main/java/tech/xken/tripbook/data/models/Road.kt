package tech.xken.tripbook.data.models

import androidx.compose.ui.geometry.Offset
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * A chain of towns
 */
@Entity(
    tableName = Road.TABLE_NAME,
//    foreignKeys = [
//        ForeignKey(
//            entity = Town::class,
//            parentColumns = [Town.ID, Town.ID],
//            childColumns = [Road.TOWN_1, Road.TOWN_2]
//        )
//    ]
)
data class Road(
    @ColumnInfo(name = ID) @PrimaryKey val id: String,
    @ColumnInfo(name = NAME) val name: String?,
    @ColumnInfo(name = DISTANCE) val distance: Double?,
    @ColumnInfo(name = SHAPE) val shape: String?,
    @ColumnInfo(name = TOWN_1) val town1: String?,
    @ColumnInfo(name = TOWN_2) val town2: String?,
    @ColumnInfo(name = TIMESTAMP) val timestamp: Long?,
) {
    // Contains all towns which make up the road
    @Ignore
    var itineraryTownsIds: List<String> = listOf()

    companion object {
        @Ignore
        const val TABLE_NAME = "Roads"
        @Ignore
        const val ID = "id"
        @Ignore
        const val NAME = "name"
        @Ignore
        const val DISTANCE = "distance"
        @Ignore
        const val SHAPE = "shape"
        @Ignore
        const val TOWN_1 = "town1"
        @Ignore
        const val TOWN_2 = "town2"
        @Ignore
        const val TIMESTAMP = "timestamp"
    }
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
    /**
     * We traverse like a Linked List i.e we use the previous element to get the next until
     * there no next
     */
    val townIds = run {
        //Managing reversal cases
        var prev = start
        val itinerary = mutableListOf(prev)
        while (prev != stop) {
            val next = townPairs[prev]!!
            itinerary += next
            prev = next
        }
        itinerary
    }

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