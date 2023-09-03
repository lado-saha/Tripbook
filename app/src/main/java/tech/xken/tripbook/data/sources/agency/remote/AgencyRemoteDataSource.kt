//package tech.xken.tripbook.data.sources.agency.remote
//
//import kotlinx.coroutines.flow.Flow
//import tech.xken.tripbook.data.models.Results
//import tech.xken.tripbook.data.models.Scanner
//import tech.xken.tripbook.data.models.agency.Station
//import tech.xken.tripbook.data.models.agency.StationJob
//import tech.xken.tripbook.data.models.agency.StationScannerMap
//import tech.xken.tripbook.data.models.agency.StationTownMap
//import tech.xken.tripbook.data.sources.agency.AgencyDataSource
//
//object AgencyRemoteDataSource: AgencyDataSource {
//    override suspend fun stationJobsFromIds(
//        station: String,
//        ids: List<String>
//    ): Results<List<StationJob>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun stationJobs(station: String): Results<List<StationJob>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun observeStationJobs(station: String): Flow<Results<List<StationJob>>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveStationJobs(stationJobs: List<StationJob>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun scanners(agency: String): Results<List<Scanner>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun scannersFromIds(ids: List<String>): Results<List<Scanner>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun observeStationScannerMaps(station: String): Flow<Results<List<StationScannerMap>>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun stationScannerMaps(
//        station: String,
//        scanners: List<String>
//    ): Results<List<StationScannerMap>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveStationScannerMaps(maps: List<StationScannerMap>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun deleteStationScannerMap(station: String, scanners: List<String>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveStations(stations: List<Station>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveStationTownMaps(stationTownMaps: List<StationTownMap>) {
//        TODO("Not yet implemented")
//    }
//
//    override fun observeStationTownMaps(station: String): Flow<Results<List<StationTownMap>>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun stationTownMaps(station: String): Results<List<StationTownMap>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun stations(): Results<List<Station>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun stationsFromIds(ids: List<String>): Results<List<Station>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun observeStationsFromIds(ids: List<String>): Flow<Results<List<Station>>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun deleteStationTownMaps(station: String, towns: List<String>) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun saveStationLocation(id: String, lat: Double, lon: Double) {
//        TODO("Not yet implemented")
//    }
//}