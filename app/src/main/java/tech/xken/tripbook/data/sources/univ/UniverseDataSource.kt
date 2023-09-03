package tech.xken.tripbook.data.sources.univ

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.decodeFromJsonElement
import tech.xken.tripbook.data.models.*

/**
 * The implementation of a Universe Datasource. Must be override by the the Local and Online
 * Datasource
 */
interface UniverseDataSource {

    suspend fun townsFromNames(names: List<String>, columns: List<String>): Results<List<Town>>

    suspend fun townsFromIds(ids: List<String>, columns: List<String>): Results<List<Town>>

    suspend fun fullRoadFromTownNames(names: List<String>): Results<Road>
}
//    // Lightweight operations
////    suspend fun townNames(excludedIds: List<String>): Results<List<String>>
////    suspend fun townRegionNames(excludedIds: List<String>): Results<List<String>>
////    suspend fun townDivisionsNames(excludedIds: List<String>): Results<List<String>>
//
//    // Town only operations
//    suspend fun townsFromGeoPoint(lat: Double, lon: Double): Results<List<Town>>
//
//    // Roads only operations
//    suspend fun roads(): Results<List<Road>>
//    suspend fun roadFromIds(ids: String): Results<List<Road>>
//    suspend fun saveRoads(roads: List<Road>)
//
//    //Composite Towns and Roads i.e Transactions
//    suspend fun roadBtn2TownsFromIds(id1: String, id2: String): Results<Road>
//
//    // Purely called at the level of the Repository
//    suspend fun roadBtn2TownsFromNames(name1: String, name2: String): Results<Road>
//    suspend fun roadsFromOrToTownFromId(id: String): Results<List<Road>>
//
//    // Purely called at the level of the Repository
//    suspend fun saveTownPairs(links: List<TownPair>)
//    suspend fun roadsFromOrToTownFromName(name: String): Results<List<Road>>
//    suspend fun itineraryOfRoadFromId(id: String, start: String, stop: String): Results<Itinerary>
//
//    suspend fun savePlanets(planets: List<Planet>)
//
//    suspend fun saveContinents(continents: List<Continent>)
//
//    suspend fun saveCountries(countries: List<Country>)
//
//    suspend fun saveRegions(regions: List<Region>)
//
//    suspend fun saveDivisions(divisions: List<Division>)
//
//    suspend fun saveSubdivisions(subdivisions: List<Subdivision>)
//    suspend fun saveTowns(towns: List<Town>)
//
//    //Getting Planets
//    suspend fun planets(): Results<List<Planet>>
//
//    suspend fun planetsFromNames(names: List<String>): Results<List<Planet>>
//    suspend fun planetsFromIds(ids: List<String>): Results<List<Planet>>
//
//    //Getting Continents
//
//    suspend fun continents(): Results<List<Continent>>
//
//    suspend fun continentsFromNames(names: List<String>): Results<List<Continent>>
//
//    suspend fun continentsFromIds(ids: List<String>): Results<List<Continent>>
//
//    //Getting Countries
//    suspend fun countries(): Results<List<Country>>
//
//    suspend fun countriesFromNames(names: List<String>): Results<List<Country>>
//
//    suspend fun countriesFromIds(ids: List<String>): Results<List<Country>>
//
//    //Getting Regions
//    suspend fun regions(): Results<List<Region>>
//
//    suspend fun regionsFromNames(names: List<String>): Results<List<Region>>
//
//    suspend fun regionsFromIds(ids: List<String>): Results<List<Region>>
//
//    //Getting Divisions
//    suspend fun divisions(): Results<List<Division>>
//
//    suspend fun divisionsFromNames(names: List<String>): Results<List<Division>>
//
//    suspend fun divisionsFromIds(ids: List<String>): Results<List<Division>>
//
//    //Getting Subdivisions
//    suspend fun subdivisions(): Results<List<Subdivision>>
//
//    suspend fun subdivisionsFromNames(names: List<String>): Results<List<Subdivision>>
//
//    suspend fun subdivisionsFromIds(ids: List<String>): Results<List<Subdivision>>
//
//    //Getting Towns
//    suspend fun towns(): Results<List<Town>>
//
//    suspend fun townsFromNames(names: List<String>): Results<List<Town>>
//
//    //Deeper Recursive use of the above
//    suspend fun planetsOfTowns(ids: List<String>): Results<List<Planet>>
//    suspend fun continentsOfTowns(ids: List<String>): Results<List<Continent>>
//    suspend fun countriesOfTowns(ids: List<String>): Results<List<Country>>
//    suspend fun regionsOfTowns(ids: List<String>): Results<List<Region>>
//    suspend fun divisionsOfTowns(ids: List<String>): Results<List<Division>>
//    suspend fun subdivisionsOfTowns(ids: List<String>): Results<List<Subdivision>>
//    suspend fun townsFromIds(ids: List<String>): Results<List<Town>>
//
//    //Inverse use
//    suspend fun townsFromSubdivisions(ids: List<String>): Results<List<Town>>
//    suspend fun townsFromDivisions(ids: List<String>): Results<List<Town>>
//    suspend fun townsFromRegions(ids: List<String>): Results<List<Town>>
//    suspend fun townsFromCountries(ids: List<String>): Results<List<Town>>
//    suspend fun townsFromContinents(ids: List<String>): Results<List<Town>>
//    suspend fun townsFromPlanets(ids: List<String>): Results<List<Town>>
//}