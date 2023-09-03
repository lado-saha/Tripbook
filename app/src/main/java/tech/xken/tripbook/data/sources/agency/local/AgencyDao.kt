//package tech.xken.tripbook.data.sources.agency.local
//
//import androidx.room.*
//import androidx.room.OnConflictStrategy.Companion.REPLACE
//import kotlinx.coroutines.flow.Flow
//import tech.xken.tripbook.data.models.*
//
//@Dao
//interface AgencyDao {
//    /**
//     * gets all scanner's associated permissions
//     */
//    @Query("SELECT * FROM StationJobs WHERE station = :station AND jobId IN (:ids)")
//    fun stationJobsFromIds(station: String, ids: List<String>): List<StationJob>
//
//    @Query("SELECT * FROM StationJobs WHERE station = :station")
//    fun stationJobs(station: String): List<StationJob>
//
//    @Query("SELECT * FROM StationJobs WHERE station = :station")
//    fun observeStationJobs(station: String): Flow<List<StationJob>>
//
//    @Insert
//    fun saveStationJobs(stationJobs: List<StationJob>)
//
//    @Query("SELECT * FROM Scanners WHERE agency_id = :agency")
//    fun scannersFromAgency(agency: String): List<Scanner>
//
//    @Query("SELECT * FROM Scanners WHERE booker_id IN (:ids)")
//    fun scannersFromIds(ids: List<String>): List<Scanner>
//
//    @Query("SELECT * FROM StationScannerMaps WHERE station = :station")
//    fun observeStationScannerMaps(station: String): Flow<List<StationScannerMap>>
//
//    @Query("SELECT * FROM StationScannerMaps WHERE station = :station")
//    fun stationScannerMaps(station: String): List<StationScannerMap>
//
//    @Insert
//    fun saveStationScannerMaps(maps: List<StationScannerMap>)
//
//    @Query("DELETE FROM StationScannerMaps WHERE station = :station AND scanner IN (:scanners)")
//    fun deleteStationScannerMap(station: String, scanners: List<String>)
//
//    /**
//     * Stations
//     */
//    @Insert(onConflict = REPLACE)
//    fun saveStations(stations: List<Station>)
//
//    @Query("UPDATE Stations SET lat = :lat, lon = :lon WHERE id = :id")
//    fun saveStationsLocation(id: String, lat: Double, lon: Double)
//
//    @Query("DELETE FROM StationTownMaps WHERE station = :station AND town IN (:towns)")
//    fun deleteStationTownMaps(station: String, towns: List<String>)
//
//    @Query("SELECT * FROM Stations WHERE id IN (:ids)")
//    fun observeStationsFromIds(ids: List<String>): Flow<List<Station>>
//
//    /**
//     * Associates [Station] to [Town] by their id
//     */
//    @Insert(onConflict = REPLACE)
//    fun saveStationTownMaps(stationTownMaps: List<StationTownMap>)
//
//    @Query("SELECT * FROM StationTownMaps WHERE station = :station")
//    fun observeStationTownMaps(station: String): Flow<List<StationTownMap>>
//
//    @Query("SELECT * FROM StationTownMaps WHERE station = :station")
//    fun stationTownMaps(station: String): List<StationTownMap>
//
//    @Query("SELECT * FROM Stations WHERE id IN (:ids)")
//    fun stationsFromIds(ids: List<String>): List<Station>
//
//    @Query("SELECT * FROM Stations")
//    fun stations(): List<Station>
//}