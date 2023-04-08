package tech.xken.tripbook.data.sources.agency

import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.*

interface AgencyDataSource {

//    suspend fun stationScannerMapsFromStation(station: String)
    suspend fun stationJobsFromIds(station: String, ids: List<String>): Results<List<StationJob>>
    suspend fun stationJobs(station: String): Results<List<StationJob>>
    fun observeStationJobs(station: String): Flow<Results<List<StationJob>>>
    suspend fun saveStationJobs(stationJobs: List<StationJob>)
    suspend fun scanners(agency: String): Results<List<Scanner>>
    suspend fun scannersFromIds(ids: List<String>): Results<List<Scanner>>
    fun observeStationScannerMaps(station: String): Flow<Results<List<StationScannerMap>>>
    
    suspend fun stationScannerMaps(station: String, scanners: List<String>): Results<List<StationScannerMap>>
    
    suspend fun saveStationScannerMaps(maps: List<StationScannerMap>)
    suspend fun deleteStationScannerMap(station: String, scanners: List<String>)
    suspend fun saveStations(stations: List<Station>)
    suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>)
    fun observeStationTownMaps(station: String): Flow<Results<List<StationTownMap>>>
    suspend fun stationTownMaps(station: String): Results<List<StationTownMap>>
    suspend fun stations(): Results<List<Station>>
    suspend fun stationsFromIds(ids: List<String>): Results<List<Station>>
    fun observeStationsFromIds(ids: List<String>): Flow<Results<List<Station>>>

    suspend fun deleteStationTownMaps(station: String, towns: List<String>)

    suspend fun saveStationLocation(id: String, lat: Double, lon: Double)
}