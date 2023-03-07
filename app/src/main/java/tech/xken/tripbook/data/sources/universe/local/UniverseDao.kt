package tech.xken.tripbook.data.sources.universe.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import tech.xken.tripbook.data.models.*

@Dao
interface UniverseDao {
    //Lightweight operations to returns
    @Query("SELECT * FROM Towns WHERE (lat BETWEEN :lat + 0.1 AND :lat - 0.1) AND (lon BETWEEN :lon + 0.1 AND :lon - 0.1)")
    fun townsFromGeoPoint(lat: Double, lon: Double): List<Town>

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

    @Insert(onConflict = REPLACE)
    fun savePlanets(planets: List<Planet>)

    @Insert(onConflict = REPLACE)
    fun saveContinents(continents: List<Continent>)

    @Insert(onConflict = REPLACE)
    fun saveCountries(countries: List<Country>)

    @Insert(onConflict = REPLACE)
    fun saveRegions(regions: List<Region>)

    @Insert(onConflict = REPLACE)
    fun saveDivisions(divisions: List<Division>)

    @Insert(onConflict = REPLACE)
    fun saveSubdivisions(subdivisions: List<Subdivision>)

    @Insert(onConflict = REPLACE)
    fun saveTowns(towns: List<Town>)


    //Getting Planets
    @Query("SELECT * FROM Planets")
    fun planets(): List<Planet>

    @Query("SELECT * FROM Planets WHERE name IN (:names)")
    fun planetsFromNames(names: List<String>): List<Planet>

    @Query("SELECT * FROM Planets WHERE id IN (:ids)")
    fun planetsFromIds(ids: List<String>): List<Planet>

    //Getting Continents
    @Query("SELECT * FROM Continents")
    fun continents(): List<Continent>

    @Query("SELECT * FROM Continents WHERE name IN (:names)")
    fun continentsFromNames(names: List<String>): List<Continent>

    @Query("SELECT * FROM Continents WHERE id IN (:ids)")
    fun continentsFromIds(ids: List<String>): List<Continent>

    //Getting Countries
    @Query("SELECT * FROM Countries")
    fun countries(): List<Country>

    @Query("SELECT * FROM Countries WHERE name IN (:names)")
    fun countriesFromNames(names: List<String>): List<Country>

    @Query("SELECT * FROM Countries WHERE id IN (:ids)")
    fun countriesFromIds(ids: List<String>): List<Country>

    //Getting Regions
    @Query("SELECT * FROM Regions")
    fun regions(): List<Region>

    @Query("SELECT * FROM Regions WHERE name IN (:names)")
    fun regionsFromNames(names: List<String>): List<Region>

    @Query("SELECT * FROM Regions WHERE id IN (:ids)")
    fun regionsFromIds(ids: List<String>): List<Region>

    //Getting Divisions
    @Query("SELECT * FROM Divisions")
    fun divisions(): List<Division>

    @Query("SELECT * FROM Divisions WHERE name IN (:names)")
    fun divisionsFromNames(names: List<String>): List<Division>

    @Query("SELECT * FROM Divisions WHERE id IN (:ids)")
    fun divisionsFromIds(ids: List<String>): List<Division>

    //Getting Subdivisions
    @Query("SELECT * FROM Subdivisions")
    fun subdivisions(): List<Subdivision>

    @Query("SELECT * FROM Subdivisions WHERE name IN (:names)")
    fun subdivisionsFromNames(names: List<String>): List<Subdivision>

    @Query("SELECT * FROM Subdivisions WHERE id IN (:ids)")
    fun subdivisionsFromIds(ids: List<String>): List<Subdivision>

    //Getting Towns
    @Query("SELECT * FROM Towns")
    fun towns(): List<Town>

    @Query("SELECT * FROM Towns WHERE name IN (:names)")
    fun townsFromNames(names: List<String>): List<Town>

    @Query("SELECT * FROM Towns WHERE id IN (:ids)")
    fun townsFromIds(ids: List<String>): List<Town>

    @Query("SELECT * FROM Towns WHERE subdivision IN (:ids)")
    suspend fun townsFromSubdivisions(ids: List<String>): List<Town>

    @Query("SELECT * FROM Subdivisions WHERE division IN (:ids)")
    suspend fun subdivisionsFromDivisions(ids: List<String>): List<Subdivision>

    @Query("SELECT * FROM Divisions WHERE region IN (:ids)")
    suspend fun divisionsFromRegions(ids: List<String>): List<Division>

    @Query("SELECT * FROM Regions WHERE country IN (:ids)")
    suspend fun regionsFromCountry(ids: List<String>): List<Region>

    @Query("SELECT * FROM Countries WHERE continent IN (:ids)")
    suspend fun countriesFromContinents(ids: List<String>): List<Country>

    @Query("SELECT * FROM Continents WHERE planet IN (:ids)")
    suspend fun continentsFromPlanets(ids: List<String>): List<Continent>
}