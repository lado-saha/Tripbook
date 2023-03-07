package tech.xken.tripbook.data.sources.agency.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import tech.xken.tripbook.data.models.Park
import tech.xken.tripbook.data.models.TownParkMap

@Dao
interface AgencyDao {
    /**
     * Parks
     */
    @Insert(onConflict = REPLACE)
    fun savePark(park: Park)

    @Query("SELECT * FROM TownParkMaps WHERE town = :town")
    fun townParkLinksFromTown(town: String): List<TownParkMap>

    @Query("SELECT * FROM TownParkMaps WHERE town = :park")
    fun townParkLinksFromPark(park: String): List<TownParkMap>

    @Query("SELECT * FROM Parks WHERE id IN (:ids)")
    fun parksFromIds(ids: List<String>): List<Park>

    @Query("SELECT * FROM Parks")
    fun parks(): List<Park>
}