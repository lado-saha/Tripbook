package tech.xken.tripbook.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import tech.xken.tripbook.data.models.TownPair.Companion.TOWN_1
import tech.xken.tripbook.data.models.TownPair.Companion.TOWN_2
import tech.xken.tripbook.data.models.TownPair.Companion.ROAD_ID

@Entity(
    tableName = TownPair.TABLE_NAME,
    primaryKeys = [ROAD_ID, TOWN_1, TOWN_2],
//    foreignKeys = [
//        ForeignKey(
//            entity = Road::class,
//            parentColumns = [Road.ID],
//            childColumns = [ROAD_ID],
//        ), ForeignKey(
//            entity = Town::class,
//            parentColumns = [Town.ID, Town.ID],
//            childColumns = [RoadLinkEntity.NODE_1, RoadLinkEntity.NODE_2],
//       )
//    ]
)
data class TownPair(
    @ColumnInfo(name = ROAD_ID) val roadID: String,
    @ColumnInfo(name = TOWN_1) val town1: String,
    @ColumnInfo(name = TOWN_2) val town2: String,
) {
    companion object {
        /**
         * From a list of towns, we create the list of [TownPair]. i.e the i-th town is node1 and the
         * node2 is the (i+1)-th town for a single entity, and then next, the i+1 is connected to i+2 and so on till
         * we reach the second to the last connected to the last and then we stop
         */
        @Ignore
        fun townPairsFromTownsIds(
            townIds: List<String>,
            roadID: String,
        ): List<TownPair> {
            return (0..townIds.size - 2).map { i ->
                TownPair(roadID = roadID, town1 = townIds[i], town2 = townIds[i + 1])
            }
        }

        @Ignore
        const val TABLE_NAME = "Itineraries"
        @Ignore
        const val ROAD_ID = "road_id"
        @Ignore
        const val TOWN_1 = "town1"
        @Ignore
        const val TOWN_2 = "town2"
    }
}

