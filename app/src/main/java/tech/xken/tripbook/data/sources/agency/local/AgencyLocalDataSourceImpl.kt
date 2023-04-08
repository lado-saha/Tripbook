package tech.xken.tripbook.data.sources.agency.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import kotlin.coroutines.CoroutineContext

class AgencyLocalDataSourceImpl internal constructor(
    private val dao: AgencyDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) : AgencyDataSource {

    override fun observeStationJobs(station: String) =
        dao.observeStationJobs(station).map { Success(it) }

    override suspend fun stationScannerMaps(
        station: String,
        scanners: List<String>,
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationScannerMaps(station))
        } catch (e: Exception) {
            Failure(e)
        }
    }


    override suspend fun stationJobsFromIds(station: String, ids: List<String>) =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.stationJobsFromIds(station, ids))
            } catch (e: Exception) {
                Failure(e)
            }
        }


    override suspend fun stationJobs(station: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationJobs(station))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun saveStationJobs(stationJobs: List<StationJob>) =
        withContext(ioDispatcher) {
            dao.saveStationJobs(stationJobs)
        }


    override suspend fun saveStations(stations: List<Station>) = withContext(ioDispatcher) {
        dao.saveStations(stations)
    }

    override suspend fun scanners(agency: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.scannersFromAgency(agency))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun scannersFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.scannersFromIds(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun observeStationScannerMaps(station: String) =
        dao.observeStationScannerMaps(station).map { Success(it) }

//    override suspend fun stationScannerMapsFromStation(station: String) = withContext(ioDispatcher) {
//        return@withContext try {
//            Success(dao.stationScannerMaps(station))
//        } catch (e: Exception) {
//            Failure(e)
//        }
//    }

    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) =
        withContext(ioDispatcher) {
            dao.saveStationScannerMaps(maps)
        }

    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) =
        withContext(ioDispatcher) {
            dao.deleteStationTownMaps(station, scanners)
        }

    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) =
        withContext(ioDispatcher) {
            dao.saveStationTownMaps(stationTownMaps)
        }

    override suspend fun stations(): Results<List<Station>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stations())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun stationsFromIds(ids: List<String>) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationsFromIds(ids))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override fun observeStationsFromIds(ids: List<String>) =
        dao.observeStationsFromIds(ids).map { Success(it) }

    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) =
        withContext(ioDispatcher) {
            dao.saveStationsLocation(id, lat, lon)
        }

    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) =
        withContext(ioDispatcher) {
            dao.deleteStationTownMaps(station, towns)
        }

    override fun observeStationTownMaps(station: String) =
        dao.observeStationTownMaps(station).map { Success(it) }

    override suspend fun stationTownMaps(station: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.stationTownMaps(station))
        } catch (e: Exception) {
            Failure(e)
        }
    }

}