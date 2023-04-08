package tech.xken.tripbook.data.sources.agency

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.*

interface AgencyRepository {
    /**
     * @param agency is the scanner's employer
     */
    suspend fun scanners(
        agency: String,
        station: String,
        getBookers: Boolean,
        getJobs: Boolean,
    ): Results<Map<String, Scanner>>

    suspend fun scannersFromIds(
        ids: List<String>,
        station: String,
        getBookers: Boolean,
        getJobs: Boolean,
    ): Results<Map<String, Scanner>>

    suspend fun saveStationJobs(stationJobs: List<StationJob>)
    fun observeStationJobs(station: String): Flow<Results<List<StationJob>>>
    suspend fun stationJobsFromIds(station: String, ids: List<String>): Results<List<StationJob>>
    suspend fun stationJobs(station: String): Results<List<StationJob>>
    fun observeStationScannerMaps(station: String): Flow<Results<List<StationScannerMap>>>
    suspend fun stationScannerMaps(
        station: String,
        scanners: List<String>,
    ): Results<List<StationScannerMap>>
    suspend fun saveStationScannerMaps(maps: List<StationScannerMap>)
    suspend fun deleteStationScannerMap(station: String, scanners: List<String>)
    fun observeStationsFromIds(ids: List<String>): Flow<Results<List<Station>>>
    suspend fun saveStations(stations: List<Station>)
    suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>)
    suspend fun deleteStationTownMaps(station: String, towns: List<String>)
    fun observeStationTownMaps(station: String): Flow<Results<List<StationTownMap>>>
    suspend fun stationTownMaps(station: String): Results<List<StationTownMap>>
    suspend fun stations(): Results<List<Station>>
    suspend fun stationsFromIds(ids: List<String>): Results<List<Station>>
    suspend fun saveStationLocation(id: String, lat: Double, lon: Double)

}