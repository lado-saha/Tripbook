package tech.xken.tripbook.data.sources.universe

import tech.xken.tripbook.data.models.*

interface UniverseRepository {
    //Lightweight operations
    suspend fun townNames(excludedIds: List<String>): Results<List<String>>
    suspend fun townRegionNames(excludedIds: List<String>): Results<List<String>>
    suspend fun townDivisionsNames(excludedIds: List<String>): Results<List<String>>
    // Town only operations
    suspend fun towns(): Results<List<Town>>
    suspend fun townsFromIDs(ids: List<String>): Results<List<Town>>
    suspend fun townsFromNames(names: List<String>): Results<List<Town>>
    suspend fun townsFromGeoPoint(lat: Double, lon: Double): Results<List<Town>>
    suspend fun townsFromRegion(region: String): Results<List<Town>>
    suspend fun townsFromDivision(division: String): Results<List<Town>>
    suspend fun townsFromSubDivision(subdivision: String): Results<List<Town>>
    suspend fun saveTowns(towns: List<Town>)

    // Roads only operations
    suspend fun roads(): Results<List<Road>>
    suspend fun roadFromIds(ids: String): Results<List<Road>>

    suspend fun saveRoads(roads: List<Road>)

    //Composite Towns and Roads i.e Transactions
    suspend fun roadBtn2TownsFromIds(id1: String, id2: String): Results<Road>

    // Purely called at the level of the Repository
    suspend fun roadBtn2TownsFromNames(name1: String, name2: String): Results<Road>

    suspend fun roadsFromOrToTownFromId(id: String): Results<List<Road>>

    // Purely called at the level of the Repository
    suspend fun roadsFromOrToTownFromName(name: String): Results<List<Road>>

    suspend fun saveTownPairs(links: List<TownPair>)
    suspend fun itineraryOfRoadFromId(id: String, start: String, stop: String): Results<Itinerary>
//    suspend fun itineraryOfRoadBtn2TownsFromIds(id1: String, id2: String):Results<Itinerary>
//    suspend fun itineraryOfRoadBtn2TownsFromNames(name1: String, name2: String): Results<Itinerary>
}