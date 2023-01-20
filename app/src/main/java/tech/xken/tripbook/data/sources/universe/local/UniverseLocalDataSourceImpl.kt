package tech.xken.tripbook.data.sources.universe.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Itinerary.Companion.toItinerary
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Town
import tech.xken.tripbook.data.models.TownPair
import tech.xken.tripbook.data.sources.universe.UniverseDataSource
import kotlin.coroutines.CoroutineContext

/**
 * The current only database for the project
 * SQLITE
 */
class UniverseLocalDataSourceImpl internal constructor(
    private val dao: UniverseDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : UniverseDataSource {
    /**
     * Returns all the names of towns
     */
    override suspend fun townNames(excludedIds: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townNames(excludedIds))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    /**
     * Returns all the different regions names
     */
    override suspend fun townRegionNames(excludedIds: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townRegionNames(excludedIds))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    /**
     * Returns all the divisions names
     */
    override suspend fun townDivisionsNames(excludedIds: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townDivisionsNames(excludedIds))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    /**
     * Returns the list of all towns in the database
     */
    override suspend fun towns(): Results<List<Town>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.towns())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    /*
    =withContext(ioDispatcher){
    return@withContext try {
                Success(dao)
            }catch(e: Exception){
                Failure(e)
            }
            }
       */
    override suspend fun townsFromIDs(ids: List<String>) =
        withContext(ioDispatcher)
        {
            return@withContext try {
                Success(dao.townsFromIDs(ids))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun townsFromNames(names: List<String>) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.townsFromNames(names))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun townsFromGeoPoint(lat: Double, lon: Double) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townsFromGeoPoint(lat, lon))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun townsFromRegion(region: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townsFromRegion(region))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun townsFromDivision(division: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townsFromDivision(division))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun townsFromSubDivision(subdivision: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.townsFromSubDivision(subdivision))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun saveTowns(towns: List<Town>) = withContext(ioDispatcher) {
        dao.saveTowns(towns)
    }

    override suspend fun roads() = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.roads())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun roadFromIds(ids: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.roadFromIds(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun saveRoads(roads: List<Road>) = withContext(ioDispatcher) {
        dao.saveRoad(roads)
    }


    override suspend fun roadBtn2TownsFromIds(id1: String, id2: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.roadBtn2TownsFromIds(id1, id2))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    /**
     * We first get the ids corresponding to the [name1] and [name2] and then return the corresponding road using
     * [roadBtn2TownsFromIds]
     */
    override suspend fun roadBtn2TownsFromNames(name1: String, name2: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                val towns = dao.townsFromNames(listOf(name1, name2))//1st operation
                Success(dao.roadBtn2TownsFromIds(towns.first().id, towns.last().id))//Last operation
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun roadsFromOrToTownFromId(id: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.roadsFromOrToTownById(id))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun saveTownPairs(links: List<TownPair>) = withContext(ioDispatcher) {
        dao.saveItinerary(links)
    }

    /**
     * First finds the associated [Town.id] for the [name] and then returns all [Road]s which can be from or to the the town
     */
    override suspend fun roadsFromOrToTownFromName(name: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                val town = dao.townsFromNames(listOf(name)).first()//1st operation
                Success(dao.roadsFromOrToTownById(town.id))//Last operation
            } catch (e: Exception) {
                Failure(e)
            }
        }

    /**
     * We get a List of [TownPair]s from the database and convert it to an ordered itinerary using the
     * [start]ID and the [stop]ID.
     */
    override suspend fun itineraryOfRoadFromId(id: String, start: String, stop: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.townPairsOfRoadFromId(id).toItinerary(start, stop))
            } catch (e: Exception) {
                Failure(e)
            }
        }
}

