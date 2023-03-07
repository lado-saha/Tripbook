package tech.xken.tripbook.data.sources.agency

import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.sources.universe.UniverseDataSource

class AgencyRepositoryImpl(
    private val localAgencySource: AgencyDataSource,
    private val localUnivSource: UniverseDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AgencyRepository {

    override suspend fun savePark(park: Park)  = localAgencySource.savePark(park)

    override suspend fun parks() = localAgencySource.parks()

    override suspend fun parksFromIds(ids: List<String>) = localAgencySource.parksFromIds(ids)

    /**
     * Returns the [Park] from the ids gotten from [TownParkMap]
     */
    override suspend fun parksFromTown(town: String) =
        when(val townParkMaps = localAgencySource.townParkMapFromTown(town)){
            is Results.Success -> {
                val parkIds = townParkMaps.data.map { it.park }
                localAgencySource.parksFromIds(parkIds)
            }
            is Results.Failure -> {
                throw townParkMaps.exception
            }
        }

    /**
     * Returns the [Park] from the ids gotten from [TownParkMap]
     */
    override suspend fun townFromPark(park: String) = when(val townParkMaps = localAgencySource.townParkMapFromTown(park)){
        is Results.Success -> {
            val townIds = townParkMaps.data.map { it.town }
            localUnivSource.townsFromIds(townIds)
        }
        is Results.Failure -> {
            throw townParkMaps.exception
        }
    }
}