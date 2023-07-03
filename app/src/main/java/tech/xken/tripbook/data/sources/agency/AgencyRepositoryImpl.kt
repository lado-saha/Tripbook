package tech.xken.tripbook.data.sources.agency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.booker.BookerRepository

class AgencyRepositoryImpl(
    private val localAgencySource: AgencyDataSource,
    private val bookingRepo: BookerRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : AgencyRepository {

    override suspend fun saveStationJobs(stationJobs: List<StationJob>) =
        localAgencySource.saveStationJobs(stationJobs)

    override fun observeStationJobs(station: String): Flow<Results<List<StationJob>>> =
        localAgencySource.observeStationJobs(station)

    override suspend fun stationJobsFromIds(
        station: String,
        ids: List<String>,
    ): Results<List<StationJob>> = localAgencySource.stationJobsFromIds(station, ids)

    override suspend fun stationJobs(station: String) = localAgencySource.stationJobs(station)

    override suspend fun scannersFromIds(
        ids: List<String>,
        station: String,
        getBookers: Boolean,
        getJobs: Boolean,
    ): Results<Map<String, Scanner>> {
        val scanners = mutableMapOf<String, Scanner>()
        var exception: Exception? = null
        //Get scanner
        when (val results = localAgencySource.scannersFromIds(ids)) {
            is Success -> scanners += results.data.associateBy { it.bookerID }
            is Failure -> exception = results.exception
        }
        //Get booker associated with scanner
        if (getBookers && exception == null)
            when (val results = bookingRepo.bookersFromIds(scanners.keys.toList())) {
                is Success -> results.data.forEach {
                    scanners[it.id]!!.apply { booker = it }
                }
                is Failure -> exception = results.exception
            }
        //Get jobs ids associated with each scanner
        if (getJobs && exception == null)
            when (
                val results = localAgencySource.stationScannerMaps(
                    station = station,
                    scanners = scanners.keys.toList()
                )
            ) {
                is Failure -> exception = results.exception
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.jobIds?.plusAssign(it.job)
                }
            }
        //Get Permissions ids
        /*if (getPermissions && exception == null)
            when (val results =
                localAgencySource.stationJobPermissionsMaps(jobs = scanners.values.map { it. })) {
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.apply {
                        if (permissionIds == null) permissionIds = mutableListOf(it.permission)
                        else permissionIds!!.add(it.permission)
                    }
                }
                is Failure -> exception = results.exception
            }*/
        return if (exception == null) Success(scanners) else Failure(exception)
    }

    override suspend fun scanners(
        agency: String,
        station: String,
        getBookers: Boolean,
        getJobs: Boolean,
    ): Results<Map<String, Scanner>> {
        val scanners = mutableMapOf<String, Scanner>()
        var exception: Exception? = null
        when (val results = localAgencySource.scanners(agency)) {
            is Success -> scanners += results.data.associateBy { it.bookerID }
            is Failure -> exception = results.exception
        }
        if (getBookers && exception == null)
            when (val results = bookingRepo.bookersFromIds(scanners.keys.toList())) {
                is Success -> results.data.forEach {
                    scanners[it.id]!!.apply { booker = it }
                }
                is Failure -> exception = results.exception
            }
        /*if (getPermissions && exception == null)
            when (val results =
                localAgencySource.stationJobPermissionsMaps(scanners = scanners.keys.toList())) {
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.apply {
                        if (permissionIds == null) permissionIds = mutableListOf(it.permission)
                        else permissionIds!!.add(it.permission)
                    }
                }
                is Failure -> exception = results.exception
            }*/
        if (getJobs && exception == null)
            when (
                val results = localAgencySource.stationScannerMaps(
                    station = station,
                    scanners = scanners.keys.toList()
                )
            ) {
                is Failure -> exception = results.exception
                is Success -> results.data.forEach {
                    scanners[it.scanner]!!.jobIds?.plusAssign(it.job)
                }
            }

        return if (exception == null) Success(scanners) else Failure(exception)
    }


    override fun observeStationScannerMaps(station: String) =
        localAgencySource.observeStationScannerMaps(station)

    override suspend fun stationScannerMaps(station: String, scanners: List<String>) =
        localAgencySource.stationScannerMaps(station, scanners)

    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) =
        localAgencySource.saveStationScannerMaps(maps)

    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) =
        localAgencySource.deleteStationScannerMap(station, scanners)

    override suspend fun saveStations(stations: List<Station>) =
        localAgencySource.saveStations(stations)

    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) =
        localAgencySource.saveStationTownMaps(stationTownMaps)

    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) =
        localAgencySource.deleteStationTownMaps(station, towns)

    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) =
        localAgencySource.saveStationLocation(id, lat, lon)

    override fun observeStationTownMaps(station: String) =
        localAgencySource.observeStationTownMaps(station)

    override fun observeStationsFromIds(ids: List<String>) =
        localAgencySource.observeStationsFromIds(ids)

    override suspend fun stationTownMaps(station: String) =
        localAgencySource.stationTownMaps(station)

    override suspend fun stations() = localAgencySource.stations()

    override suspend fun stationsFromIds(ids: List<String>) = localAgencySource.stationsFromIds(ids)

    /**
     * Returns the [Station] from the ids gotten from [StationTownMap]
     */
//    override suspend fun parksFromTown(town: String) =
//        when(val townParkMaps = localAgencySource.townParkMapFromTown(town)){
//            is Results.Success -> {
//                val parkIds = townParkMaps.data.map { it.park }
//                localAgencySource.parksFromIds(parkIds)
//            }
//            is Results.Failure -> {
//                throw townParkMaps.exception
//            }
//        }

    /**
     * Returns the [Station] from the ids gotten from [StationTownMap]
     */
//    override suspend fun town(station: String) = when(val townParkMaps = localAgencySource.townParkMapFromTown(station)){
//        is Results.Success -> {
//            val townIds = townParkMaps.data.map { it.town }
//            localUnivSource.townsFromIds(townIds)
//        }
//        is Results.Failure -> {
//            throw townParkMaps.exception
//        }
//    }
}