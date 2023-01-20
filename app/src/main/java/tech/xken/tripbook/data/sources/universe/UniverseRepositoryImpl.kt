package tech.xken.tripbook.data.sources.universe

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.data.models.TownPair

/**
 * Default implementation of [UniverseRepository] and is the only entry point to the backend
 */
class UniverseRepositoryImpl(
    private val localDataSource: UniverseDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : UniverseRepository {
    override suspend fun townNames(excludedIds: List<String>) =
        localDataSource.townNames(excludedIds)

    override suspend fun townRegionNames(excludedIds: List<String>) =
        localDataSource.townRegionNames(excludedIds)

    override suspend fun townDivisionsNames(excludedIds: List<String>) =
        localDataSource.townDivisionsNames(excludedIds)

    override suspend fun towns() = localDataSource.towns()

    override suspend fun townsFromIDs(ids: List<String>) = localDataSource.townsFromIDs(ids)

    override suspend fun townsFromNames(names: List<String>) = localDataSource.townsFromNames(names)

    override suspend fun townsFromGeoPoint(lat: Double, lon: Double) =
        localDataSource.townsFromGeoPoint(lat, lon)

    override suspend fun townsFromRegion(region: String) = localDataSource.townsFromRegion(region)

    override suspend fun townsFromDivision(division: String) =
        localDataSource.townsFromDivision(division)

    override suspend fun townsFromSubDivision(subdivision: String) =
        localDataSource.townsFromSubDivision(subdivision)

    override suspend fun saveTowns(towns: List<Town>): Unit = coroutineScope {
        launch { localDataSource.saveTowns(towns) }
    }

    override suspend fun roads(): Results<List<Road>> = localDataSource.roads()

    override suspend fun roadFromIds(ids: String) = localDataSource.roadFromIds(ids)

    override suspend fun saveRoads(roads: List<Road>): Unit = coroutineScope {
        launch { localDataSource.saveRoads(roads) }
    }

    override suspend fun roadBtn2TownsFromIds(id1: String, id2: String) =
        localDataSource.roadBtn2TownsFromIds(id1, id2)

    override suspend fun roadBtn2TownsFromNames(name1: String, name2: String) =
        localDataSource.roadBtn2TownsFromNames(name1, name2)

    override suspend fun roadsFromOrToTownFromId(id: String) =
        localDataSource.roadsFromOrToTownFromId(id)

    override suspend fun roadsFromOrToTownFromName(name: String) =
        localDataSource.roadsFromOrToTownFromName(name)

    override suspend fun saveTownPairs(links: List<TownPair>): Unit = coroutineScope {
        launch { localDataSource.saveTownPairs(links) }
    }

    override suspend fun itineraryOfRoadFromId(id: String, start: String, stop: String) =
        localDataSource.itineraryOfRoadFromId(id, start, stop)
}
