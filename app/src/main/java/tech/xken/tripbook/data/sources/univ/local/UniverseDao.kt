//package tech.xken.tripbook.data.sources.univ.local
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy.Companion.REPLACE
//import androidx.room.Query
//import tech.xken.tripbook.data.models.*
//
//@Dao
//interface UniverseDao {
//    //Lightweight operations to returns
////    @Query("SELECT * FROM town WHERE (geo_point BETWEEN :lat + 0.1 AND :lat - 0.1) AND (lon BETWEEN :lon + 0.1 AND :lon - 0.1)")
////    fun townsFromGeoPoint(lat: Double, lon: Double): List<Town>
//
//    //road only
//    @Insert
//    fun saveRoad(roads: List<Road>)
//
//    @Query("SELECT * FROM road")
//    fun roads(): List<Road>
//
//    @Query("SELECT * FROM road WHERE road_id IN (:ids)")
//    fun roadFromIds(ids: String): List<Road>
//
//    //Composite Queries
//    @Query("SELECT * FROM road WHERE (town_1_id OR town_2_id = :id1) AND (town_2_id OR town_1_id = :id2)")
//    fun roadBtn2TownsFromIds(id1: String, id2: String): Road
//
//    @Query("SELECT town_id FROM town WHERE name IN (:names)")
//    fun townIdsFromNames(names: List<String>): List<String>
//
//    @Query("SELECT * FROM road WHERE town_1_id OR town_2_id = :id")
//    fun roadsFromOrToTownById(id: String): List<Road>
//
//    //Itinerary
//    @Insert
//    fun saveItinerary(links: List<TownPair>)
//
//    @Query("SELECT * FROM Itineraries WHERE road_id = :id")
//    fun townPairsOfRoadFromId(id: String): List<TownPair>
//
//
//    @Insert(onConflict = REPLACE)
//    fun saveCountries(countries: List<Country>)
//
//    @Insert(onConflict = REPLACE)
//    fun saveRegions(regions: List<Region>)
//
//    @Insert(onConflict = REPLACE)
//    fun saveDivisions(divisions: List<Division>)
//
//    @Insert(onConflict = REPLACE)
//    fun saveSubdivisions(subdivisions: List<Subdivision>)
//
//    @Insert(onConflict = REPLACE)
//    fun saveTowns(towns: List<Town>)
//
//
//    //Getting country
//    @Query("SELECT * FROM country")
//    fun countries(): List<Country>
//
//    @Query("SELECT * FROM country WHERE name IN (:names)")
//    fun countriesFromNames(names: List<String>): List<Country>
//
//    @Query("SELECT * FROM country WHERE country_id IN (:ids)")
//    fun countriesFromIds(ids: List<String>): List<Country>
//
//    //Getting region
//    @Query("SELECT * FROM region")
//    fun regions(): List<Region>
//
//    @Query("SELECT * FROM region WHERE name IN (:names)")
//    fun regionsFromNames(names: List<String>): List<Region>
//
//    @Query("SELECT * FROM region WHERE region_id IN (:ids)")
//    fun regionsFromIds(ids: List<String>): List<Region>
//
//    //Getting division
//    @Query("SELECT * FROM division")
//    fun divisions(): List<Division>
//
//    @Query("SELECT * FROM division WHERE name IN (:names)")
//    fun divisionsFromNames(names: List<String>): List<Division>
//
//    @Query("SELECT * FROM division WHERE division_id IN (:ids)")
//    fun divisionsFromIds(ids: List<String>): List<Division>
//
//    //Getting subdivision
//    @Query("SELECT * FROM subdivision")
//    fun subdivisions(): List<Subdivision>
//
//    @Query("SELECT * FROM subdivision WHERE name IN (:names)")
//    fun subdivisionsFromNames(names: List<String>): List<Subdivision>
//
//    @Query("SELECT * FROM subdivision WHERE subdivision_id IN (:ids)")
//    fun subdivisionsFromIds(ids: List<String>): List<Subdivision>
//
//    //Getting town
//    @Query("SELECT * FROM town")
//    fun towns(): List<Town>
//
//    @Query("SELECT * FROM town WHERE name IN (:names)")
//    fun townsFromNames(names: List<String>): List<Town>
//
//    @Query("SELECT * FROM town WHERE town_id IN (:ids)")
//    fun townsFromIds(ids: List<String>): List<Town>
//
//    @Query("SELECT * FROM town WHERE subdivision_id IN (:ids)")
//    suspend fun townsFromSubdivisions(ids: List<String>): List<Town>
//
//    @Query("SELECT * FROM subdivision WHERE division_id IN (:ids)")
//    suspend fun subdivisionsFromDivisions(ids: List<String>): List<Subdivision>
//
//    @Query("SELECT * FROM division WHERE region_id IN (:ids)")
//    suspend fun divisionsFromRegions(ids: List<String>): List<Division>
//
//    @Query("SELECT * FROM region WHERE country_id IN (:ids)")
//    suspend fun regionsFromCountry(ids: List<String>): List<Region>
//
//
//}