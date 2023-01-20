package tech.xken.tripbook.data.sources.universe.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.data.models.TownPair

@Dao
interface UniverseDao {
    //Lightweight operations to returns
    @Query("SELECT name FROM Towns WHERE id NOT IN (:excludedIds) ORDER BY name ASC")
    fun townNames(excludedIds: List<String>): List<String>

    @Query("SELECT region FROM Towns WHERE id NOT IN (:excludedIds) GROUP BY region ORDER BY region ASC")
    fun townRegionNames(excludedIds: List<String>): List<String>

    @Query("SELECT division FROM Towns WHERE id NOT IN (:excludedIds) GROUP BY division ORDER BY division ASC")
    fun townDivisionsNames(excludedIds: List<String>): List<String>

    //Towns only
    @Query("SELECT * FROM Towns ORDER BY name ASC")
    fun towns(): List<Town>

    @Query("SELECT * FROM Towns WHERE id IN (:ids)")
    fun townsFromIDs(ids: List<String>): List<Town>

    @Query("SELECT * FROM Towns WHERE name IN (:names)")
    fun townsFromNames(names: List<String>): List<Town>

    @Query("SELECT * FROM Towns WHERE (lat BETWEEN :lat + 0.1 AND :lat - 0.1) AND (lon BETWEEN :lon + 0.1 AND :lon - 0.1)")
    fun townsFromGeoPoint(lat: Double, lon: Double): List<Town>

    @Query("SELECT * FROM Towns WHERE region = :region")
    fun townsFromRegion(region: String): List<Town>

    @Query("SELECT * FROM Towns WHERE division = :division")
    fun townsFromDivision(division: String): List<Town>

    @Query("SELECT * FROM Towns WHERE region = :subdivision")
    fun townsFromSubDivision(subdivision: String): List<Town>

    @Insert(entity = Town::class, onConflict = REPLACE)
    fun saveTowns(towns: List<Town>)

    //Roads only
    @Insert
    fun saveRoad(roads: List<Road>)

    @Query("SELECT * FROM Roads")
    fun roads(): List<Road>

    @Query("SELECT * FROM Roads WHERE id IN (:ids)")
    fun roadFromIds(ids: String): List<Road>

    //Composite Queries
    @Query("SELECT * FROM Roads WHERE (town1 OR town2 = :id1) AND (town2 OR town1 = :id2)")
    fun roadBtn2TownsFromIds(id1: String, id2: String): Road

    @Query("SELECT id FROM Towns WHERE name IN (:names)")
    fun townIdsFromNames(names: List<String>): List<String>

    @Query("SELECT * FROM Roads WHERE town1 OR town2 = :id")
    fun roadsFromOrToTownById(id: String): List<Road>

    //Itinerary
    @Insert
    fun saveItinerary(links: List<TownPair>)

    @Query("SELECT * FROM Itineraries WHERE road_id = :id")
    fun townPairsOfRoadFromId(id: String): List<TownPair>
}