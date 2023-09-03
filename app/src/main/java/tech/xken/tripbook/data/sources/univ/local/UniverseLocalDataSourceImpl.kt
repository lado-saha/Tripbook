//package tech.xken.tripbook.data.sources.univ.local
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import tech.xken.tripbook.data.models.*
//import tech.xken.tripbook.data.models.Itinerary.Companion.toItinerary
//import tech.xken.tripbook.data.models.Results.Failure
//import tech.xken.tripbook.data.models.Results.Success
//import tech.xken.tripbook.data.sources.univ.UniverseDataSource
//import kotlin.coroutines.CoroutineContext
//
///**
// * The current only database for the project
// * SQLITE
// */
//class UniverseLocalDataSourceImpl internal constructor(
//    private val dao: UniverseDao,
//    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
//) : UniverseDataSource {
//
//    /**
//     * Returns the list of all towns in the database
//     */
//    override suspend fun towns(): Results<List<Town>> = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.towns())
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromNames(names: List<String>) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.townsFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//
//    override suspend fun townsFromIds(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.townsFromIds(ids))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromSubdivisions(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.townsFromSubdivisions(ids))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromDivisions(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            val subdivisions = dao.subdivisionsFromDivisions(ids).map { it.subdivisionId }
//            townsFromSubdivisions(subdivisions)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromRegions(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            val divisions = dao.divisionsFromRegions(ids).map { it.divisionId }
//            townsFromDivisions(divisions)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromCountries(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            val regions = dao.regionsFromCountry(ids).map { it.regionId }
//            townsFromRegions(regions)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromContinents(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            val countries = dao.countriesFromContinents(ids).map { it.countryId }
//            townsFromCountries(countries)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun townsFromPlanets(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            val continents = dao.continentsFromPlanets(ids).map { it.id }
//            townsFromContinents(continents)
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun planetsOfTowns(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            when (val continents = continentsOfTowns(ids)) {
//                is Failure -> throw continents.exception
//                is Success -> continents.data.map { it.planet!! }
//                    .let { Success(dao.planetsFromIds(it)) }
//            }
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun continentsOfTowns(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            when (val countries = countriesOfTowns(ids)) {
//                is Failure -> throw countries.exception
//                is Success -> countries.data.map { it.continent!! }
//                    .let { Success(dao.continentsFromIds(it)) }
//            }
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun countriesOfTowns(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            when (val regions = regionsOfTowns(ids)) {
//                is Failure -> throw regions.exception
//                is Success -> regions.data.map { it.countryId!! }
//                    .let { Success(dao.countriesFromIds(it)) }
//            }
//        } catch (e: Exception) {
//            Failure(e)
//        }
//
//    }
//
//    override suspend fun regionsOfTowns(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            when (val divisions = divisionsOfTowns(ids)) {
//                is Failure -> throw divisions.exception
//                is Success -> divisions.data.map { it.regionId!! }
//                    .let { Success(dao.regionsFromIds(it)) }
//            }
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun divisionsOfTowns(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            when (val subdivisions = subdivisionsOfTowns(ids)) {
//                is Failure -> throw subdivisions.exception
//                is Success -> subdivisions.data.map { it.divisionId!! }
//                    .let { Success(dao.divisionsFromIds(it)) }
//            }
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun subdivisionsOfTowns(ids: List<String>) = withContext(ioDispatcher) {
//        return@withContext try {
//            when (val towns = townsFromIds(ids)) {
//                is Failure -> throw towns.exception
//                is Success -> towns.data.map { it.subdivisionId!! }
//                    .let { Success(dao.subdivisionsFromIds(it)) }
//            }
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//
//    override suspend fun townsFromGeoPoint(lat: Double, lon: Double) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.townsFromGeoPoint(lat, lon))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun saveTowns(towns: List<Town>) = withContext(ioDispatcher) {
//        dao.saveTowns(towns)
//    }
//
//    override suspend fun planets(): Results<List<Planet>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.planets())
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun planetsFromNames(names: List<String>): Results<List<Planet>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.planetsFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun planetsFromIds(ids: List<String>): Results<List<Planet>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.planetsFromIds(ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun continents(): Results<List<Continent>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.continents())
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun continentsFromNames(names: List<String>): Results<List<Continent>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.continentsFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun continentsFromIds(ids: List<String>): Results<List<Continent>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.continentsFromIds(ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun countries(): Results<List<Country>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.countries())
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun countriesFromNames(names: List<String>): Results<List<Country>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.countriesFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun countriesFromIds(ids: List<String>): Results<List<Country>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.countriesFromIds(ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun regions(): Results<List<Region>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.regions())
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun regionsFromNames(names: List<String>): Results<List<Region>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.regionsFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun regionsFromIds(ids: List<String>): Results<List<Region>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.regionsFromIds(ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun divisions(): Results<List<Division>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.divisions())
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun divisionsFromNames(names: List<String>): Results<List<Division>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.divisionsFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun divisionsFromIds(ids: List<String>): Results<List<Division>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.divisionsFromIds(ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun subdivisions(): Results<List<Subdivision>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.subdivisions())
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun subdivisionsFromNames(names: List<String>): Results<List<Subdivision>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.subdivisionsFromNames(names))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun subdivisionsFromIds(ids: List<String>): Results<List<Subdivision>> {
//        return withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.subdivisionsFromIds(ids))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//    }
//
//    override suspend fun roads() = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.roads())
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun roadFromIds(ids: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.roadFromIds(ids))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun saveRoads(roads: List<Road>) = withContext(ioDispatcher) {
//        dao.saveRoad(roads)
//    }
//
//    override suspend fun roadBtn2TownsFromIds(id1: String, id2: String) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.roadBtn2TownsFromIds(id1, id2))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//
//    /**
//     * We first get the ids corresponding to the [name1] and [name2] and then return the corresponding road using
//     * [roadBtn2TownsFromIds]
//     */
//    override suspend fun roadBtn2TownsFromNames(name1: String, name2: String) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                val towns = dao.townsFromNames(listOf(name1, name2))//1st operation
//                Success(dao.roadBtn2TownsFromIds(towns.first().townId, towns.last().townId))//Last operation
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//
//    override suspend fun roadsFromOrToTownFromId(id: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.roadsFromOrToTownById(id))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }
//
//    override suspend fun saveTownPairs(links: List<TownPair>) = withContext(ioDispatcher) {
//        dao.saveItinerary(links)
//    }
//
//    /**
//     * First finds the associated [Town.townId] for the [name] and then returns all [Road]s which can be from or to the the town
//     */
//    override suspend fun roadsFromOrToTownFromName(name: String) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                val town = dao.townsFromNames(listOf(name)).first()//1st operation
//                Success(dao.roadsFromOrToTownById(town.townId))//Last operation
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//
//    /**
//     * We get a List of [TownPair]s from the database and convert it to an ordered itinerary using the
//     * [start]ID and the [stop]ID.
//     */
//    override suspend fun itineraryOfRoadFromId(id: String, start: String, stop: String) =
//        withContext(ioDispatcher) {
//            return@withContext try {
//                Success(dao.townPairsOfRoadFromId(id).toItinerary(start, stop))
//            } catch (e: Exception) {
//                Failure(e)
//            }
//        }
//
//    override suspend fun savePlanets(planets: List<Planet>) {
//        withContext(ioDispatcher) {
//            dao.savePlanets(planets)
//        }
//    }
//
//    override suspend fun saveContinents(continents: List<Continent>) {
//        withContext(ioDispatcher) {
//            dao.saveContinents(continents)
//        }
//    }
//
//    override suspend fun saveCountries(countries: List<Country>) {
//        withContext(ioDispatcher) {
//            dao.saveCountries(countries)
//        }
//    }
//
//    override suspend fun saveRegions(regions: List<Region>) {
//        withContext(ioDispatcher) {
//            dao.saveRegions(regions)
//        }
//    }
//
//    override suspend fun saveDivisions(divisions: List<Division>) {
//        withContext(ioDispatcher) {
//            dao.saveDivisions(divisions)
//        }
//    }
//
//    override suspend fun saveSubdivisions(subdivisions: List<Subdivision>) {
//        withContext(ioDispatcher) {
//            dao.saveSubdivisions(subdivisions)
//        }
//    }
//}
//
