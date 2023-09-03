package tech.xken.tripbook.data.sources.univ.remote

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.data.models.data
import tech.xken.tripbook.data.sources.univ.UniverseDataSource
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class UniverseRemoteDataSource @Inject constructor(
    val ioDispatcher: CoroutineContext,
    val client: SupabaseClient
) : UniverseDataSource {
    private var parser = Json {
        coerceInputValues = true
        encodeDefaults = true
    }

    override suspend fun townsFromNames(names: List<String>, columns: List<String>) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(client.postgrest["sc_univ", "town"].select(columns = Columns.list(columns)) {
                    isIn("name", names)
                }.let {
                    parser.decodeFromJsonElement<List<Town>>(it.body!!).sortedBy { town ->
                        names.indexOf(town.name!!.lowercase())
                    }
                }
                )
            } catch (e: Exception) {
                Log.e("Failed-Names", "$e")
                Failure(e)
            }
        }

    override suspend fun townsFromIds(
        ids: List<String>,
        columns: List<String>
    ) = withContext(ioDispatcher) {

        return@withContext try {
            Success(
                client.postgrest["sc_univ", "town"].select(columns = Columns.list(columns)) {
                    isIn("town_id", ids)
                }.let {
                    parser.decodeFromJsonElement<List<Town>>(it.body!!).sortedBy { town ->
                        ids.indexOf(town.townId.lowercase())
                    }
                }
            )

        } catch (e: Exception) {
            Log.e("Failed-Ids", "$e")
            Failure(e)
        }
    }

    override suspend fun fullRoadFromTownNames(names: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            val towns =
                townsFromNames(names, listOf("town_id", "name", "geo_point", "subdivision_id")).data
            val townIds = towns.sortedBy { it.name!! }.map { it.townId }
            val road = client.postgrest.from("sc_univ", "road").select(
                columns = Columns.ALL
            ) {
                isIn("town_1_id", townIds)
                isIn("town_2_id", townIds)
            }.let { parser.decodeFromJsonElement<List<Road>>(it.body!!)[0] }
            val pathTowns = townsFromIds(
                road.roadPath!!,
                columns = listOf("town_id", "name", "geo_point", "subdivision_id")
            ).data
            Success(
                road.apply {
                    town1 = towns[0]
                    town2 = towns[1]
                    roadPathTowns = pathTowns
                }
            )
        } catch (e: Exception) {
            Log.e("Failed-ROad", "$e")
            Failure(e)
        }
    }

}
//
//    override suspend fun roads(): Results<List<Road>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun roadFromIds(ids: String): Results<List<Road>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveRoads(roads: List<Road>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun roadBtn2TownsFromIds(id1: String, id2: String): Results<Road> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun roadBtn2TownsFromNames(name1: String, name2: String): Results<Road> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun roadsFromOrToTownFromId(id: String): Results<List<Road>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveTownPairs(links: List<TownPair>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun roadsFromOrToTownFromName(name: String): Results<List<Road>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun itineraryOfRoadFromId(
//        id: String,
//        start: String,
//        stop: String
//    ): Results<Itinerary> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun savePlanets(planets: List<Planet>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveContinents(continents: List<Continent>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveCountries(countries: List<Country>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveRegions(regions: List<Region>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveDivisions(divisions: List<Division>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveSubdivisions(subdivisions: List<Subdivision>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveTowns(towns: List<Town>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun planets(): Results<List<Planet>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun planetsFromNames(names: List<String>): Results<List<Planet>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun planetsFromIds(ids: List<String>): Results<List<Planet>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun continents(): Results<List<Continent>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun continentsFromNames(names: List<String>): Results<List<Continent>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun continentsFromIds(ids: List<String>): Results<List<Continent>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun countries(): Results<List<Country>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun countriesFromNames(names: List<String>): Results<List<Country>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun countriesFromIds(ids: List<String>): Results<List<Country>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun regions(): Results<List<Region>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun regionsFromNames(names: List<String>): Results<List<Region>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun regionsFromIds(ids: List<String>): Results<List<Region>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun divisions(): Results<List<Division>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun divisionsFromNames(names: List<String>): Results<List<Division>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun divisionsFromIds(ids: List<String>): Results<List<Division>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun subdivisions(): Results<List<Subdivision>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun subdivisionsFromNames(names: List<String>): Results<List<Subdivision>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun subdivisionsFromIds(ids: List<String>): Results<List<Subdivision>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun towns(): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromNames(names: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun planetsOfTowns(ids: List<String>): Results<List<Planet>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun continentsOfTowns(ids: List<String>): Results<List<Continent>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun countriesOfTowns(ids: List<String>): Results<List<Country>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun regionsOfTowns(ids: List<String>): Results<List<Region>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun divisionsOfTowns(ids: List<String>): Results<List<Division>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun subdivisionsOfTowns(ids: List<String>): Results<List<Subdivision>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromIds(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromSubdivisions(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromDivisions(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromRegions(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromCountries(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromContinents(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun townsFromPlanets(ids: List<String>): Results<List<Town>> {
//        TODO("Not yet implemented")
//    }
//}