//package tech.xken.tripbook.data.sources.univ
//
//import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.sources.univ.UniverseDataSource
import tech.xken.tripbook.data.sources.univ.UniverseRepository

/**
 * Default implementation of [UniverseRepository] and is the only entry point to the backend
 */
class UniverseRepositoryImpl(
    private val remoteDataSource: UniverseDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : UniverseRepository {
    override suspend fun fullRoadFromTownNames(town1Name: String, town2Name: String): Results<Road> {
        return remoteDataSource.fullRoadFromTownNames(listOf(town1Name, town2Name))
    }

}
//    override suspend fun towns() = localDataSource.towns()
//    override suspend fun townsFromNames(names: List<String>): Results<List<Town>> =
//        localDataSource.townsFromNames(names)

//    override suspend fun townsFromIds(ids: List<String>): Results<List<Town>> =
//        localDataSource.townsFromIds(ids)
//
//    override suspend fun planetsOfTowns(ids: List<String>): Results<List<Planet>> =
//        localDataSource.planetsOfTowns(ids)
//
//    override suspend fun continentsOfTowns(ids: List<String>): Results<List<Continent>> =
//        localDataSource.continentsOfTowns(ids)
//
//    override suspend fun countriesOfTowns(ids: List<String>): Results<List<Country>> =
//        localDataSource.countriesOfTowns(ids)
//
//    override suspend fun regionsOfTowns(ids: List<String>): Results<List<Region>> =
//        localDataSource.regionsOfTowns(ids)
//
//    override suspend fun divisionsOfTowns(ids: List<String>): Results<List<Division>> =
//        localDataSource.divisionsOfTowns(ids)
//
//    override suspend fun subdivisionsOfTowns(ids: List<String>): Results<List<Subdivision>> =
//        localDataSource.subdivisionsOfTowns(ids)
//
//    override suspend fun saveTowns(towns: List<Town>): Unit = coroutineScope {
//        launch { localDataSource.saveTowns(towns) }
//    }
//
//    override suspend fun planets(): Results<List<Planet>> = localDataSource.planets()
//    override suspend fun planetsFromNames(names: List<String>): Results<List<Planet>> =
//        localDataSource.planetsFromNames(names)
//
//    override suspend fun planetsFromIds(ids: List<String>): Results<List<Planet>> =
//        localDataSource.planetsFromIds(ids)
//
//    override suspend fun continents(): Results<List<Continent>> = localDataSource.continents()
//    override suspend fun continentsFromNames(names: List<String>): Results<List<Continent>> =
//        localDataSource.continentsFromNames(names)
//
//    override suspend fun continentsFromIds(ids: List<String>): Results<List<Continent>> =
//        localDataSource.continentsFromIds(ids)
//
//    override suspend fun countries(): Results<List<Country>> = localDataSource.countries()
//    override suspend fun countriesFromNames(names: List<String>): Results<List<Country>> =
//        localDataSource.countriesFromNames(names)
//
//    override suspend fun countriesFromIds(ids: List<String>): Results<List<Country>> =
//        localDataSource.countriesFromIds(ids)
//
//    override suspend fun regions(): Results<List<Region>> = localDataSource.regions()
//    override suspend fun regionsFromNames(names: List<String>): Results<List<Region>> =
//        localDataSource.regionsFromNames(names)
//
//    override suspend fun regionsFromIds(ids: List<String>): Results<List<Region>> =
//        localDataSource.regionsFromIds(ids)
//
//    override suspend fun divisions(): Results<List<Division>> = localDataSource.divisions()
//    override suspend fun divisionsFromNames(names: List<String>): Results<List<Division>> =
//        localDataSource.divisionsFromNames(names)
//
//    override suspend fun divisionsFromIds(ids: List<String>): Results<List<Division>> =
//        localDataSource.divisionsFromIds(ids)
//
//    override suspend fun subdivisions(): Results<List<Subdivision>> = localDataSource.subdivisions()
//    override suspend fun subdivisionsFromNames(names: List<String>): Results<List<Subdivision>> =
//        localDataSource.subdivisionsFromNames(names)
//
//    override suspend fun subdivisionsFromIds(ids: List<String>): Results<List<Subdivision>> =
//        localDataSource.subdivisionsFromIds(ids)
//
//    override suspend fun roads(): Results<List<Road>> = localDataSource.roads()
//
//    override suspend fun roadFromIds(ids: String) = localDataSource.roadFromIds(ids)
//
//    override suspend fun saveRoads(roads: List<Road>): Unit = coroutineScope {
//        launch { localDataSource.saveRoads(roads) }
//    }
//
//    override suspend fun roadBtn2TownsFromIds(id1: String, id2: String) =
//        localDataSource.roadBtn2TownsFromIds(id1, id2)
//
//    override suspend fun roadBtn2TownsFromNames(name1: String, name2: String) =
//        localDataSource.roadBtn2TownsFromNames(name1, name2)
//
//    override suspend fun roadsFromOrToTownFromId(id: String) =
//        localDataSource.roadsFromOrToTownFromId(id)
//
//    override suspend fun roadsFromOrToTownFromName(name: String) =
//        localDataSource.roadsFromOrToTownFromName(name)
//
//    override suspend fun saveTownPairs(links: List<TownPair>): Unit = coroutineScope {
//        launch { localDataSource.saveTownPairs(links) }
//    }
//
//    override suspend fun itineraryOfRoadFromId(id: String, start: String, stop: String) =
//        localDataSource.itineraryOfRoadFromId(id, start, stop)
//
//    override suspend fun savePlanets(planets: List<Planet>): Unit = coroutineScope {
//        launch { localDataSource.savePlanets(planets) }
//    }
//
//    override suspend fun saveContinents(continents: List<Continent>): Unit = coroutineScope {
//        launch { localDataSource.saveContinents(continents) }
//    }
//
//    override suspend fun saveCountries(countries: List<Country>): Unit = coroutineScope {
//        launch { localDataSource.saveCountries(countries) }
//    }
//
//    override suspend fun saveRegions(regions: List<Region>): Unit = coroutineScope {
//        launch { localDataSource.saveRegions(regions) }
//    }
//
//    override suspend fun saveDivisions(divisions: List<Division>): Unit = coroutineScope {
//        launch { localDataSource.saveDivisions(divisions) }
//    }
//
//    override suspend fun saveSubdivisions(subdivisions: List<Subdivision>): Unit = coroutineScope {
//        launch { localDataSource.saveSubdivisions(subdivisions) }
//    }
//}
